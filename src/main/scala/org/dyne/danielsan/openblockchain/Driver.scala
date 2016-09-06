package org.dyne.danielsan.openblockchain

import org.dyne.danielsan.openblockchain.client.BitcoinClient
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.dyne.danielsan.openblockchain.data.model.BlockTransactions
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

      val block = client.getBlockForId(id)
      println(block)
      val blockInsertOp = ChainDatabase.insertBlock(block)
      Await.result(blockInsertOp, 10.seconds)

      val txs = client.getRawTransaction(id)
      println(txs)
      txs.foreach { tx =>
        val txInsertOp = ChainDatabase.insertTransaction(tx)
        Await.result(txInsertOp, 10.seconds)

        tx.vout.foreach { vout =>
          val isOpReturnTx = vout.scriptPubKey.asm.contains("OP_CHECKSIG")
          val btx = BlockTransactions(block.hash, tx.txid, vout.n, isOpReturnTx)
          println(btx)
          val btInsertOp = ChainDatabase.insertBlockTransaction(btx)
          Await.result(btInsertOp, 10.seconds)
        }
      }

      val btc = client.updateBlockTransactionCount(id)
      println(btc)
      val updateBtcOp = ChainDatabase.saveOrUpdateBlockTransactionCount(btc)
      Await.result(updateBtcOp, 10.seconds)

      val op_return_count = client.updateBlockOpReturnTransactionCount(id)
      println(op_return_count)
      val operationOpReturn = ChainDatabase.saveOrUpdateBlockOpReturnTransactionCount(op_return_count)
      Await.result(operationOpReturn, 10.seconds)

      // new line between results
      println()

    }

    println("Sample ended")
    System.exit(0)
  }

}
