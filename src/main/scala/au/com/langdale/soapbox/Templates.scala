package au.com.langdale.soapbox

import Util._
import sbt._
import Publisher._
import scala.xml.NodeSeq

/**
 * Contributes templates to the site
 */
object Templates extends Plugin {
  val DisplayDate = DateValue("dd MMM yyyy")

  override def projectSettings = Seq(

    siteDefaultTemplate := {
      (title, content) => {
        <html xmlns={XHTML}>
          <head>
            <title>{title}</title>
            <link rel="stylesheet" href="css/graphic.css" type="text/css" />
            <link rel="stylesheet" href="css/highlight.css" type="text/css" />
            <link rel="icon" href="favicon.ico" type="image/x-icon" />
            <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />  
          </head>
          <body>
            <div class="container">
              <div id="banner">
                <img id="illust" src="illust-composite.png"/>
                <img id="logo" src="logo-composite.png"/>
              </div>
              <div id="main">
                <div id="content">{ content }</div>
                <div id="twitter">{ Twitter.tweetButton.value }</div>
                { Disqus.disqusFragment.value }
                <div id="menu">
                  <ul>
                  {
                    for((text, target) <- siteMenu.value.entries) yield {
                      <li><a href={target} class="menu">{text}</a></li>
                    }
                  }
                  </ul>  
                </div>
              </div>  
              <div id="footer">{ siteFooter.value }</div>
            </div>
            { Twitter.tweetScript.value }
            { GoogleAnalytics.analyticsFragment.value }  
            <script src="js/highlight.pack.js"></script>
            <script>hljs.initHighlightingOnLoad();</script>
          </body>
        </html>
      }
    },
    
    Gallery.galleryTemplate := {
      (title, items) => {

        val prefix = sitePath.value
        val head = if(title.nonEmpty) <h1>{title}</h1> else NodeSeq.Empty

        val content = 
          for( Item(_, path, title, descr, date, Some(thumb)) <- items) 
          yield {
            <div class="picture">
              <a href={prefix + path}>
                <img src={prefix + thumb} alt={descr} />
                <p>{title}</p>
              </a>
            </div>
          }

        siteDefaultTemplate.value( title, head ++ content )
      }
    },

    Blog.blogTemplate := {
      (title, items) => {

        val prefix = sitePath.value
        val head = if(title.nonEmpty) <h1>{title}</h1> else NodeSeq.Empty

        val content = 
          for( Item(_, path, title, descr, date, optIcon) <- items) 
          yield {
            <div class="blog-item">
              <h2><a href={prefix + path}>{title}</a></h2>
              <p>{descr}</p>
              <p><span class="datetime">{DisplayDate(date)}</span></p>
            </div>
          }

        siteDefaultTemplate.value( title, head ++ content )
      }
    },

    siteTemplates += Template("*.reveal", 
      (title, content) => {
        <html xmlns={XHTML}>
          <head>
            <title>{title}</title>

            <meta name="apple-mobile-web-app-capable" content="yes" />
            <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>

            <link rel="stylesheet" href="css/reveal.css"/>
            <link rel="stylesheet" href="css/theme/default.css" id="theme"/>
            <link rel="stylesheet" href="css/highlight.css" type="text/css" />
            <link rel="icon" href="favicon.ico" type="image/x-icon" />
            <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />  

            <!--[if lt IE 9]>
            <script src="lib/js/html5shiv.js"></script>
            <![endif]-->
          </head>
          <body>
            <div class="reveal">
              <div class="slides">
              { content }
              </div>
            </div>
            <script src="js/highlight.pack.js"></script>
            <script src="lib/js/head.min.js"></script>
            <script src="js/reveal.min.js"></script>
            <script>hljs.initHighlightingOnLoad()</script>

            <script>
            {"""

              // Full list of configuration options available here:
              // https://github.com/hakimel/reveal.js#configuration
              Reveal.initialize({
                controls: true,
                progress: true,
                history: true,
                center: true,

                theme: Reveal.getQueryHash().theme || 'sky', // available themes are in /css/theme
                transition: Reveal.getQueryHash().transition || 'default', // default/cube/page/concave/zoom/linear/fade/none

                // Optional libraries used to extend on reveal.js
                dependencies: [
                  { src: 'lib/js/classList.js', condition: function() { return !document.body.classList; } },
                  { src: 'plugin/zoom-js/zoom.js', async: true, condition: function() { return !!document.body.classList; } },
                  { src: 'plugin/notes/notes.js', async: true, condition: function() { return !!document.body.classList; } }
                ]
              });

            """}
            </script>
          </body>
        </html>
      }
    )
  )
}
