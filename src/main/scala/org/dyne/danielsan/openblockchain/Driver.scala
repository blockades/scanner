package org.dyne.danielsan.openblockchain

import java.util.Calendar

import org.dyne.danielsan.openblockchain.client.BitcoinClient
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.dyne.danielsan.openblockchain.data.entity.{Block, Transaction}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by dan_mi_sun on 13/03/2016.
  */
object Driver {

  val client = new BitcoinClient

  val cacheBlocks = ListBuffer[Block]()
  val cacheTxs = ListBuffer[Transaction]()

  def main(args: Array[String]) {
    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session

    Await.result(ChainDatabase.autocreate().future, 30.seconds)

    scanLoop(args)
  }

  def scanLoop(args: Array[String]): Unit = {
    while (true) {
      try {
        val blockCount = Try(client.getBlockCount()).getOrElse(0)
        val heightFrom = Try(args(0).toInt).getOrElse(1)
        val heightTo = Try(args(1).toInt).getOrElse(blockCount)

        println(s"$getTimeString blockCount=$blockCount")
        println(s"$getTimeString scanning from height $heightFrom to $heightTo...")

        if (heightFrom > blockCount) {
          println(s"$getTimeString skipping, heightFrom > blockCount")
        } else if (heightTo > blockCount) {
          println(s"$getTimeString skipping, heightTo > blockCount")
        } else if (heightFrom > heightTo) {
          println(s"$getTimeString skipping, heightFrom > heightTo")
        } else {
          scanBlocks(heightFrom, heightTo)
        }

        println(s"$getTimeString pausing 10 minutes...")
        this.synchronized {
          wait(10.minutes.toMillis)
        }
      } catch {
        case x: Throwable =>
          println("!!! CAUGHT EXCEPTION !!! scanLoop")
          x.printStackTrace()

          println("will restart after 1 minute")
          this.synchronized {
            wait(1.minute.toMillis)
          }
      }
    }
  }

  def scanBlocks(fromHeight: Int, maxHeight: Int): Unit = {
    for (height <- fromHeight to maxHeight) {
      try {
        if (cacheBlocks.length > 10000 || cacheTxs.length > 10000 || height >= maxHeight) {
          Await.result(ChainDatabase.insertBlocks(cacheBlocks.toList), 10.minutes)
          Await.result(ChainDatabase.insertBlocksByHash(cacheBlocks.toList.map(_.toBlockByHash)), 10.minutes)
          Await.result(ChainDatabase.insertTransactions(cacheTxs.toList), 10.minutes)

          println(s"$getTimeString saved ${cacheBlocks.length} blocks and ${cacheTxs.length} transactions")

          cacheBlocks.clear()
          cacheTxs.clear()
        }

        if (height >= maxHeight) {
          return
        }

        val rawBlock = client.getBlock(height)
        val transactions = client.getTransactions(rawBlock)
          .filter(_ != null)
          .map(tx => tx.copy(
            is_op_return = Some(tx.hasOpReturnVout),
            time = tx.time * 1000, // s -> ms
            locktime = tx.locktime * 1000, // s -> ms
            blocktime = tx.blocktime * 1000 // s -> ms
          ))

        val blockIsOpReturn = transactions.exists(_.is_op_return.get)
        val block = rawBlock.copy(
          is_op_return = Some(blockIsOpReturn),
          time = rawBlock.time * 1000 // s -> ms
        )

        cacheBlocks += block
        cacheTxs ++= transactions

        println(s"$getTimeString cached block $height with ${transactions.length} transactions")

        if (block.nextblockhash.isEmpty || block.nextblockhash.get.isEmpty) {
          println(s"$getTimeString stop h=$height block.nextblockhash=${block.nextblockhash}")
          return
        }
      } catch {
        case x: Throwable =>
          println("!!! CAUGHT EXCEPTION !!! scanBlock")
          x.printStackTrace()

          println("will continue to the next block after 1 minute")
          this.synchronized {
            wait(1.minute.toMillis)
          }
      }
    }
  }

  def getTimeString = {
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val min = cal.get(Calendar.MINUTE)
    val sec = cal.get(Calendar.SECOND)
    val ms = cal.get(Calendar.MILLISECOND)

    var time = ""
    time += padLeft(hour)
    time += ":"
    time += padLeft(min)
    time += ":"
    time += padLeft(sec)
    time += "."
    time += padRight(ms)
    time
  }

  def padLeft(num: Int): String = {
    if (num < 10) {
      "0" + num
    } else {
      num.toString
    }
  }

  def padRight(num: Int): String = {
    var s = num.toString
    while (s.length < 3) {
      s += "0"
    }
    s
  }

}
