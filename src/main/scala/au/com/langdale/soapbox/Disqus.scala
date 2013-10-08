package au.com.langdale.soapbox
import scala.xml.NodeSeq
import sbt._
import Publisher._

object Disqus extends Plugin {

  val disqusID = taskKey[String]("A disqus forum ID")
  val disqusFragment = taskKey[NodeSeq]("The disqus forum code fragment to be included in a page")

  private val disqusDebug = false

  override def projectSettings = Seq(
    
    disqusFragment := {
      disqusID.?.value map { id =>
        def disqusScript = "http://disqus.com/forums/"+ id +"/embed.js"
        def disqusRef = "http://disqus.com/forums/"+ id +"/?url=ref"

        <div id="comments">
          <div id="disqus_thread"></div>
          { if(disqusDebug) <script type="text/javascript">  var disqus_developer = 1; </script> else NodeSeq.Empty }
          <script type="text/javascript" src={disqusScript}></script>
          <noscript><a href={disqusRef}>View the discussion thread.</a></noscript>
        </div>  
      } getOrElse NodeSeq.Empty
    }

  )
}
