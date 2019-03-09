package video.game;

import java.awt.image.BufferedImage;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Assets {

    public static BufferedImage background;
    public static BufferedImage player;
    public static BufferedImage alien;
    public static BufferedImage shot;
    public static BufferedImage bomb;
    public static BufferedImage explosion;
            
    public static void init() {
        background = ImageLoader.loadImage("/images/background.png");
        player = ImageLoader.loadImage("/images/player.png");
        alien = ImageLoader.loadImage("/images/alien.png");
        shot = ImageLoader.loadImage("/images/shot.png");
        bomb = ImageLoader.loadImage("/images/bomb.png");
        explosion = ImageLoader.loadImage("/images/explosion.png");
    }
}

