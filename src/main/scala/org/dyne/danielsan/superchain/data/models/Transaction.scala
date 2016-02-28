package org.dyne.danielsan.superchain.data.models

import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by dan_mi_sun on 27/02/2016.
  */

case class Transaction(id: String){

/*
val btcurl="http://127.0.0.1:8332"

implicit val jsonrpc = new JSONRPC(btcurl, "dave", "suckme") {
  val resp = BitcoinRPC.getrawtransaction ("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098", true)

  println (resp)

  Parse your json (see "Parsing JSON") and extract it into the case class (see "Extracting values"). Name foo,
  bar, and blah in the case class the same as the properties you want to extract. Try to get just one extract working,
  use println to ensure the properties are set correctly after extraction. Then you can try one insert. Once that works,
  you should be able to loop through and do the inserts for the whole chain, and then you're on to the API :).
*/
}