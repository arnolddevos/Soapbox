package au.com.langdale.soapbox

import java.net.{URI, URISyntaxException}
import scala.xml.{XML, Node, NodeSeq, NodeBuffer, Text, Elem, MetaData, TopScope, Xhtml, Source}
import javax.xml.parsers.SAXParserFactory
import org.joda.time.format.DateTimeFormat

/**
 * XML and URI supporting utilities
 */
object Util {
  val XHTML = "http://www.w3.org/1999/xhtml"
  
  def nodeToXHTML(x: Elem): String = {
    val sb = new StringBuilder
    sb append """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
"""
    Xhtml.toXhtml(x, TopScope, sb, false, false)
    sb.toString
  }

  def xmlToNode(x: String): Elem = {
    val f = SAXParserFactory.newInstance
    f.setNamespaceAware(false)
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    f.setFeature("http://xml.org/sax/features/external-general-entities", false )
    f.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    XML.loadXML(Source.fromString(x), f.newSAXParser)
  }

  object E {
    def apply(label: String, atts: MetaData, child: Seq[Node]) = Elem( null, label, atts, TopScope, false, child: _*)
    def unapply(n: Node): Option[(String, MetaData, Seq[Node])] = n match {
      case e: Elem => Some(e.label, e.attributes, e.child)
      case _ => None
    }
  }
  
  object URI {
    def apply(s: String, a: String, p: String, q: String, f: String) = new URI(s,a,p,q,f).toASCIIString
    def unapply(u: String) = {
      try {
        val v = new URI(u)
        if( v.isOpaque)
          None
        else
          Some(v.getScheme, v.getAuthority, v.getPath, v.getQuery, v.getFragment)
      }
      catch {
        case _:URISyntaxException => None
      }
    }
  }

  implicit class MetaDataOps(m: MetaData) {
    def has(p: (String, String)): Boolean = m(p._1) == Text(p._2) 
    def has(k: String): Boolean = m(k) != null
  }

  case class MatchAttribute( name: String ) {
    def unapply(atts: MetaData) = Option(atts(name)) map(_.text)
  }
  
  def find[T](ns: NodeSeq)(rule: PartialFunction[Node, T]): Option[T] = {
    for(n <- ns) {
      if( rule.isDefinedAt(n)) 
        return Some(rule(n))
      else {
        val ot = find(n.child)(rule)
        if( ! ot.isEmpty )
          return ot
      }
    }
    None
  }
 
  def findElem(ns: NodeSeq, label: String): Option[Elem] = find(ns) {
    case e: Elem if e.label == label => e
  }

  def findId(ns: NodeSeq, id: String): Option[Elem] = find(ns) {
    case e : Elem if e.attributes has "id" -> id => e
  }

  case class DateValue(pattern: String) {
    private val format = DateTimeFormat.forPattern(pattern)

    def unapply(text: String) = 
      try {
        Some(format.parseDateTime(text).getMillis)
      }
      catch {
        case _:IllegalArgumentException => None
      }

    def apply(millis: Long) = format.print(millis)
  }
}
