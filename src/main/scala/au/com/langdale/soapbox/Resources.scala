package au.com.langdale.soapbox

import sbt._
import Keys._
import Publisher._

import org.pegdown.{PegDownProcessor,Extensions}
import Extensions._

object Resources extends Plugin {
  
  val resourceBuild       = taskKey[Seq[File]]("copy resource files into site")
  val resourceSources     = taskKey[Seq[File]]("directories for resource source files")
  val resourceFilter      = taskKey[FileFilter]("wildcard expression matching resource file names")
  val resourceImageFilter = taskKey[FileFilter]("wildcard expression matching image file names")
  val resourcePageFilter  = taskKey[FileFilter]("wildcard expression matching HTML file names")
  val resourceExclusions  = taskKey[PathFinder]("specifies paths to ignore when copying resources")

  def mapPaths( roots: Seq[File], exclude: PathFinder, include: FileFilter): Seq[(File, String)] = {
    for {
      root <- roots
      pair <- root ** include --- exclude pair relativeTo(root)
    }
    yield pair
  }

  override def projectSettings = Seq(

    resourceSources     := siteSources.value,
    resourceFilter      := "*.css" | "*.pdf" | "*.ico" | "*.js" | "*.woff" | "*.ttf",
    resourceImageFilter := "*.png" | "*.gif" | "*.jpg" | "*.svg",
    resourcePageFilter  := "*.html",
    resourceExclusions  := PathFinder.empty,

    resourceBuild := {
      for {
        (src, path) <- mapPaths(resourceSources.value, resourceExclusions.value, 
          resourceFilter.value || resourceImageFilter.value || resourcePageFilter.value)
        dst = siteProduct.value / path
      }
      yield {
        IO.copyFile(src, dst)
        dst
      }
    },

    siteResources ++= {
      for((_, dst) <- mapPaths(resourceSources.value, resourceExclusions.value, resourceFilter.value))
        yield dst
    },

    siteImages ++= {
      for((_, dst) <- mapPaths(resourceSources.value, resourceExclusions.value, resourceImageFilter.value))
        yield dst
    },

    sitePages ++= {
      for((_, dst) <- mapPaths(resourceSources.value, resourceExclusions.value, resourcePageFilter.value))
        yield dst
    }
  )
}
