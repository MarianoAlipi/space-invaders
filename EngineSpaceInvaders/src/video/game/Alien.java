package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Alien extends Item {
    
    private Game game;
    private boolean visible;    // to show or hide the alien whether it's dead or not
    private SoundClip explosion;    // sound effect for when the alien is killed
    
    /**
     * Constructor for the aliens
     * @param x
     * @param y
     * @param hits
     * @param g 
     */
    public Alien(int x, int y, int hits, Game g) {
        super(x, y);
        this.width = 12;
        this.height = 12;
        this.game = g;
        this.hitbox = new Rectangle(getX(), getY(), getWidth(), getHeight());
        this.explosion = new SoundClip("/sounds/explosion.wav");
        this.visible = true;
    }

    @Override
    public void tick() {
        // Check for collision with shot
        if (getHitbox().intersects(game.getShot().getHitbox())) {
            setVisible(false);
            explosion.play();
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(Assets.alien, getX(), getY(), getWidth(), getHeight(), null);
    }

    /**
     * Get visible
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set visible
     * @param visible 
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
