package org.dyne.danielsan.openblockchain

import org.dyne.danielsan.openblockchain.client.BitcoinClient
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.json4s.DefaultFormats

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by dan_mi_sun on 13/03/2016.
  */
object Driver {

  def main(args: Array[String]) {

    implicit val formats = DefaultFormats

    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session

    Await.result(ChainDatabase.autocreate().future, 10.seconds)

    val client = new BitcoinClient

    for (id <- 1 to 5) {

      val txs = client.getRawTransaction(id)
      println("Transaction: " + txs)
      txs
        .map(ChainDatabase.insertTransaction)
        .map(Await.result(_, 10.seconds))

      val block = client.getBlockForId(id)
      println("Block: " + block)
      val blockInsertOp = ChainDatabase.insertBlock(block)
      Await.result(blockInsertOp, 10.seconds)

      val btc = client.updateBlockTransactionCount(id)
      println("BlockTansactionCount: " + btc)
      val updateBtcOp = ChainDatabase.saveOrUpdateBlockTransactionCount(btc)
      Await.result(updateBtcOp, 10.seconds)

      println()

      val op_return_count = client.updateBlockOpReturnTransactionCount(id)
      println("BlockOpReturnTansactionCount: " + op_return_count)
      val operationOpReturn = ChainDatabase.saveOrUpdateBlockOpReturnTransactionCount(op_return_count)
      Await.result(operationOpReturn, 10.seconds)

    }

    println("Sample ended")
    System.exit(0)
  }

}
