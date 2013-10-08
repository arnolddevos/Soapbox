package au.com.langdale.soapbox

import Util._
import ImageUtil._
import scala.xml.{Elem}
import java.util.Date

import sbt._
import Publisher._

/**
 * Copies a set of inmages into the site and adds an RSS2 and a preview page for them.
 */

object Gallery extends Plugin {
  
  val gallerySources   = taskKey[Seq[File]]("directory for images to form a gallery")
  val galleryThumbSize = settingKey[Int]("the size of thumbnails in the gallery")
  val galleryItems     = taskKey[Seq[Item]]("images and metadata making up a gallery")
  val galleryFilter    = taskKey[FileFilter]("wildcard expression matching image file names")

  val galleryTitle       = taskKey[String]("a title for the gallery")
  val galleryTemplate    = taskKey[(String, Seq[Item]) => Elem]("a html template for a gallery")

  val galleryName  = settingKey[String]("the pathname for the gallery page")
  val galleryBuild = taskKey[Seq[File]]("create a gallery")

  def galleryItemPaths(items: Seq[Item]) =
    for( item <- items; path <- Seq( item.path ) ++ item.icon.toSeq) 
      yield path
 
  override def projectSettings = Seq(
  
    gallerySources     := siteSources.value map (_ / "gallery"),
    galleryFilter      := "*.jpg",
    galleryThumbSize   := 280,
    galleryTitle       := "Image Gallery",
    galleryName        := "gallery/index.html", 

    galleryItems := {
      val prefix = Option(file(galleryName.value).getParent) map (_ +  "/") getOrElse ""

      for( src <- (gallerySources.value ** galleryFilter.value).get) yield {
        val stem = file(src.getName).base
        val ext  = src.ext
        val path = prefix+stem+ext
        val ipath = prefix+"th-"+stem+".jpg"
        val dst = siteProduct.value / path
        val idst = siteProduct.value / ipath
        val item = Item( dst, path, stem, "", src.lastModified, Some(ipath))
        IO.copyFile( src, dst )
        readImage( src ).fit( galleryThumbSize.value ).writeJPEG( idst )
        item
      }
    },
    
    galleryBuild := {
      if(! galleryItems.value.isEmpty) {
        val html = nodeToXHTML( galleryTemplate.value( galleryTitle.value, galleryItems.value)) 
        val dst = siteProduct.value / galleryName.value
        IO.write( dst, html)
        Seq(dst) ++ (galleryItemPaths(galleryItems.value) map (siteProduct.value / _))
      }
      else Seq()
    },

    sitePages ++= {
      if( galleryItems.value.isEmpty ) Seq() else Seq(galleryName.value)
    },

    siteImages ++= galleryItemPaths(galleryItems.value)
  )
}
