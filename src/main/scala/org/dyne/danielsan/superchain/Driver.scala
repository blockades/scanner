package org.dyne.danielsan.superchain

import org.dyne.danielsan.superchain.client.BitcoinClient
import org.dyne.danielsan.superchain.data.cassandra.repositories.ChainDatabase
import org.json4s.DefaultFormats

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
  * Created by dan_mi_sun on 13/03/2016.
  *
  */
object Driver {

  def main(args: Array[String]) {

    implicit val formats = DefaultFormats

//    implicit val space = ChainDatabase.space
//    implicit val session = ChainDatabase.session
//
//    Await.result(ChainDatabase.autocreate().future, 10 seconds)

    val client = new BitcoinClient
    var a = 10
//    for (a <- 1 to 10000) {

      val t = client.decodeRawTransaction(a)


      println(t)
//      val operation = ChainDatabase.insertBlock(block)
//      Await.result(operation, 10.seconds)

//    }

//    println("Sample ended")
//    System.exit(0)

  }
}
