package au.com.langdale.soapbox

import java.util.Date
import scala.xml.{Elem}
import Util._

import sbt._
import Publisher._

/**
 * Add a basic blog to the site.
 */
object Blog extends Plugin {

  val blogTitle       = taskKey[String]("title text appearing on the blog")
  val blogSources     = taskKey[Seq[File]]("Directories containing publication lists.")
  val blogFilter      = taskKey[FileFilter]("Pattern to match a publication list containing lines formated 'yyyy-MM-dd path'")
  val blogItems       = taskKey[Seq[Item]]("candidate items for the blog")
  val blogContents    = taskKey[Seq[Item]]("filtered and sorted items for the blog")
  val blogPath        = taskKey[String]("the path name of the blog page")
  val blogBuild       = taskKey[Seq[File]]("build the blog")
  val blogTemplate    = taskKey[(String, Seq[Item]) => Elem]("a template for the blog")

  val Publication = """([\w-]+)\s+(.*)""".r
  val PubDate = DateValue("yyyy-MM-dd")

  override def projectSettings = Seq(

    blogTitle       := "A Blog",
    blogItems       := Seq(),
    blogPath        := "blog.html",
    blogSources     := siteSources.value, 
    blogFilter      := "*blog.txt",

    blogContents := {

      val items = blogItems.value.map (item => item.path -> item).toMap
      
      val publish = {
        for {
          f <- (blogSources.value ** blogFilter.value).get
          Publication(PubDate(date), path) <- IO.readLines(f)
          if items contains path
          item = items(path)
        }
        yield item.copy(date=date)
      }        

      publish.distinct.sortBy(_.date).reverse.take(100)
    },

    blogBuild := {
      if(blogContents.value.nonEmpty) {
        val html = nodeToXHTML( blogTemplate.value( blogTitle.value, blogContents.value))
        val dst = siteProduct.value / blogPath.value
        IO.write( dst, html)
        Seq(dst)
      }
      else Seq()
    },

    sitePages ++= {
      if( blogItems.value.isEmpty ) Seq() else Seq(blogPath.value)
    }
  )
}
