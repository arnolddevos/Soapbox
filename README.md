# Soapbox - Another Static Site Publisher

Soapbox creates static web sites from HTML, markdown,
images and other resources. It is an [sbt] 0.13 plugin and
easy to configure via sbt settings 
and extend with scala code.

Here's what it can do:

* Convert markdown using [pegdown] and copy resources into a web site.
* Apply templates, which are scala functions.  (Scala's XML syntax works well here.)
* Generate a blog and/or a picture gallery based on the site contents.
* Conveniently incorporate Twitter, Disqus and Google Analytics.
* Extract content from existing XHTML pages, convert it to markdown and incorporate it into the site.
* Execute custom tasks written in scala for sbt as part of the build.
* Create a site map and use it to recognize wiki words in content.

## Rationale and Alternatives

There are many alternatives.  [Jekyll], which powers github pages, is popular and easy to use.  The author has successfully deployed Jekyll for internal documentation sites and can recommend it.

In comparison, Soapbox is more scala oriented. It is easy for scala programmers to 
configure and extend Soapbox.

One could also use [sbt] with the [lwm] plugin or similar.  In comparison, Soapbox is more specialized for web site generation.

The author started this project as [SPublisher] and then migrated the most useful features to the sbt environment. Some things, such as RSS, were dropped.  Markdown replaces the tomcat notes utility for authoring content.

## Quick Start

The default directory structure for a Soapbox project looks like this:

```
+-- build.sbt
|
+--project
|  |
|  +--soapbox.sbt
|  |
|  +--Templates.scala
|
+--src
|  |
|  +--site
|     |
|     +-- blog.txt
|     |
|     +-- tree of markdown, css, js, image and other files
|
+--target
   |
   +--site
      |
      +-- generated web site
```

### build.sbt

This contains the main settings for the site.  For example:

```scala
siteDomain := "notes.langdale.com.au"

analyticsTracker := "UA-9999999-3"

disqusID := "notasvandevos"

twitterID := "a4dev"

blogPath := "index.html"

blogTitle := "Software related Notes by Arnold deVos by date"

siteMenu := Menu( "Main",
  List(
    "Home" -> "index.html", 
    "About" -> "About.html",
    "GitHub" -> "http://github.com/arnolddevos/",
    "Contact" -> "http://www.langdale.com.au/contacts/adv.html"
  )
)
```

Check the source for available tasks and settings and their descriptions.  
A listing will soon be available here.

### soapbox.sbt

This endows sbt with the desired version of soapbox. It has one line:

```scala
addSbtPlugin("au.com.langdale" % "soapbox" % "0.2")
```
### Templates.scala

Contains template definitions like this:

```scala
import au.com.langdale.soapbox.Publisher._
import sbt._

object Templates extends Plugin {
  override def projectSettings = Seq(
    siteTemplates += Template("*.md", 
      (title, content) => {
        <html xmlns={XHTML}>
          <head><title>{title}</title></head>
          <body>{content}</body>
        </html>
      }
    )
  )
}
```

The pattern `"*.md"` determines which source files are expanded with this template. 
The template itself is a function `(String, NodeSeq) => Elem`.

Any number of templates can be appended to `siteTemplates`, each for a different
set of source files.  The pattern can be replaced with an explicit FileFilter.
The templates need not all reside in `Templates.scala`.  A number of `.scala` files
can be created.

As templates are just scala functions they can be composed as required and common 
parts factored out.
Templates can also reference tasks and settings.  For example, a template might use 
`sitePath.value` to construct links.

### blog.txt

This is used to create a chronological list of pages, the blog. 

```
2012-03-25 An_Incremental_JSON_Generator.html
2010-09-19 Querying_a_Dataset_with_Scala_s_Pattern_Matching.html
2010-09-14 Generators_in_Scala.html
2010-08-11 An_Update_to_the_Scala_Jetty_Wrapper.html
2010-04-16 Pimping_Servlet_and_Jetty.html
2009-11-14 Polyphonic_Scala_Actors_Part_2.html
2009-10-21 Polyphonic_Scala_Actors_Part_1.html
```
Each line gives a publication date and the pathname of a file 
in the generated site.  

There can be a number of blog listings: all files matching `*blog.txt`
are merged to form a combined listing.

### src/site and target/site

The markdown files and all resources needed to generate the site belong
under the `src/site` directory.  There is no restriction on the directory hierarchy,
which is replicated in the generated site under `target/site`.

It is often convenient to merge a number of directory trees to form the site.
The `siteSources` setting can be set in `build.sbt`. It takes a `Seq[File]`. 

[markdown]: http://daringfireball.net/projects/markdown/
[sbt]: http://www.scala-sbt.org/
[lwm]: http://software.clapper.org/sbt-lwm/
[jekyll]: http://jekyllrb.com/
[pegdown]: https://github.com/sirthias/pegdown
[SPublisher]: https://github.com/arnolddevos/SPublisher

