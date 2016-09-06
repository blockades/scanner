package org.dyne.danielsan.openblockchain

import org.dyne.danielsan.openblockchain.client.BitcoinClient
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

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
      println("scanning from block 0...")
      scanBlock(0)

      println("pausing 1 hour...")
      wait(1.hour.length)
    }
  }

  def scanBlock(id: Int): Unit = {
    val rawBlock = client.getBlock(id)
    val transactions = client.getTransactions(rawBlock).filter(_ != null)
    val blockIsOpReturn = transactions.exists(_.vout.exists(_.scriptPubKey.asm.contains("OP_RETURN")))
    val block = rawBlock.copy(is_op_return = Some(blockIsOpReturn))

    Await.result(ChainDatabase.insertBlock(block), 10.seconds)
    Await.result(ChainDatabase.insertTransactions(transactions), 10.seconds)

    println(s"saved block ${block.height} with ${transactions.length} transactions")

    if (block.nextblockhash.isDefined && block.nextblockhash.get.nonEmpty) {
      scanBlock(block.height + 1)
    }
  }

}
