package org.dyne.danielsan.openblockchain.data.entity

/**
  * Created by dan_mi_sun on 11/05/2016.
  */
case class Block(hash: String,
                 confirmations: Int,
                 size: Int,
                 height: Int,
                 version: Int,
                 merkleroot: String,
                 tx: List[String],
                 time: Long,
                 nonce: Long,
                 bits: String,
                 difficulty: Float,
                 chainwork: String,
                 previousblockhash: Option[String],
                 nextblockhash: Option[String],
                 is_op_return: Option[Boolean] // not part of blockchain
                ) {

  def toBlockByHash = {
    BlockByHash(hash, height)
  }

}
