package au.com.langdale.soapbox

import scala.xml.{Node, NodeSeq, Text, Elem}
import java.net.URI
import Util._

trait HtmlToMarkdown extends (NodeSeq => String) {

  val prefix: String
  val bases: Seq[URI]
  val codeLanguage: String
  val addTitle: Boolean
  val contentId: Option[String]

  val indentSpaces = 4
  val listItem = Phrase("*" + " " * (indentSpaces-1))

  def apply(html: NodeSeq): String = {
    val content = contentId flatMap (id => findId(html,id)) getOrElse html
    val title = if(addTitle) findElem(html, "title") map (_.text) else None
    val tsegs = title.map (t => mdH(1, cleanLines(t))) getOrElse Seq()
    val raw = tsegs ++ nodesToSegments(content)
    foldSegments(indentSegments(raw)).map(segmentToString).mkString
  }

  sealed trait Segment
  case class Phrase(text: String) extends Segment
  case class Indent( n: Int ) extends Segment
  case object Space extends Segment
  case object Break extends Segment
  case object Block extends Segment

  private def nodesToSegments(html: NodeSeq): Seq[Segment]  = html flatMap blockRule

  private def foldSegments( segs: Seq[Segment]): Seq[Segment] = {
    
    def merge( ss: List[Segment], s0: Segment): List[Segment] = (ss, s0) match {
      case (_, Indent(_))      => ss
      case (Phrase(_) :: _, _) => s0 :: ss
      case (_, Phrase(_))      => s0 :: ss
      case (Block :: _, _)     => ss
      case (_ :: ts, Block)    => Block :: ts
      case (Break :: _, _)     => ss
      case (_ :: ts, Break)    => Break :: ts
      case (Space :: _, Space) => ss
      case (_ :: _, Space)     => Space :: ss
      case (Nil, _)            => Nil
    }

    segs.foldLeft(List[Segment]())(merge).reverse
  }

  private def indentSegments( segs: Seq[Segment]): Seq[Segment] = {
    type Result = (Int, List[Segment])

    def propagate( r: Result, s: Segment): Result = (r, s) match {
      case ((level, ss), Indent(n))                      => (level + n, ss)
      case ((level, ss @ (Break|Block) :: _), Phrase(t)) => (level, Phrase(" " * (level * indentSpaces) + t) :: ss)
      case ((level, ss @ (Break|Block) :: _), Space)     => (level, ss)
      case ((level, ss), _)                              => (level, s :: ss)
    }

    val (level, result) = segs.foldLeft((0, List[Segment]()))(propagate)
    assert( level == 0)
    result.reverse
  }

  private def segmentToString(s: Segment): String = s match {
    case Phrase(text) => text
    case Space => " "
    case Break => "\n"
    case Block => "\n\n"
    case _ => ""
  }

  private def wrap(a: String, b: String="")(s: Segment) = s match {
    case Phrase(text) => Phrase(a + text + b)
    case s => s
  }
  
  private def cleanLines(text: String): Seq[Segment] = {
    require(text.nonEmpty)

    val head = text.head match {
      case ' ' | '\t' => Seq(Space)
      case _ => Seq()
    }

    val last = text.last match {
      case ' ' | '\t' => Seq(Space)
      case '\n' | '\f' => Seq(Break)
      case _ => Seq()
    }

    val middle = for {
      l1 <- text.lines.toSeq
      l2 = l1.trim 
      if l2.nonEmpty
      s <- Seq(Break, Phrase(l2))
    } 
    yield s

    head ++ middle.drop(1) ++ last
  }

  def trimCodeBlock(segs: Seq[Segment]): Seq[Segment] = {
    val break: Segment => Boolean =  { case Break|Phrase("\n") => true case _ => false }
    val ss1 = segs.reverse dropWhile break
    val ss2 = ss1.reverse dropWhile break
    ss2
  }

  private def prefixPhrase( prefix: Seq[Segment], segs: Seq[Segment]): Seq[Segment] = {
    var (a, b) = segs span { case Phrase(_) => false case _ => true }
    a ++ prefix ++ b
  }

  private def makeSiteRelative( uri0: String): String = {
    val uri = new URI(uri0).normalize
    if( Option(uri.getPath) exists (_ startsWith "/")) {
      for( base <- bases ) {
        val rel = base.relativize(base.resolve(uri))
        if( rel != uri && ! rel.isAbsolute) return prefix + rel.toString
      }
    }
    uri0
  }

