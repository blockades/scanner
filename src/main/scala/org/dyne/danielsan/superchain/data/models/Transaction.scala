package org.dyne.danielsan.superchain.data.models

import argonaut._, Argonaut._

/**
  * Created by dan_mi_sun on 27/02/2016.
  */

  /*
  // The following just me trying to figure out what fields constitute a Transaction
  // This is just a 'spreadout' version of the output gathered from bitcoin-cli getrawtransaction

  {"blockhash":"00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048",
    "blocktime":1231469665,
    "hex":"01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0704ffff001d0104ffffffff0100f
    2052a0100000043410496b538e853519c726a2c91e61ec11600ae1390813a627c66fb8be7947be63c52da7589379515d4e0a604f8141781e622947
      21166bf621e73a82cbf2342c858eeac00000000",
    "confirmations":269439,
    "txid":"0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098",
    "vout":[
    {"value":50.00000000,
    "n":0,
    "scriptPubKey":
    {"hex":"410496b538e853519c726a2c91e61ec11600ae1390813a627c66fb8be7947be63c52da7589379515d4e0a604f8141781e62294721166
    bf621e73a82cbf2342c858eeac",
    "asm":"0496b538e853519c726a2c91e61ec11600ae1390813a627c66fb8be7947be63c52da7589379515d4e0a604f8141781e62294721166bf6
    21e73a82cbf2342c858ee OP_CHECKSIG",
    "type":"pubkey",
    "reqSigs":1,
    "addresses":["12c6DSiU4Rq3P4ZxziKxzrL5LmMBrzjrJX"]}}],
    "version":1,
    "vin":[{"coinbase":"04ffff001d0104","sequence":4294967295}],
    "time":1231469665,
    "locktime":0
    }
    */

 // The following here is using this is as a template: http://lollyrock.com/articles/scala-implicit-conversion/
 // First attempt is to get something down to try and figure out if this is the correct direction

  object ImplicitConversion {

    case class Vout (value: Float,n: Int,scriptpubkey: ScriptPubKey)
    case class ScriptPubKey (hex: String,asm: String,typetx: String,reqsigs: Int,addresses: Any)
    case class Vin (coinbase: String,sequence: Int)
    case class Transaction (blockhash: String,blocktime: Long,hex: String,confirmations:Int,txid: String,vout: Vout,
                            version: Int,vin: Vin, time: Int, locktime: Int)

   // implicit conversion with argonaut
   implicit def TransactionEncodeJson: EncodeJson[Transaction] =
   EncodeJson((t: Transaction) =>
     ("blockhash" := t.blockhash) ->:
     ("blocktime" := t.blocktime) ->:
     ("hex" := t.hex) ->:
     ("confirmations" := t.confirmations) ->:
     ("txid" := t.txid) ->:
     ("vout" := Json (
       ("value" := t.vout.value),
       ("scriptpubkey" := Json (
         ("hex" := t.vout.scriptpubkey.hex),
         ("asm" := t.vout.scriptpubkey.asm),
         ("typetx" := t.vout.scriptpubkey.typetx),
         ("reqsigs" := t.vout.scriptpubkey.reqsigs),
         ("addresses" := t.vout.scriptpubkey.addresses)
       )
       ) ->: jEmptyObject)
       )
       ) ->: jEmptyObject)
     ("version" := t.version) ->:
     ("vin" := Json (
       ("coinbase" := t.vin.coinbase)
       ("sequence" := t.vin.sequence)

     ) ->: jEmptyObject)
       ("time" := t.time)
     ("locktime" := t.locktime)
   )

   implicit def TransactionDecodeJson: DecodeJson[Transaction] =
   DecodeJson(c => for {

     blockhash <- (c --\ "blockhash").as[String]
     blocktime <- (c --\ "time").as[Long]
     hex <- (c --\ "hex").as[String]
     confirmations <- (c --\ "confirmations").as[Int]
     txid <- (c --\ "txid").as[String]
     vout <- (c --\ "vout").as[Json]
     version <- (c --\ "version").as[Int]
     vin <- (c --\ "vin").as[Json]
     time <- (c --\ "time").as[Int]
     locktime <- (c --\ "locktime").as[Int]

     // extract data from vout
     value <- (vout.acursor --\ "value").as[Float]
     scriptpubkey <- (vout.acursor --\ "scriptpubkey").as[Json]

     // extract data from scriptpubkey
     hex <- (scriptpubkey.acursor --\ "hex").as[String]
     asm <- (scriptpubkey.acursor --\ "asm").as[String]
     typetx <- (scriptpubkey.acursor --\ "typetx").as[String]
     reqsigs <- (scriptpubkey.acursor --\ "reqsigs").as[Int]
     addresses <- (scriptpubkey.acursor --\ "addresses").as[Any]

     // extract data from vin
     coinbase <- (vin.acursor --\ "coinbase").as[String]
     sequence <- (vin.acursor --\ "sequence").as[Int]

   } yield Transaction(blockhash, blocktime, hex, confirmations, txid, Vout(value, ScriptPubKey(hex, asm, typetx, reqsigs,
     addresses)), version, Vin(coinbase, sequence), time, locktime)

  }

}