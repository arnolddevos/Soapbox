name := "soapbox"

organization := "au.com.langdale"

licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

homepage := Some(url("https://github.com/arnolddevos/Soapbox"))

version := "0.3"

sbtPlugin := true

libraryDependencies += "org.pegdown" % "pegdown" % "1.4.1"

libraryDependencies += "joda-time" % "joda-time" % "1.6.2"
