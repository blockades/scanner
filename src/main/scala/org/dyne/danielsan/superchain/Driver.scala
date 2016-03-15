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
  * TODO FOR CODING
  * What needs to happen now is to upgrade to the latest phantom DSL
  * This will require looking through: http://outworkers.com/blog/post/a-series-on-phantom-part-1-getting-started-with-phantom
  * Once I have updated this I then need to make sure that I can still insert
  * Once I have done this I then need to get working with the Transaction class
  * Once I have done this I then need to reassess approach for MVP
  * Also need to consider more thorough testing - what is the minimum amount
  * Pie in the sky is to also use spark - but this should come after the MVP (data + visualisation + sonification?)
  *
  * SUPERVISOR QUESTIONS
  * Speak with David about this.
  * Plan atm is to pull all of the blockchain data out and then display as a graph, polling for new blockchain data.
  * Is this ok?
  *
  * Questions about submission format - how to provide it in such a way that it works?
  * - how detailed do I need to go in terms of mocking etc
  * - what happens if I submit early?
  * - what is the submission due date?
  * - how much feedback can I get?
  *
  */
object Driver extends App {
  def main(implicit session: Session): Unit = {
    implicit val formats = DefaultFormats


    implicit val space = ChainDatabase.space

    Await.result(ChainDatabase.autocreate().future, 10 seconds)


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



    val connect: Try[Session] = Try(ChainDatabase.session)

    connect match {
      case Success(session) => main(session)
      case Failure(error) => println(s"KO: ${error}")
    }


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
