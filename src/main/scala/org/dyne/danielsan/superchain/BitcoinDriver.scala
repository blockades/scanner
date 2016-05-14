package org.dyne.danielsan.superchain

import org.dyne.danielsan.superchain.client.BitcoinClient
import org.dyne.danielsan.superchain.data.database.ChainDatabase
import org.json4s.DefaultFormats

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by dan_mi_sun on 14/05/2016.
  */
class BitcoinDriver {

  def main(args: Array[String]) {

    implicit val formats = DefaultFormats

    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session

    Await.result(ChainDatabase.autocreate().future, 10 seconds)

    //Known limitation here is that the scanner is not dynamic or automatic
    //It is also not fault tolerant with regards to getting to the end of blocks
    //echo `bitcoin-cli getblockcount 2>&1`/`wget -O - http://blockchain.info/q/getblockcount 2>/dev/null`

    val client = new BitcoinClient

    for (a <- 1 to 1000) {

      val t = client.decodeRawTransaction(a)
      println("Transaction: " + t)
      val operationT = ChainDatabase.insertTransaction(t)
      Await.result(operationT, 10.seconds)

      val b = client.getBlockForId(a)
      println("Block: " + b)
      val operationB = ChainDatabase.insertBlock(b)
      Await.result(operationB, 10.seconds)

    }


    println("Sample ended")
    System.exit(0)
  }

}
