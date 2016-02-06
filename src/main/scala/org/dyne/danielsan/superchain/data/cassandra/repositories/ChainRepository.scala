package org.dyne.danielsan.superchain.data.cassandra.repositories

import java.util.UUID

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.iteratee.Iteratee
import org.dyne.danielsan.superchain.data.cassandra.init.CassandraConnector
import org.dyne.danielsan.superchain.data.models.ChainEntry

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 05/02/2016.
  */

object ChainRepository extends ChainRepository with CassandraConnector{

  def insertNewRecord(chainEntry: ChainEntry) = {
    val newId = UUID.randomUUID().toString
    insert.value(_.id, newId)
      .value(_.txCount, chainEntry.txCount)
      .value(_.blockHeader, chainEntry.blockHeader)
      .future()
    getById(newId)
  }

  def getById(id: String): Future[Option[ChainEntry]] = {
    select.where(_.id eqs id).one()
  }

  def listAll = {
    select.fetchEnumerator() run Iteratee.collect()
  }

  def destroy(id: String): Future[ResultSet] = {
    delete.where(_.id eqs id).future()
  }

  def updateChainEntry(chainEntry: ChainEntry) = {
    update.where(_.id eqs chainEntry.id)
      .modify(_.blockHeader setTo chainEntry.blockHeader)
      .and(_.txCount setTo chainEntry.txCount)
      .future()
    getById(chainEntry.id)
  }
}

sealed class ChainRepository extends CassandraTable[ChainRepository, ChainEntry] {

  object id extends StringColumn(this) with PartitionKey[String]

  object txCount extends IntColumn(this)

  object blockHeader extends StringColumn(this)


  // Now the mapping function, transforming a row into a custom type.
  // This is a bit of boilerplate, but it's one time only and very short.
  def fromRow(row: Row): ChainEntry = {
    ChainEntry(
      id(row),
      txCount(row),
      blockHeader(row)
    )
  }
}
