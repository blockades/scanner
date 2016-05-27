package org.dyne.danielsan.openblockchain.http.controllers.api.swaggerdocs

import org.dyne.danielsan.openblockchain.data.entity.Block
import org.scalatra.swagger.{StringResponseMessage, SwaggerSupport}

/**
  * Created by dan_mi_sun on 22/05/2016.
  */
trait BlocksControllerDocs extends SwaggerSupport {

  val applicationDescription = "OpenBlockchain API"
  val getBlocks =
    (apiOperation[List[Block]]("getBlocks")
      summary "Shows all Blocks"
      notes "Currently shows all blocks, with no possibility of filtering them. TODO: add filtering functionality. Mock data can be found here: https://github.com/dan-mi-sun/openblockchain/blob/feature/webapp/src/main/resources/mocks/blocks.csv")

  val getBlock =
    (apiOperation[Block]("getBlock")
      summary "Shows a block"
      notes "Retrieves a block by its hash. Mock data can be found here: https://github.com/dan-mi-sun/openblockchain/blob/feature/webapp/src/main/resources/mocks/blocks.csv"
      parameters
      pathParam[String]("id").description("ID of the block that needs to be fetched").required
      responseMessage StringResponseMessage(200, "Block retrieved"))

  val getBlockTransactionCount =
    (apiOperation[Int]("getBlockTransactionCount")
      summary "Shows the number of transactions within a block"
      notes "Retrieves transaction count from block by its hash. Mock data can be found here: https://github.com/dan-mi-sun/openblockchain/blob/feature/webapp/src/main/resources/mocks/block_transaction_counts.csv"
      parameters
      pathParam[String]("id").description("ID of the block that needs to be fetched").required
      responseMessage StringResponseMessage(200, "Block Transaction-count retrieved"))
}
