package org.dyne.danielsan.openblockchain

import org.dyne.danielsan.openblockchain.client.BitcoinClient
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.json4s.DefaultFormats

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}
//
//
///**
//  * Created by dan_mi_sun on 13/03/2016.
//  *
//  */
object Driver {

  def main(args: Array[String]) {

    implicit val formats = DefaultFormats

    implicit val space = ChainDatabase.space
    implicit val session = ChainDatabase.session

    Await.result(ChainDatabase.autocreate().future, 10 seconds)
//
//    //The following is like the API for the application. Need to find a sane way of organising
//    //everything so that the layout makes sense. Will likely need to do this at some point during
//    //the Scalatra integration.
//
//    //Known limitation here is that the scanner is not dynamic or automatic
//    //It is also not fault tolerant with regards to getting to the end of blocks
//    //echo `bitcoin-cli getblockcount 2>&1`/`wget -O - http://blockchain.info/q/getblockcount 2>/dev/null`
//
    val client = new BitcoinClient
//
//    /*
//NEXT STEP IS TO TAKE THE JSON OBJECT PARSE IT AND THEN INSERT INTO THE DB
//NEXT STEP IS TO HAVE THIS HAPPEN IN SYNC WITH THE REST OF THE SCANNER
// */
//
//    //    //This gives us the num_transactions wiithin a Block
//    //(hash: String, time: Long, num_transactions: Long) <- this should be in the Bitcoin client
//    //    val BTC = client.getTransactionCountFromWithinBlock(728)
//    //    println("BlockTransactionCounts " + BTC)
//    //    val operationBTC = ChainDatabase.saveOrUpdateBlockTransactionCount(BTC)
//    //    Await.result(operationBTC, 10.seconds)
//
//
//    //    val count = client.getTransactionCountFromWithinBlock(1)
//    //    println("this is the count: " + count)
//
////        val btc = client.updateBlockTransactionCount(2)
////        println(s"this is the btc: $btc")
//
    for (a <- 1 to 100) {

      val t = client.decodeRawTransaction(a)
      println("Transaction: " + t)
      val operationT = ChainDatabase.insertTransaction(t)
      Await.result(operationT, 10.seconds)

      val b = client.getBlockForId(a)
       println("Block: " + b)
      val operationB = ChainDatabase.insertBlock(b)
      Await.result(operationB, 10.seconds)

      val btc = client.updateBlockTransactionCount(a)
      println(s"BlockTansactionCount: $btc")
      val operationBTC = ChainDatabase.saveOrUpdateBlockTransactionCount(btc)
      Await.result(operationBTC, 10.seconds)

    }
//
//    //List all Blocks
//    //        val cblist = ChainDatabase.listAllBlocks
//    //        Await.result(cblist, 10.seconds)
//    //        println("List of Blocks" + cblist)
//    //
//    //    //List all Transactions
//    //    val txList = ChainDatabase.listAllTransactions
//    //    Await.result(txList, 10.seconds)
//    //    println("List of Transactions" + txList)
//
//    //    //Get Block by its Hash.
//    //    val hash = "000000004da68466ee873c7095c766baf62df93a16df579350e01e7f78911616"
//    //    val block = ChainDatabase.getBlockByHash(hash)
//    //    Await.result(block, 10.seconds)
//    //    println("Block by Hash " + block)
//    //    block onComplete {
//    //      case Success(s) => println("This is s: " + s.get)
//    //      case Failure(f) => println("An error has occured: " + f.getMessage)
//    //    }
//    //
//    //    //Get Transaction by it 's txid
//
//    //    println("this is the beginning of the block")
//    //
//    //    val txid = "a84c57b17fb767870a708f336e1cbf95582ad0fde26ec10195f82189295d073f"
//    //    val tx = ChainDatabase.getTransactionByTxid(txid)
//    //    Await.result(tx, 10.seconds)
//    //    println("Transaction by txid " + tx)
//    //    tx onComplete {
//    //      case Success(s) => println("This is s: " + s.get)
//    //      case Failure(f) => println("An error has occured: " + f.getMessage)
//    //    }
//
////    val hash =  "000000004da68466ee873c7095c766baf62df93a16df579350e01e7f78911616"
////    val btc = ChainDatabase.getBlockTransactionCountByHash(hash)
////    Await.result(btc, 10.seconds)
////    println(s"BlockTransactionCountByHash: $btc")
////    btc onComplete {
////      case Success(s) => println("This is s: " + s.get)
////      case Failure(f) => println("An error has occured: " + f.getMessage)
////    }
//
//
    println("Sample ended")
    System.exit(0)
  }
}