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
            <link rel="stylesheet" href="graphic.css" type="text/css" />
            <link rel="stylesheet" href="highlight.css" type="text/css" />
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
            <script src="highlight.pack.js"></script>
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
    }
  )
}