  private def optPhrase(ot: Option[String]) = (ot map (Phrase(_))).toSeq
 
  def mdP(ts: Seq[Segment])     = Block +: ts :+ Block
  def mdDiv(ts: Seq[Segment])   = Block +: ts :+ Block
  def mdPRE(ts: Seq[Segment])   = Block +: Phrase("```" + codeLanguage) +: Break +: trimCodeBlock(ts) :+ Break :+ Phrase("```") :+ Block
  def mdBR                      = Seq(Break)
  def mdUL(ts: Seq[Segment])    = Block +: ts :+ Block
  def mdLI(ts: Seq[Segment])    = Break +: prefixPhrase(Seq(listItem, Indent(1)), ts) :+ Indent(-1) :+ Break
  def mdH(level: Int, ts: Seq[Segment]) = Block +: (ts map wrap("#" * level + " ")) :+ Block
  def mdA(href: String, ts: Seq[Segment]) = Phrase("[") +: ts :+ Phrase("](" + makeSiteRelative(href) + ")")
  def mdImg(src: String, alt: Option[String]) = Phrase("![") +: optPhrase(alt) :+ Phrase("](" + makeSiteRelative(src) + ")")
  def mdEM(ts: Seq[Segment])    = ts map wrap("_", "_")
  def mdSTRONG(ts: Seq[Segment])= ts map wrap("__", "__")
  def mdBIG(ts: Seq[Segment])   = mdSTRONG(ts)
  def mdSMALL(ts: Seq[Segment]) = ts
  def mdDEL(ts: Seq[Segment])   = ts
  def mdCODE(ts: Seq[Segment])  = ts map wrap("`", "`")
  def mdSPAN(c: Option[String], ts: Seq[Segment]) = ts

  private type MdRule = PartialFunction[Node, Seq[Segment]]
  private val Href = MatchAttribute("href")
  private val Src  = MatchAttribute("src")
  private val Alt  = MatchAttribute("alt")

  private val blockRule: MdRule = {
    case <html>{child@_*}</html> => child flatMap blockRule
    case <head>{child@_*}</head> => Seq()
    case <body>{child@_*}</body> => child flatMap blockRule
    case <script>{child@_*}</script> => Seq()
    case <div>{child@_*}</div> => mdDiv(child flatMap blockRule)
    case <h1>{child@_*}</h1> => mdH(1, child flatMap textRule)  
    case <h2>{child@_*}</h2> => mdH(2, child flatMap textRule)  
    case <h3>{child@_*}</h3> => mdH(3, child flatMap textRule)  
    case <pre>{child@_*}</pre> => mdPRE(child flatMap preRule)
    case <ul>{child@_*}</ul> => mdUL(child flatMap blockRule)
    case <li>{child@_*}</li> => mdLI(child flatMap blockRule)  
    case <p>{child@_*}</p> => mdP(child flatMap textRule)  
    case n => textRule(n)
  }

  private val preRule: MdRule = {
    case Text(t) if t.nonEmpty => Seq(Phrase(t))
    case <br/> => Seq(Phrase("\n"))
    case E(_, _, child) => child flatMap preRule
    case _ => Seq()
  }
  
  private val textRule: MdRule = {
    case E("a", Href(href), child) => mdA(href, child flatMap textRule)
    case E("img", atts @ Src(src), _) => mdImg(src, Alt unapply atts)
    case <strong>{child@_*}</strong> => mdSTRONG(child flatMap textRule)  
    case <em>{child@_*}</em> => mdEM(child flatMap textRule)
    case <del>{child@_*}</del> => mdDEL(child flatMap textRule)  
    case E("span", atts, child) => mdSPAN(atts.get("class").map(_.text), child flatMap textRule)  
    case <small>{child@_*}</small> => mdSMALL(child flatMap textRule)  
    case <big>{child@_*}</big> => mdBIG(child flatMap textRule)  
    case <code>{child@_*}</code> => mdCODE(child flatMap textRule)  
    case E(_, _, child) => child flatMap textRule
    case <br/> => mdBR 
    case Text(t) if t.nonEmpty => cleanLines(t)
    case _ => Seq()
  }
}
