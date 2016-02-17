import com.rydgel.scalagram._
import com.rydgel.scalagram.responses._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object InstagramAPI {

  def followers(at: String, uid: String, c: Option[String] = None): Future[(Option[List[User]], Option[String])] =
    for {
      response <- Scalagram.followedBy(AccessToken(at), uid, Some(100), c)
      nextCursor <- Future.successful(response.pagination.flatMap(_.nextCursor))
      users <- Future.successful(response.data)
    } yield (users, nextCursor)


  def user(at: String, uid: String): Future[Option[Profile]] = {
    Scalagram.userInfo(AccessToken(at), uid).map(_.data)
      .recoverWith({ case _ => Future.successful(None)})
  }

}
