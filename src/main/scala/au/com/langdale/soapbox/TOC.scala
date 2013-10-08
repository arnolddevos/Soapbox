package au.com.langdale.soapbox

import scala.xml.{Elem, Node, NodeSeq, XML}
import scala.xml.NodeSeq.Empty
import Util._

import sbt._
import Publisher._

/**
 * Adds an eclipse help table of contents document to the site.
 *
 * The TOC is generated from a list of anchors on a given html page.
 * It is sufficient to make the site deployable as eclipse help content.
 */
object TOC extends Plugin {

  val tocName    = taskKey[String]("the path name for the generated eclipse table of contents")
  val tocTitle   = taskKey[String]("the title for the generated table of contents")
  val tocSource  = taskKey[String]("the path name in the generated site of an HTML page from which an eclipse table of contents will be extracted")
  val tocBuild   = taskKey[Seq[File]]("create an eclipse help table of contents file")
  
  override def projectSettings = Seq(

    tocTitle   := "Help Contents",
    tocName := "toc.xml",
    tocSource  := "toc.html",

    tocBuild := {
      val src = siteProduct.value / tocSource.value
      if( src.exists ) {
        val html = xmlToNode(IO.read(src))
        val toc = htmlToTOC(tocTitle.value, tocSource.value, html)
        val dst = siteProduct.value / tocName.value
        XML.save(dst.getPath, toc)
        Seq(dst)
      }
      else Seq()
    }
  )

  def htmlToTOC(title: String, link: String, source: NodeSeq): Elem = {
    val ls = findElem(source, "ul") map (_.child) getOrElse NodeSeq.Empty
    <toc label={title} topic={link}>{ls flatMap tocRule}</toc>
  }
  
  private val tocRule: PartialFunction[Node, Seq[Node]] = {
    case <li>{child@_*}</li> =>
      var href: Option[String] = None
      val text = new StringBuilder
      var subs: NodeSeq = Empty
      
      child foreach {
        case a @ <a>{child@_*}</a> if a.attributes has "href" => 
          href = Some(a.attributes("href").text)
          text append child.text
          
        case <ul>{child@_*}</ul> => subs = child 
          
        case other =>  text append other.text
      }
      
      href match {
        case Some(addr) =>
          <topic label={text.toString.trim} href={addr}>{subs flatMap tocRule}</topic>
            
        case None =>
          <topic label={text.toString.trim}>{subs flatMap tocRule}</topic>
      }      
          
    case _ => Empty
  }
}
