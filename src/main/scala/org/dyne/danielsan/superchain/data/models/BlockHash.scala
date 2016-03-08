package org.dyne.danielsan.superchain.data.models

import argonaut._, Argonaut._

/**
  * Created by dan_mi_sun on 27/02/2016.
  */

/*
$ bitcoin-cli getblock 1

00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048

*/

object BlockHashImplicitConversion {

  case class BlockHash(blockHash: String)

  implicit val blockHashCodec: CodecJson[BlockHash] =
    casecodec1(BlockHash.apply, BlockHash.unapply)("blockhash")

}