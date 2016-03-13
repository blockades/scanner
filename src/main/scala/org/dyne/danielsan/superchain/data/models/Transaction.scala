package org.dyne.danielsan.superchain.data.models

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
  "time":1231469665S
  "locktime":0
  }
  */

// Put up a question here: http://stackoverflow.com/questions/35747753/complex-encoding-of-multiple-nested-classes-using-scala-argonaut
// Easier to read code here: https://gist.github.com/mbbx6spp/df771b3aaaea90d20dd0


case class Transaction(blockhash: String,
                       blocktime: Long,
                       hex: String,
                       confirmations: Int,
                       txid: String,
                       vout: List[Vout],
                       version: Int,
                       vin: List[Vin],
                       time: Int,
                       locktime: Int)

case class Vout(value: Float,
                n: Int,
                scriptPubKey: ScriptPubKey)

case class ScriptPubKey(hex: String,
                        asm: String,
                        `type`: String,
                        reqSigs: Int,
                        addresses: List[String])

case class Vin(coinbase: String,
               sequence: Int)

