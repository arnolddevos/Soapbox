package au.com.langdale.soapbox

import sbt._
import Keys._
import Publisher._

import org.pegdown.{PegDownProcessor,Extensions}
import Extensions._

object Resources extends Plugin {
  
  val resourceBuild       = taskKey[Seq[File]]("copy resource files into site")
  val resourceSources     = taskKey[Seq[File]]("directories for resource source files")
  val resourceLibraries   = taskKey[File]("directory for third party resource libraries. each subdirectory will be added to resourceSources")
  val resourceFilter      = taskKey[FileFilter]("wildcard expression matching resource file names")
  val resourceImageFilter = taskKey[FileFilter]("wildcard expression matching image file names")
  val resourcePageFilter  = taskKey[FileFilter]("wildcard expression matching HTML file names")
  val resourceExclusions  = taskKey[PathFinder]("specifies paths to ignore when copying resources")
  val resourceMap         = taskKey[Seq[(File, String)]]("resource files paired theit site relative path names")

  def mapPaths( roots: Seq[File], exclude: PathFinder, include: FileFilter): Seq[(File, String)] = {
    for {
      root <- roots
      pair <- root ** include --- exclude pair (relativeTo(root), false)
    }
    yield pair
  }

  override def projectSettings = Seq(

    resourceLibraries   := baseDirectory.value / "lib",
    resourceSources     := siteSources.value,
    resourceFilter      := "*.css" | "*.pdf" | "*.ico" | "*.js" | "*.woff" | "*.ttf",
    resourceImageFilter := "*.png" | "*.gif" | "*.jpg" | "*.svg",
    resourcePageFilter  := "*.html",
    resourceExclusions  := PathFinder.empty,

    resourceMap := 
      mapPaths(resourceSources.value, resourceExclusions.value, 
          resourceFilter.value || resourceImageFilter.value || resourcePageFilter.value) ++
      mapPaths((resourceLibraries.value * DirectoryFilter).get, resourceExclusions.value, 
          resourceFilter.value || resourceImageFilter.value ),

    resourceBuild := {
      for {
        (src, path) <- resourceMap.value 
        dst = siteProduct.value / path
      }
      yield {
        IO.copyFile(src, dst)
        dst
      }
    },

    siteResources ++= {
      for((src, dst) <-resourceMap.value if resourceFilter.value accept src)
        yield dst
    },

    siteImages ++= {
      for((src, dst) <-resourceMap.value if resourceImageFilter.value accept src)
        yield dst
    },

    sitePages ++= {
      for((src, dst) <-resourceMap.value if resourcePageFilter.value accept src)
        yield dst
    },

    watchSources ++= {
      for((f, _) <- resourceMap.value)
        yield f
    }

  )
}
