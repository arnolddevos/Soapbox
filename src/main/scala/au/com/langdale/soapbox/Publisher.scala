package au.com.langdale
package soapbox

import scala.xml.{NodeSeq, Elem}
import java.util.Date

import sbt._
import Keys._
import browserreload.BrowserReload._

/**
 * Defines the common types. 
 */ 
trait Common {

  case class Item(resource: File, path: String, title: String, description: String, date: Long, icon: Option[String] = None)
  case class Menu(val title: String, val entries: Seq[(String, String)])
  case class Template( filter: FileFilter, expand: (String, NodeSeq) => Elem)

}

object Publisher extends Common with Plugin {

  // the file locations
  val siteSources = settingKey[Seq[File]]("Directories for source files to be transformed into a web site")
  val siteProduct = settingKey[File]("Directory containing the generated web site")

  // the location of the site after deployment
  val siteProtocol= settingKey[String]("site protocol, http or https")
  val siteDomain  = settingKey[String]("site domain name")
  val sitePath    = settingKey[String]("path component of site URL")
  val sitePrefix  = settingKey[String]("prefix of all URLs in this site, including domain and path")

  // these keys form a sitemap which may be used to check links when pages are processed
  val sitePages     = taskKey[Seq[String]]("relative pathnames for all HTML pages") 
  val siteImages    = taskKey[Seq[String]]("relative pathnames for all images")
  val siteResources = taskKey[Seq[String]]("relative pathnames for other resources in the site")
  val siteTerms     = taskKey[Map[String,String]]("mapping from phrases to page pathnames for wiki hyperlinking")

  // site wide fragments and templates
  val siteMenu      = taskKey[Menu]("site wide menu")
  val siteFooter    = taskKey[NodeSeq]("site wide footer")
  val siteTemplates = taskKey[Seq[Template]]("search list of templates")
  val siteDefaultTemplate = taskKey[(String, NodeSeq) => Elem]("fallback template")
  val siteTemplate  = taskKey[File => Template]("map a source file to a template from siteTemplates")

  // the task to build a site
  val siteBuild = taskKey[Seq[File]]("master task to build a site (runs siteTasks)")

  override def projectSettings = Seq(

    siteSources := Seq(sourceDirectory.value / "site"),
    siteProduct := target.value / "site",

    siteProtocol:= "http",
    siteDomain  := "localhost",
    sitePath    := "/",
    sitePrefix  := siteProtocol.value + "://" + siteDomain.value + sitePath.value,

    sitePages     := Seq(),
    siteImages    := Seq(),
    siteResources := Seq(),
    siteTerms     := Map(),

    siteMenu      := Menu("", Nil),
    siteFooter    := NodeSeq.Empty,
    siteTemplates := Seq(),

    siteTemplate  := {
      src => (
        siteTemplates.value find (template => template.filter accept src) 
        getOrElse Template(AllPassFilter, siteDefaultTemplate.value)
      )
    },

    siteBuild := (
      Scraper.scrapeBuild.value ++
      Resources.resourceBuild.value ++
      Markdown.markdownBuild.value ++
      Gallery.galleryBuild.value ++
      Blog.blogBuild.value ++
      TOC.tocBuild.value
    ),

    browserTasks := Seq( siteBuild.value )
  )
}
