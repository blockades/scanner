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

    Await.result(ChainDatabase.autocreate().future, 10 seconds)

    val client = new BitcoinClient

    for (a <- 168 to 231730) {

      val t = client.decodeRawTransaction(a)
      println("Transaction: " + t)
      t.map( transaction => Await.result((ChainDatabase.insertTransaction(transaction)), 10.seconds))

      val b = client.getBlockForId(a)
       println("Block: " + b)
      val operationB = ChainDatabase.insertBlock(b)
      Await.result(operationB, 10.seconds)

      val btc = client.updateBlockTransactionCount(a)
      println(s"BlockTansactionCount: $btc")
      val operationBTC = ChainDatabase.saveOrUpdateBlockTransactionCount(btc)
      Await.result(operationBTC, 10.seconds)

    }

    println("Sample ended")
    System.exit(0)
  }
}