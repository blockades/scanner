package org.dyne.danielsan.superchain.data.entity

/**
  * Created by dan_mi_sun on 12/05/2016.
  */
case class ScriptPubKey(hex: String,
                        asm: String,
                        `type`: String,
                        reqSigs: Int,
                        addresses: List[String])
