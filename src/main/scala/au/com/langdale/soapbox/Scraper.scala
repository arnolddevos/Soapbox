package au.com.langdale.soapbox

import sbt._
import Keys._
import Publisher._
import Util._

import Resources.resourceExclusions

object Scraper extends Plugin {
  
  val scrapeBuild     = taskKey[Seq[File]]("Convert HTML to Markdown")
  val scrapeSources   = taskKey[Seq[File]]("directories for HTML source files")
  val scrapeProduct   = taskKey[File]("directory for generated markdown files")
  val scrapeFilter    = taskKey[FileFilter]("wildcard expression matching HTML file names")
  val scrapeMap       = taskKey[Seq[(File, String)]]("HTML source files paired with markdown paths")
  val scrapeBases     = settingKey[Seq[URI]]("base URIs. each extracted URI will be made relative to one of these if possible")
  val scrapeCodeLanguage = settingKey[String]("the programming language assumed for all code blocks")
  val scrapeAddTitle  = settingKey[Boolean]("if true, a top level heading is added with the html title text")
  val scrapeContentId = settingKey[Option[String]]("if given, only the element with this id is extracted")

  def mapPaths( roots: Seq[File], include: FileFilter, ext: String): Seq[(File, String)] = {
    for {
      root <- roots
      (src, rel) <- root ** include pair relativeTo(root)
      dst = file(rel).base + ext
    }
    yield (src, dst)
  }

  override def projectSettings = Seq(

    scrapeSources := siteSources.value map (_ / "scrape"), 
    scrapeProduct := target.value / "scrape",
    scrapeFilter  := "*.html",
    scrapeMap     := mapPaths(scrapeSources.value, scrapeFilter.value, ".md"),
    scrapeBases   := Seq(new URI(sitePrefix.value)),
    scrapeCodeLanguage := "scala",
    scrapeAddTitle := true,
    scrapeContentId := Some("content"),

    resourceExclusions := resourceExclusions.value +++ scrapeSources.value ** scrapeFilter.value,

    scrapeBuild := {
      object scraper extends HtmlToMarkdown {
        val prefix = sitePath.value
        val bases = scrapeBases.value
        val codeLanguage = scrapeCodeLanguage.value
        val addTitle = scrapeAddTitle.value
        val contentId = scrapeContentId.value
      }

      for {
        (src, path) <- scrapeMap.value 
        dst = scrapeProduct.value / path
        if dst olderThan src
        html = xmlToNode(IO.read(src))
        md = scraper(html)
      }
      yield {
        IO.write(dst, md)
        dst
      }
    }
  )
}
