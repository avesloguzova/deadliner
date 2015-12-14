package controllers

import javax.inject.Inject

import dao.DeadlineDAO
import models.Deadline

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Application @Inject()(deadlineDAO: DeadlineDAO) extends Controller {

  val deadlineForm = Form(
    mapping(
      "id" -> ignored[Option[Long]](None),
      "title" -> text(minLength = 4, maxLength = 120),
      "description" -> optional(text),
      "date" -> jodaDate("dd-MM-yyyy HH:mm")
    )(Deadline.apply)(Deadline.unapply)
  )

  def index = Action {
    Ok(views.html.index(deadlineForm))
  }

  def deadlineGet(id: Long) = Action.async {
    deadlineDAO.get(id).map {
      case Some(deadline) =>Ok( views.html.deadline(deadline))
      case None => BadRequest(views.html.index(deadlineForm))
    }
  }

  def deadlinePost = Action.async { implicit request =>
    deadlineForm.bindFromRequest.fold(
      formWithErrors => {
        Future(BadRequest(views.html.index(formWithErrors)))
      },
      deadline => {
        deadlineDAO.insert(deadline).map(id => Redirect(routes.Application.deadlineGet(id)).flashing("success" -> "Deadline created!"))
      }
    )
  }
}



