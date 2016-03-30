package org.dyne.danielsan.superchain.data.models

/**
  * Created by dan_mi_sun on 30/03/2016.
  */

case class ScriptPubKey(hex: String,
                        asm: String,
                        `type`: String,
                        reqSigs: Int,
                        addresses: List[String])
