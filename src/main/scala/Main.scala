import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.rydgel.scalagram.responses.User


object Main extends App {

  implicit val system = ActorSystem("Iconotasks")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val actorProps = Props[InstagramFetcher]

  Source.actorPublisher[User](actorProps)
    .mapAsyncUnordered(5000)(u => InstagramAPI.user(Config.accessToken, u.id))
    .filter(_.isDefined) // filter out private accounts
    .map(p => s"${p.get.username}: ${p.get.counts.followed_by} followers")
    .runWith(Sink.foreach(println))
    .onComplete({ case x => println(x) })
}