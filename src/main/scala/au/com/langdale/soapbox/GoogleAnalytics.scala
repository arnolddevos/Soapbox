package au.com.langdale.soapbox
import scala.xml.{NodeSeq,Comment}
import sbt._
import Publisher._

/**
 * Adds google analytics to the site.
 */
object GoogleAnalytics extends Plugin {
  
  val analyticsDomain   = taskKey[String]("The family domain for google analytics")
  val analyticsTracker  = taskKey[String]("The google analytics tracker ID")
  val analyticsFragment = taskKey[NodeSeq]("The google analytics code fragment needed on each page to be tracked")

  private val script1 = """
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
  """
  
  private val script2 = """
    try{
      var pageTracker = _gat._getTracker("TRACKER");
      pageTracker._setDomainName("DOMAIN");
      pageTracker._trackPageview();
    } catch(err) {}
  """

  override def projectSettings = Seq(

    analyticsDomain := siteDomain.value,

    analyticsFragment := {
      analyticsTracker.?.value map {
        tracker =>
          <script type="text/javascript">{Comment(script1)}</script> ++
        	<script type="text/javascript">{Comment(script2.replace("TRACKER",tracker).replace("DOMAIN",analyticsDomain.value))}</script>
      } getOrElse NodeSeq.Empty
    }
  )
}
