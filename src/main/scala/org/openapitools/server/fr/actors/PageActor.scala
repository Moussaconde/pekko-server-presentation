package org.openapitools.server.fr.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.apache.pekko.actor.typed.Behavior
import org.openapitools.server.model.Page

object PageActor {
  sealed trait Command
  case class Back(steps: Option[Int], replyTo: ActorRef[String]) extends Command
  case class Turn(pageNumber: Option[Int], replyTo: ActorRef[String]) extends Command
case class SaveDocument(doc: Seq[Page], replyTo: ActorRef[String]) extends Command
  def apply(): Behavior[Command] =
    Behaviors.setup(context => new PageActor(context))
}

class PageActor(context: ActorContext[PageActor.Command]) extends AbstractBehavior[PageActor.Command](context) {
  context.log.info("Hello PageActor !")
  import PageActor._



/** *******************DB************************
    */
  object DocDB {
    var currenDocument: Seq[Page] = null
    var currentPage: Int = 0
  }

  def saveDocument(doc: Seq[Page]): Unit = {
    DocDB.currenDocument = doc
    print(DocDB.currenDocument)
  }

  def turnPageDocument(pageNumber: Option[Int]): String = {
    if (DocDB.currenDocument == null)
      "DOCUMENT NON DEFINI"
    else if (pageNumber.getOrElse(1) >= DocDB.currenDocument.length)
      "PAGE NON DEFINI"
    else {
      DocDB.currentPage += pageNumber.getOrElse(1)
      if (
        DocDB.currentPage >= 0 && DocDB.currentPage < DocDB.currenDocument.length
      )
        DocDB.currenDocument(DocDB.currentPage).content
      else
        "PAGE NON DEFINI"
    }
  }

  def GoBackPageDocument(steps: Option[Int]): String = {
    if (DocDB.currenDocument == null)
      "DOCUMENT NON DEFINI"
    else if (DocDB.currentPage - steps.getOrElse(1) < 0)
      "PAGE NON DEFINI"
    else {
      DocDB.currentPage = DocDB.currentPage - steps.getOrElse(1)
      DocDB.currenDocument(DocDB.currentPage).content
    }

  }




  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case Back(steps, replyTo) => 
        val content = GoBackPageDocument(steps)
        replyTo ! content
        this

      case Turn(page, replyTo) =>
        val content = turnPageDocument(page)
        replyTo ! content
        this

      case SaveDocument(doc, replyTo) =>
        saveDocument(doc)
        replyTo ! "Document bien reçu"
        this
    }
}