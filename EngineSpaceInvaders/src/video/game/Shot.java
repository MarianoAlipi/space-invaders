package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Shot extends Item {
    
    private double speed;              // the shot's speed
    private boolean collision;         // to check if there's a collision this frame
    private SoundClip sound;           // the shot's sound effect
    private Game game;                 // the running game
    
    /**
     * Constructor for the shot
     * @param x
     * @param y
     * @param game 
     */
    public Shot(int x, int y, Game game) {
      //send x and y to the Item constructor
      super(x, y);
      this.width = 1;
      this.height = 5;
      this.game = game;
      this.speed = 4;
      this.collision = false;
      this.visible = true;
      this.hitbox = new Rectangle(x, y, width, height);
      this.sound = new SoundClip("/sounds/pew.wav");
      sound.play();
    }
    
    /**
     * Get speed
     * @return speed
     */
    public double getSpeed() {
        return speed;
    }
    
    /**
     * Get collision
     * @return collision
     */
    public boolean isCollision() {
        return collision;
    }
    
    /**
     * Set speed
     * @param speed 
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    /**
     * Set collision
     * @param collision 
     */
    public void setCollision(boolean collision) {
        this.collision = collision;
    }
    
    //tick is used to check bar
    @Override
    public void tick() {
        
        // Move shot
        setY( (int)(getY() - getSpeed()) );
        
        // Hide if out of screen
        if (getX() < 0 || getX() + getWidth() > game.getWidth() || getY() < 0 || getY() + getHeight() > game.getHeight()) {
            setSpeed(0);
            setVisible(false);
        }
        
        // Relocate hitbox
        hitbox.setLocation(getX(), getY());
        setCollision(false);
    }
    
    //displays aka renders
    @Override
    public void render(Graphics g) {
        g.drawImage(Assets.shot, getX(), getY(), getWidth(), getHeight(), null);
      
        // Draw the hitbox (for debugging)
        // g.drawRect((int)getHitbox().getX(), (int)getHitbox().getY(), (int)getHitbox().getWidth(), (int)getHitbox().getHeight());
    }
}