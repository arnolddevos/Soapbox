# Soapbox Task and Setting Reference

## disqusID

Task type:
:    `String` 


Description:
:    A disqus forum ID

## disqusFragment

Task type:
:    `NodeSeq` 


Description:
:    The disqus forum code fragment to be included in a page

## markdownBuild

Task type:
:    `Seq[File]` 


Description:
:    Convert Markdown sources to HTML yielding result files

## markdownItems

Task type:
:    `Seq[Item]` 


Description:
:    Convert Markdown sources to HTML

## markdownSources

Task type:
:    `Seq[File]` 


Description:
:    directories for Markdown source files

## markdownFilter

Task type:
:    `FileFilter` 


Description:
:    wildcard expression matching Markdown file names

## siteSources

Setting type:
:    `Seq[File]` 


Description:
:    Directories for source files to be transformed into a web site

## siteProduct

Setting type:
:    `File` 


Description:
:    Directory containing the generated web site

## siteProtocol

Setting type:
:    `String` 


Description:
:    site protocol, http or https

## siteDomain

Setting type:
:    `String` 


Description:
:    site domain name

## sitePath

Setting type:
:    `String` 


Description:
:    path component of site URL

## sitePrefix

Setting type:
:    `String` 


Description:
:    prefix of all URLs in this site, including domain and path

## sitePages

Task type:
:    `Seq[String]` 


Description:
:    relative pathnames for all HTML pages

## siteImages

Task type:
:    `Seq[String]` 


Description:
:    relative pathnames for all images

## siteResources

Task type:
:    `Seq[String]` 


Description:
:    relative pathnames for other resources in the site

## siteTerms

Task type:
:    `Map[String,String]` 


Description:
:    mapping from phrases to page pathnames for wiki hyperlinking

## siteMenu

Task type:
:    `Menu` 


Description:
:    site wide menu

## siteFooter

Task type:
:    `NodeSeq` 


Description:
:    site wide footer

## siteTemplates

Task type:
:    `Seq[Template]` 


Description:
:    search list of templates

## siteBuild

Task type:
:    `Seq[File]` 


Description:
:    master task to build a site (runs siteTasks)

## gallerySources

Task type:
:    `Seq[File]` 


Description:
:    directory for images to form a gallery

## galleryThumbSize

Setting type:
:    `Int` 


Description:
:    the size of thumbnails in the gallery

## galleryItems

Task type:
:    `Seq[Item]` 


Description:
:    images and metadata making up a gallery

## galleryFilter

Task type:
:    `FileFilter` 


Description:
:    wildcard expression matching image file names

## galleryTitle

Task type:
:    `String` 


Description:
:    a title for the gallery

## galleryName

Setting type:
:    `String` 


Description:
:    the pathname for the gallery page

## galleryBuild

Task type:
:    `Seq[File]` 


Description:
:    create a gallery

## scrapeBuild

Task type:
:    `Seq[File]` 


Description:
:    Convert HTML to Markdown

## scrapeSources

Task type:
:    `Seq[File]` 


Description:
:    directories for HTML source files

## scrapeProduct

Task type:
:    `File` 


Description:
:    directory for generated markdown files

## scrapeFilter

Task type:
:    `FileFilter` 


Description:
:    wildcard expression matching HTML file names

## scrapeBases

Setting type:
:    `Seq[URI]` 


Description:
:    base URIs. each extracted URI will be made relative to one of these if possible

## scrapeCodeLanguage

Setting type:
:    `String` 


Description:
:    the programming language assumed for all code blocks

## scrapeAddTitle

Setting type:
:    `Boolean` 


Description:
:    if true, a top level heading is added with the html title text

## scrapeContentId

Setting type:
:    `Option[String]` 


Description:
:    if given, only the element with this id is extracted

## blogTitle

Task type:
:    `String` 


Description:
:    title text appearing on the blog

## blogSources

Task type:
:    `Seq[File]` 


Description:
:    Directories containing publication lists.

## blogFilter

Task type:
:    `FileFilter` 


Description:
:    Pattern to match a publication list containing lines formated 'yyyy-MM-dd path'

## blogItems

Task type:
:    `Seq[Item]` 


Description:
:    candidate items for the blog

## blogContents

Task type:
:    `Seq[Item]` 


Description:
:    filtered and sorted items for the blog

## blogPath

Task type:
:    `String` 


Description:
:    the path name of the blog page

## blogBuild

Task type:
:    `Seq[File]` 


Description:
:    build the blog

## analyticsDomain

Task type:
:    `String` 


Description:
:    The family domain for google analytics

## analyticsTracker

Task type:
:    `String` 


Description:
:    The google analytics tracker ID

## analyticsFragment

Task type:
:    `NodeSeq` 


Description:
:    The google analytics code fragment needed on each page to be tracked

## tocName

Task type:
:    `String` 


Description:
:    the path name for the generated eclipse table of contents

## tocTitle

Task type:
:    `String` 


Description:
:    the title for the generated table of contents

## tocSource

Task type:
:    `String` 


Description:
:    the path name in the generated site of an HTML page from which an eclipse table of contents will be extracted

## tocBuild

Task type:
:    `Seq[File]` 


Description:
:    create an eclipse help table of contents file

## twitterID

Task type:
:    `String` 


Description:
:    A twitter handle.

## tweetScript

Task type:
:    `NodeSeq` 


Description:
:    The tweeting script to be included in the page head.

## tweetButton

Task type:
:    `NodeSeq` 


Description:
:    The tweet button to include one or more times in the page body.

## resourceBuild

Task type:
:    `Seq[File]` 


Description:
:    copy resource files into site

## resourceSources

Task type:
:    `Seq[File]` 


Description:
:    directories for resource source files

## resourceLibraries

Task type:
:    `File` 


Description:
:    directory for third party resource libraries. each subdirectory will be added to resourceSources

## resourceFilter

Task type:
:    `FileFilter` 


Description:
:    wildcard expression matching resource file names

## resourceImageFilter

Task type:
:    `FileFilter` 


Description:
:    wildcard expression matching image file names

## resourcePageFilter

Task type:
:    `FileFilter` 


Description:
:    wildcard expression matching HTML file names

## resourceExclusions

Task type:
:    `PathFinder` 


Description:
:    specifies paths to ignore when copying resources

