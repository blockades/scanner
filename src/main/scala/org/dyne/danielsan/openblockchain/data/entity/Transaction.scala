package org.dyne.danielsan.openblockchain.data.entity

/**
  * Created by dan_mi_sun on 12/05/2016.
  */
case class Transaction(txid: String,
                       version: Int,
                       locktime: Int,
                       vin: List[Vin],
                       vout: List[Vout]
                      )