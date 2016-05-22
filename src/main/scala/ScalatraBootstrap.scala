import javax.servlet.ServletContext
import org.dyne.danielsan.openblockchain.data.database.ChainDatabase
import org.dyne.danielsan.openblockchain.http.controllers.api.{BlocksController, ChartsController, TransactionsController}
import org.scalatra._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global

class ScalatraBootstrap extends LifeCycle {

  //  implicit val swagger = new SuperChainSwagger

  //Cassandra's initialisation code
  implicit val keySpace = ChainDatabase.space
  implicit val session = ChainDatabase.session

  override def init(context: ServletContext) {

    Await.ready(ChainDatabase.autocreate().future(), 3 seconds)

    context.mount(new ChartsController, "/api/charts")
    context.mount(new BlocksController, "/api/blocks")
    context.mount(new TransactionsController, "/api/transactions")


  }
}