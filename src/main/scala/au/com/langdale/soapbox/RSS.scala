package au.com.langdale.soapbox

import au.com.langdale.soapbox.Publisher.Item
import java.text.SimpleDateFormat
import java.util.Date

/**
 * A template for an RSS1 document.
 */
object RSS {

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  val tzFormat = new SimpleDateFormat("Z")
  def formatW3CDate(date: Date) =  dateFormat.format(date) + { val tz = tzFormat.format(date); tz.substring(0,3) + ":" + tz.substring(3) }

  def apply(channel: Item, items: Seq[Item]) =
    <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns="http://purl.org/rss/1.0/" xmlns:dc="http://purl.org/dc/elements/1.1/">
      <channel rdf:about="">
        <title>{channel.title}</title>
        <link>{channel.path}</link>
        <description>{channel.description}</description>
        <items>
          <rdf:Seq>
            {
            for( i <- items ) yield
              <rdf:li resource={i.path}/>
            }
          </rdf:Seq>
        </items>
      </channel>
      {
      for( i <- items ) yield
      <item rdf:about={i.path}>
        <title>{i.title}</title>
        <link>{i.path}</link>
        <description>{i.description}</description>
        <dc:date>{formatW3CDate(new Date(i.date))}</dc:date>
      </item>
      }
    </rdf:RDF>

}
