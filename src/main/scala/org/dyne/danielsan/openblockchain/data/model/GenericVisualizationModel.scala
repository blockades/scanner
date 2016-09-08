package org.dyne.danielsan.openblockchain.data.model

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.dyne.danielsan.openblockchain.data.entity.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 27/02/2016.
  */
sealed class VisualizationModel extends CassandraTable[ConcreteVisualizationModel, Visualization] {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def fromRow(row: Row): Visualization = {
    Visualization(id(row), period(row), unit(row), data(row))
  }

  object id extends StringColumn(this) with PartitionKey[String]

  object period extends StringColumn(this) with PartitionKey[String]

  object unit extends StringColumn(this) with PartitionKey[String]

  object data extends JsonListColumn[ConcreteVisualizationModel, Visualization, Map[String, Long]](this) {
    override def fromJson(obj: String): Map[String, Long] = {
      parse(obj).extract[Map[String, Long]]
    }

    override def toJson(obj: Map[String, Long]): String = {
      write(obj)
    }
  }

}

abstract class ConcreteVisualizationModel extends VisualizationModel with RootConnector {

  override val tableName = "visualizations"

  def insertNew(visualization: Visualization): Future[ResultSet] = insertNewRecord(visualization).future()

  def insertNewRecord(visualization: Visualization) = {
    insert
      .value(_.id, visualization.id)
      .value(_.period, visualization.period)
      .value(_.unit, visualization.unit)
      .value(_.data, visualization.data)
  }

  def listAll = {
    select.fetch
  }

  def getVisualization(id: String, period: String, unit: String): Future[Option[Visualization]] = {
    select
      .where(_.id eqs id).and(_.period eqs period).and(_.unit eqs unit)
      .one()
  }

}
