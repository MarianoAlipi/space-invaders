package video.game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class ImageLoader {
    public static BufferedImage loadImage(String path){
         BufferedImage bi = null;
         try{
           bi = ImageIO.read(ImageLoader.class.getResource(path));
         } catch (IOException ioe){
           System.out.println("Error loading image " + path + ioe.toString());
           System.exit(1);
         }
       return bi;
    }
    
}
