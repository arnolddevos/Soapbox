package au.com.langdale.soapbox

import sbt._
import Keys._
import org.pegdown.{PegDownProcessor,Extensions}
import java.util.Date
import Extensions._
import Util._
import Publisher._

object Markdown extends Plugin {
  import xml.Node

  val markdownBuild   = taskKey[Seq[File]]("Convert Markdown sources to HTML yielding result files")
  val markdownItems   = taskKey[Seq[Item]]("Convert Markdown sources to HTML")
  val markdownSources = taskKey[Seq[File]]("directories for Markdown source files")
  val markdownFilter  = taskKey[FileFilter]("wildcard expression matching Markdown file names")
  val markdownMap     = taskKey[Seq[(File, String)]]("markdown source files paired with HTML path names")

  def mapPaths( roots: Seq[File], include: FileFilter, ext: String): Seq[(File, String)] = {
    for {
      root <- roots
      (src, rel) <- root ** include pair relativeTo(root)
      path = file(rel).base + ext
    }
    yield (src, path)
  }

  def groupSections(ns: Seq[Node]): Seq[Node] = {
    type Heap = List[List[Node]]

    def scan(nss: Heap, n: Node): Heap = n match {
      case _ if nss.isEmpty   => List(List(n))
      case E("h1"|"h2", _, _) => List(n) :: nss
      case _                  => (n :: nss.head) :: nss.tail
    }

    val heap = ns.foldLeft(Nil: Heap)(scan)

    def rebuild(ns: List[Node], s: List[Node]) = {
      val r = s.reverse
      r.head match {
        case E("h1"|"h2", _, _) => <section class="slide">{r}</section> :: ns
        case _                  => r ::: ns
      }
    }

    heap.foldLeft(Nil: List[Node])(rebuild)
  }

  override def projectSettings = Seq(

    markdownSources := siteSources.value,
    markdownFilter  := "*.md" | "*.markdown" | "*.reveal" | "*.deck",
    markdownMap := mapPaths(markdownSources.value, markdownFilter.value, ".html"),

    markdownItems := {

      val ss = Scraper.scrapeBuild.value

      val pegdown = new PegDownProcessor(ALL - HARDWRAPS - QUOTES - SMARTS)

      for {
        (src, path) <- markdownMap.value
        template = siteTemplate.value(src)
        mdText = IO.read(src)
        xmlText = pegdown.markdownToHtml(mdText)
        nodes = xmlToNode( "<wrap>" + xmlText + "</wrap>" ).child
        title = findElem(nodes, "h1") map (_.text.trim) getOrElse file(src.getName).base
        short = findElem(nodes, "p") map (_.text.trim) getOrElse ""
        page = nodeToXHTML( template.expand(title, groupSections(nodes)))
        dst = siteProduct.value / path
      }
      yield {
        IO.write( dst, page)
        Item(dst, path, title, short, src.lastModified)
      }


    },

    markdownBuild := {
      for(item <- markdownItems.value)
        yield item.resource
    },

    Blog.blogItems ++= markdownItems.value,

    sitePages ++= {
      for((_, path) <- markdownMap.value)
        yield path
    },

    watchSources ++= {
      for((f, _) <- markdownMap.value)
        yield f
    }
  )
}
