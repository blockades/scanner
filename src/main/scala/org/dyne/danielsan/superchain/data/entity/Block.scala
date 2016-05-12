package org.dyne.danielsan.superchain.data.entity

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
                 previousblockhash: String,
                 nextblockhash: String)
