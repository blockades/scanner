package org.dyne.danielsan.superchain

import org.dyne.danielsan.superchain.data.database.ChainDatabase
import org.json4s.DefaultFormats

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}


/**
  * Created by dan_mi_sun on 13/03/2016.
  *
  */
object Driver {

  def main(args: Array[String]) {

    implicit val formats = DefaultFormats

    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session

    Await.result(ChainDatabase.autocreate().future, 10 seconds)

    //Known limitation here is that the scanner is not dynamic or automatic
    //It is also not fault tolerant with regards to getting to the end of blocks
    //echo `bitcoin-cli getblockcount 2>&1`/`wget -O - http://blockchain.info/q/getblockcount 2>/dev/null`

    //          val BTC = client.getTransactionCountFromWithinBlock(a)
    //          println("BlockTransactionCounts" + BTC)
    //          //need to look at adding info from decodeRawTransaction
    //          //might be done through the service
    //          //this is the same as updating to two tables at the same time
    //          val operationBTC = ChainDatabase.insertBlockTransactionCounts(BTC)
    //          Await.result(operationBTC, 10.seconds)
    //    val client = new BitcoinClient
    //
    //    for (a <- 1 to 1000) {
    //
    //      val t = client.decodeRawTransaction(a)
    //      println("Transaction: " + t)
    //      val operationT = ChainDatabase.insertTransaction(t)
    //      Await.result(operationT, 10.seconds)
    //
    //      val b = client.getBlockForId(a)
    //      println("Block: " + b)
    //      val operationB = ChainDatabase.insertBlock(b)
    //      Await.result(operationB, 10.seconds)
    //
    //    }

    //    //List all Blocks
    //    val cblist = ChainDatabase.listAllBlocks
    //    Await.result(cblist, 10.seconds)
    //    println("List of Blocks" + cblist)
    //
    //    //List all Transactions
    //    val txList = ChainDatabase.listAllTransactions
    //    Await.result(txList, 10.seconds)
    //    println("List of Transactions" + txList)

    //    //Get Block by its Hash.
    //    val hash = "000000004da68466ee873c7095c766baf62df93a16df579350e01e7f78911616"
    //    val block = ChainDatabase.getBlockByHash(hash)
    //    Await.result(block, 10.seconds)
    //    println("Block by Hash " + block)
    //    block onComplete {
    //      case Success(s) => println("This is s: " + s.get)
    //      case Failure(f) => println("An error has occured: " + f.getMessage)
    //    }
    //
    //    //Get Transaction by it 's txid

    println("this is the beginning of the block")

    val txid = "a84c57b17fb767870a708f336e1cbf95582ad0fde26ec10195f82189295d073f"
    val tx = ChainDatabase.getTransactionByTxid(txid)
    Await.result(tx, 10.seconds)
    println("Transaction by txid " + tx)
    tx onComplete {
      case Success(s) => println("This is s: " + s.get)
      case Failure(f) => println("An error has occured: " + f.getMessage)
    }


    println("Sample ended")
    System.exit(0)
  }
}