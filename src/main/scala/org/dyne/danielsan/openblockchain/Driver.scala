package org.dyne.danielsan.openblockchain

import org.dyne.danielsan.openblockchain.client.BitcoinClient
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by dan_mi_sun on 13/03/2016.
  */
object Driver {

  val client = new BitcoinClient

  def main(args: Array[String]) {

    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session

    Await.result(ChainDatabase.autocreate().future, 10.seconds)

    while (true) {
      val startHeight = Try(args(0).toInt).getOrElse(0)

      println(s"scanning from block $startHeight...")
      scanBlock(startHeight)

      println("pausing 1 hour...")
      wait(1.hour.length)
    }
  }

  def scanBlock(id: Int): Unit = {
    val rawBlock = client.getBlock(id)
    val transactions = client.getTransactions(rawBlock)
        .filter(_ != null)
        .map(tx => tx.copy(is_op_return = Some(tx.hasOpReturnVout)))

    val blockIsOpReturn = transactions.exists(_.is_op_return.get)
    val block = rawBlock.copy(is_op_return = Some(blockIsOpReturn))

    Await.result(ChainDatabase.insertBlock(block), 10.seconds)
    Await.result(ChainDatabase.insertTransactions(transactions), 10.seconds)

    println(s"saved block ${block.height} with ${transactions.length} transactions")

    if (block.nextblockhash.isDefined && block.nextblockhash.get.nonEmpty) {
      scanBlock(block.height + 1)
    }
  }

}
