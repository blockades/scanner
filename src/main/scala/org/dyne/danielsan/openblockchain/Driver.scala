package org.dyne.danielsan.openblockchain

import java.util.Calendar

import org.dyne.danielsan.openblockchain.client.BitcoinClient
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Random, Try}

/**
  * Created by dan_mi_sun on 13/03/2016.
  */
object Driver {

  val client = new BitcoinClient

  def main(rawArgs: Array[String]) {
    val args = parseArgs(rawArgs)
    println(args)

    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session
    Await.result(ChainDatabase.autocreate().future, 10.seconds)

    while (true) {
      val blockCount = Try(client.getBlockCount()).getOrElse(0)
      var blocksPerScan = blockCount
      var starth = args.starth

      if (args.scale > 0) {
        blocksPerScan = Math.floor(blockCount.toDouble / args.scale).toInt
        starth = Random.nextInt(args.scale) * blocksPerScan + 1
      }

      if (starth < 1) {
        starth = 1
      }

      println(s"$getTimeString scanning $blocksPerScan blocks from height $starth...")
      scanBlock(starth, args.starth + blocksPerScan)

      println(s"$getTimeString pausing 1 hour...")
      wait(1.hour.toMillis)
    }
  }

  def scanBlock(height: Int, maxHeight: Int): Unit = {
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

    Await.result(ChainDatabase.insertBlock(block), 10.seconds)
    Await.result(ChainDatabase.insertTransactions(transactions), 10.seconds)

    println(s"$getTimeString saved block ${block.height} with ${transactions.length} transactions")

    if (block.nextblockhash.isDefined && block.nextblockhash.get.nonEmpty) {
      scanBlock(block.height + 1, maxHeight)
    }
  }

  def getTimeString = {
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val min = cal.get(Calendar.MINUTE)
    val sec = cal.get(Calendar.SECOND)
    val ms = cal.get(Calendar.MILLISECOND)

    var time = ""
    time += hour
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

  def parseArgs(args: Array[String]): Args = {
    var starth = -1
    var scale = -1

    for (arg <- args) {
      if (arg.startsWith("--scale=")) {
        val scaleOpt = Try(arg.stripPrefix("--scale=").toInt)
        if (scaleOpt.isSuccess) {
          scale = scaleOpt.get
        }
      }

      if (arg.startsWith("--starth=")) {
        val starthOpt = Try(arg.stripPrefix("--starth=").toInt)
        if (starthOpt.isSuccess) {
          starth = starthOpt.get
        }
      }
    }

    Args(scale, starth)
  }

  case class Args(scale: Int, starth: Int)

}
