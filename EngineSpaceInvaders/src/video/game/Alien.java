package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Alien extends Item {
    
    private Game game;
    private boolean visible;        // to show or hide the alien whether it's dead or not
    private boolean dying;          // to indicate if the alien has been hit and is in the process of disappearing
    private SoundClip explosion;    // sound effect for when the alien is killed
    private int framesCounter;      // to count a number of frames to show a different sprite
    
    /**
     * Constructor for the aliens
     * @param x
     * @param y
     * @param g 
     */
    public Alien(int x, int y, Game g) {
        super(x, y);
        this.width = 12;
        this.height = 12;
        this.game = g;
        this.hitbox = new Rectangle(getX(), getY(), getWidth(), getHeight());
        this.dying = false;
        this.framesCounter = 0;
        this.explosion = new SoundClip("/sounds/explosion.wav");
    }

    @Override
    public void tick() {
        // Check for collision with shot
        if (!isDying()) {
            if (game.getShot() != null) {
                if (getHitbox().intersects(game.getShot().getHitbox())) {
                    setDying(true);
                    game.setShot(null);
                    explosion.play();
                }
            }
        } else {
            if (framesCounter++ >= 5) {
                setVisible(false);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (!isDying())
            g.drawImage(Assets.alien, getX(), getY(), getWidth(), getHeight(), null);
        else
            g.drawImage(Assets.explosion, getX(), getY(), getWidth(), getHeight(), null);
        
        // Draw hitbox (for debugging)
        //g.setColor(Color.red);
        //g.drawRect(x, y, width, height);
    }

    /**
     * Get dying
     * @return dying
     */
    public boolean isDying() {
        return dying;
    }

    /**
     * Set dying
     * @param dying 
     */
    public void setDying(boolean dying) {
        this.dying = dying;
    }
}
