import javax.servlet.ServletContext

import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.dyne.danielsan.openblockchain.http.controllers.api.{BlocksController, ChartsController, TransactionsController}
import org.dyne.danielsan.openblockchain.http.controllers.{ApiDocsController, OpenBlockchainSwagger}
import org.scalatra._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ScalatraBootstrap extends LifeCycle {

  implicit val swagger = new OpenBlockchainSwagger

  //Cassandra's initialisation code
  implicit val keySpace = ChainDatabase.space
  implicit val session = ChainDatabase.session

  override def init(context: ServletContext) {

    Await.result(ChainDatabase.autocreate().future(), 10 seconds)

    context.mount(new ApiDocsController, "/api-docs/")
    context.mount(new ChartsController, "/api/charts")
    context.mount(new BlocksController, "/api/blocks")
    context.mount(new TransactionsController, "/api/transactions")


  }
}