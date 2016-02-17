import akka.actor._
import akka.stream.actor.ActorSubscriberMessage._
import akka.stream.actor._
import com.rydgel.scalagram.responses.User
import scalaz._
import Scalaz._

import scala.collection.mutable
import scala.language.postfixOps
import scala.concurrent.duration._



object InstagramFetcher {
  case class Publish(data: List[User])
  case object Tick
}

class InstagramFetcher extends ActorPublisher[User] {

  import ActorPublisherMessage._
  import context.dispatcher

  def scheduler = context.system.scheduler

  var lastCursor: Option[String] = none[String]
  var isOver: Boolean = false
  var queue: mutable.Queue[User] = mutable.Queue.empty

  override def preStart() = {
    scheduler.schedule(1 seconds, 1 seconds, self, InstagramFetcher.Tick)
  }

  def receive: Receive = {

    case InstagramFetcher.Publish(users) =>
      users.foreach(queue.enqueue(_))
      publishIfNeeded()

    case Request(cnt) =>
      publishIfNeeded()

    case Cancel =>
      context.stop(self)

    case OnError(err: Exception) =>
      onError(err)
      context.stop(self)

    case OnComplete =>
      onComplete()
      context.stop(self)

    case InstagramFetcher.Tick =>
      if (!isOver) addUsersToBuffer()
      else context.stop(self)

    case _ =>
  }

  def publishIfNeeded() = {
    while (queue.nonEmpty && isActive && totalDemand > 0) {
      onNext(queue.dequeue())
    }
  }

  def addUsersToBuffer(): Unit = {
    InstagramAPI.followers(Config.accessToken, Config.userId, lastCursor).map { response =>
      lastCursor = response._2
      isOver = lastCursor.isEmpty
      self ! InstagramFetcher.Publish(response._1.getOrElse(List.empty))
    }
  }

}
