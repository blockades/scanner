package org.dyne.danielsan.superchain

import com.datastax.driver.core.Session
import org.dyne.danielsan.superchain.client.BitcoinClient
import org.dyne.danielsan.superchain.data.cassandra.repositories.ChainDatabase
import org.json4s.DefaultFormats

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
  * Created by dan_mi_sun on 13/03/2016.
  *
  */
//object Driver extends App {
  object App {

  //def main(implicit session: Session): Unit = {
  def main(args: Array[String]) {

    implicit val formats = DefaultFormats

    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session

    Await.ready(ChainDatabase.autocreate().future, 10 seconds)

    val client = new BitcoinClient
    var a = 1
    //for (a <- 1 to 10)

      //      val resp = client.getHashForId(a)
      //    val block = client.getBlockForHash("00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048")
      val block = client.getBlockForId(a)

      println(block)
      //Database.insertBlockBatch(block)
      val operation = ChainDatabase.insertBlock(block)
      Await.result(operation, 10.seconds)

      println("Sample ended")
      System.exit(0)



//    val connect: Try[Session] = Try(ChainDatabase.session)
//
//    connect match {
//      case Success(session) => main(session)
//      case Failure(error) => println(s"KO: ${error}")
//    }


    //
    //    client.getBlockChainFromId(1)

    // The aim of the game now is to parse the Transaction and put it in to ChainEntry
    //    // and then subsequently into Cassanadra
    //
    //    val chainEntry = ChainEntry(
    //      "id",
    //      101,
    //    "Hello-btc-server"
    //      //"address"
    //    )
    //ChainRepository.insertNewRecord(resp.)
    //}

    /**
      * This is a mostly unsuccessful attempt to regain control in the sbt shell
      * by releasing the session. As soon as we use the session in this class,
      * e.g. by inserting a new Recipe, the app hangs at exit.
      *
      * Calling session.getCluster.close() or session.getCluster.closeAsync()
      * causes the session to eventually die off (I guess it must be triggering
      * some kind of reaping function somewhere), but it takes about 60 seconds
      * to happen.
      */
    //  session.getCluster.closeAsync()

    //      println("And now we take a million years to close...")

  }
}
