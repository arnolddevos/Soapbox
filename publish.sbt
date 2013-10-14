publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomExtra := (
  <scm>
    <url>git@github.com:arnolddevos/Soapbox.git</url>
    <connection>scm:git@github.com:arnolddevos/Soapbox.git</connection>
  </scm>
  <developers>
    <developer>
      <id>adevos</id>
      <name>Arnold deVos</name>
      <url>http://notes.langdale.com.au</url>
    </developer>
  </developers>)
