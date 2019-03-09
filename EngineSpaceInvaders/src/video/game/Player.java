package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;
/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Player extends Item {
    
    private int speed;  // the player's speed
    private Game game;  // the running game
    
    /**
     * Constructor for the player
     * @param x
     * @param y
     * @param width
     * @param height
     * @param game 
     */
    public Player(int x, int y, int width, int height, Game game){
      //send x and y to the Item constructor
      super(x, y);
      this.width = width;
      this.height = height;
      this.game = game;
      this.speed = 2;
      this.hitbox = new Rectangle(x, y, width, height);
    }
    
    /**
     * Get speed
     * @return speed
     */
    public int getSpeed() {
        return speed;
    }
    
    /**
     * Set speed
     * @param speed 
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    // tick() is used to tick the player
    @Override
    public void tick() {
        
        // Move the player depending on flags
        if (game.getKeyManager().left) {
          setX(getX() - getSpeed());
        }
        if (game.getKeyManager().right) {
          setX(getX() + getSpeed());
        }
        
        // Shoot
        if (game.getKeyManager().space) {
            if (game.getShot() == null)
                game.setShot(new Shot(getX() + getWidth() / 2, getY() - 1, game));
        }
        
      
        // Reset x position and y position if collision with screen limits
        if (getX() + getWidth() > game.getWidth() - 5) {
          setX(game.getWidth() - getWidth() - 5);
        } else if (getX() < 5) {
          setX(5);
        }
        
        // Relocate hitbox
        hitbox.setLocation(getX(), getY());
    }
    
    @Override
    public void render(Graphics g){
        g.drawImage(Assets.player, getX(), getY(), getWidth(), getHeight(), null);
        // Draw the hitbox (for debugging)
        // g.drawRect((int)getHitbox().getX(), (int)getHitbox().getY(), (int)getHitbox().getWidth(), (int)getHitbox().getHeight());
    }

}
