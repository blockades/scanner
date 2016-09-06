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
    val block = client.getBlock(id)

    println(block)
    Await.result(ChainDatabase.insertBlock(block), 10.seconds)

    val transactions = client.getTransactions(block)
    transactions.foreach { tx =>
      println(tx)
      Await.result(ChainDatabase.insertTransaction(tx), 10.seconds)
    }

    // new line between blocks
    println()

    if (block.nextblockhash.isDefined) {
      scanBlock(block.height + 1)
    }
  }

}
