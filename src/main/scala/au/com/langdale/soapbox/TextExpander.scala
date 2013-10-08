package au.com.langdale.soapbox
  
import scala.xml.{NodeSeq, Text, Node}
import scala.util.matching.Regex.Match
import scala.util.parsing.combinator.RegexParsers
import Util._

import sbt._
import Publisher._

object TextExpander extends Plugin {

  val linkWordsAndURIs = taskKey[String => NodeSeq]("function that expands text with wiki style links producing HTML")

  override def projectSettings = Seq(

    linkWordsAndURIs := new Expander {
      val pageNames = sitePages.value.toSet
      val linkedTerms = siteTerms.value
      val imageNames = siteImages.value.toSet
      val localPrefix = sitePath.value
    }
  )
  
  private trait Expander extends RegexParsers with (String => NodeSeq) {

    val pageNames: Set[String]
    val linkedTerms: Map[String, String]
    val imageNames: Set[String]
    val localPrefix: String

    override def skipWhitespace = false
    
    def ws = regex(whiteSpace) ^^ { x => Text(x) }
    
    def other = regex("""\S+""".r) ^^ { x => Text(x) }
    
    def word = regex("""\w+""".r)

    def reference = regex("""(\w|-)+(\.(\w|-)+)+(/[^]\[}{>< ]+)?""".r) 
    
    def uri = regex("""https?://[^]\[}{>< ]+""".r)
    
    val imageNameRE = """\w+\.(jpg|png|gif)""".r
    
    def imageName = regex(imageNameRE)
    
    def imageLink = "[" ~ imageName ~ "]" ^? {
      case _ ~ n ~ _ if imageNames contains n => <img src={n}/>  
    }
    
    def uriLink = uri ^^ {
      u =>  <a href={u}>{u}</a>
    }
    
    def refLink = word ~ opt(ws) ~ "[" ~ reference ~ "]" ^? {
      case word ~ _ ~ _ ~ r ~ _ if imageNameRE.unapplySeq(r).isEmpty => 
        <a href={"http://" + r}>{word}</a>
    }
    
    def wordLink = word ^? {
      case w  if linkedTerms contains w => 
        <a href={localPrefix + linkedTerms(w)} class="internal">{expandWord(w)}</a>
      case w if pageNames contains w + ".html" => 
        <a href={localPrefix + w + ".html"} class="internal">{expandWord(w)}</a>
    }
    
    def part: Parser[NodeSeq] = imageLink | refLink | uriLink | wordLink | ws | other
    
    def whole: Parser[NodeSeq] = rep(part) ^^ { _.reduceLeft( _ ++ _ ) }
    
    def apply(text: String): NodeSeq = parse(whole, text).get
  }
  
  /**
   * Prettify a wikiWord for presentation by replacing underscores.
   */
  private def expandWord( word: String) = {
    if( word contains "_" )
      word.replace("_",  " ")
    else
      tokenize(CamelHump, word).mkString( " " )  
  }

  private val CamelHump = """\p{Lu}+[^\p{Lu}]*""".r

  import scala.util.matching.Regex
  import scala.collection.mutable.ListBuffer

  private def tokenize(regex: Regex, text: String) = {
    val b = new ListBuffer[String]
    var offset = 0
    for( m <- regex.findAllIn(text).matchData) {
        if( offset < m.start)
          b += text.substring(offset, m.start)
        b += m.matched
        offset = m.end  
    }
    if( offset == 0)
      List(text)
    else {
      if( offset < text.length)
        b += text.substring(offset)
      b.toList
    }
 }


}
