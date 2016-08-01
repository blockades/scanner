package org.dyne.danielsan.openblockchain.http.controllers.api.swaggerdocs

import org.dyne.danielsan.openblockchain.data.entity.{Transaction, Block}
import org.scalatra.swagger.{StringResponseMessage, SwaggerSupport}

/**
  * Created by dan_mi_sun on 22/05/2016.
  */
trait TransactionsControllerDocs extends SwaggerSupport {

  val applicationDescription = "OpenBlockchain API"
  val getTransactions =
    (apiOperation[List[Transaction]]("getTransactions")
      summary "Shows all Transactions"
      notes "Currently shows all transactions, with no possibility of filtering them. TODO: add filtering functionality.")

  val getTransaction =
    (apiOperation[Transaction]("getTransaction")
      summary "Shows a transaction"
      notes "Retrieves a transaction by its txid."
      parameters
      pathParam[String]("id").description("ID of the transaction that needs to be fetched").required
      responseMessage StringResponseMessage(200, "Transaction retrieved"))
}
