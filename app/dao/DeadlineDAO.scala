package dao

import java.sql.Timestamp
import javax.inject.Inject

import models.Deadline
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class DeadlineDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val query = TableQuery[DeadlinesTable]

  def setup: Future[Unit] = db.run(query.schema.create)

  def insert(deadline: Deadline): Future[Long] = db.run(query.returning(query.map(_.id)) += deadline)

  def get(id: Long): Future[Option[Deadline]] = db.run(query.filter(_.id === id).result).map(_.headOption)

  private class DeadlinesTable(tag: Tag) extends Table[Deadline](tag, "DEADLINE") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def title = column[String]("TITLE")

    def description = column[Option[String]]("DESCRIPTION")

    def date = column[Timestamp]("DATE")

    override def * = (id.?, title, description, date) <>((DeadlinesTable.colToObj _).tupled, DeadlinesTable.objToCol)
  }

  private object DeadlinesTable {
    def colToObj(id: Option[Long], title: String, desc: Option[String], date: Timestamp) = {

      Deadline(id, title, desc, new DateTime(date.getTime))
    }

    def objToCol(deadline: Deadline) = {
      Some(deadline.id, deadline.title, deadline.description, new Timestamp(deadline.date.getMillis))
    }
  }

}
