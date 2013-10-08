package au.com.langdale.soapbox
import scala.xml.NodeSeq
import sbt._
import Publisher._

object Twitter extends Plugin {

  val twitterID = taskKey[String]("A twitter handle.")
  val tweetScript = taskKey[NodeSeq]("The tweeting script to be included in the page head.")
  val tweetButton = taskKey[NodeSeq]("The tweet button to include one or more times in the page body.")


  override def projectSettings = Seq(
    
    tweetScript := {
      twitterID.?.value map { _ =>
        <script id="twitter-wjs" src={siteProtocol.value + "://platform.twitter.com/widgets.js"}/>
      } getOrElse NodeSeq.Empty
    },

    tweetButton := {
      twitterID.?.value map { id =>
        <a href="https://twitter.com/share" class="twitter-share-button" data-via={id}>Tweet</a>
      } getOrElse NodeSeq.Empty
    }
  )
}
