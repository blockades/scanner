package org.dyne.danielsan.superchain


import org.dyne.danielsan.superchain.data.cassandra.repositories.ChainRepository
import org.dyne.danielsan.superchain.data.models.ChainEntry

import scala.concurrent.Await
import scala.concurrent.duration._

object App {
  def main(args: Array[String]) {

    implicit val keySpace = ChainRepository.keySpace
    implicit val session = ChainRepository.session

    // Create the table if it doesn't already exist. We use Await because
    // we need to block here, or the future won't have time to execute.
    Await.ready(
      ChainRepository.create.ifNotExists()
        .future(), 3.seconds)

    val chainEntry = ChainEntry(
      "id",
      100,
    "Hello"
    )



    println("Inserting a chainEntry...")
    ChainRepository.insertNewRecord(chainEntry)

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
    session.getCluster.closeAsync()

    println("And now we take a million years to close...")
  }
}
