package org.dyne.danielsan.openblockchain.data.entity

/**
  * Created by dan_mi_sun on 12/05/2016.
  */
case class Transaction(txid: String,
                       hex: String,
                       size: Option[Int],
                       version: Int,
                       locktime: Long,
                       vin: List[Vin],
                       vout: List[Vout],
                       blockhash: String,
                       confirmations: Int,
                       time: Long,
                       blocktime: Long,
                       is_op_return: Option[Boolean] // not part of blockchain
                      ) {

  def hasOpReturnVout = vout.exists(_.scriptPubKey.asm.contains("OP_RETURN"))

}
