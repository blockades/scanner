import javax.servlet.ServletContext
import org.dyne.danielsan.superchain.data.database.ChainDatabase
import org.dyne.danielsan.superchain.http.controllers.api.ChartsController
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
  }
}