package au.com.langdale.soapbox

import java.awt.image.BufferedImage
import java.awt.RenderingHints
import javax.imageio.ImageIO
import java.io.File

/**
 * Read write and resize images.
 */
object ImageUtil {
  implicit class RichImage( image: BufferedImage ) {
    
    def writeJPEG(f: File) { ImageIO.write( image, "jpg", f) }
    
    def fit(size: Int) = {
      val (iw, ih) = (image.getWidth, image.getHeight)
      
      val (w, h) = {
        if( iw <= size && ih <= size )
          (iw, ih)
        else if( iw > ih )
          (size, (size.toDouble/iw*ih).toInt)
        else
          ((size.toDouble/ih*iw).toInt, size)  
      }
      
      val result = new BufferedImage(w, h, image.getType)
      val g = result.createGraphics
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      g.drawImage(image, 0, 0, w, h, 0, 0, iw, ih, null)
      g.dispose
      result
    }
  }
  
  def readImage(f: File): BufferedImage = ImageIO.read(f)
}
