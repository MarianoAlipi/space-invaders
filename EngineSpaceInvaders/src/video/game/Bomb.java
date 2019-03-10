package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Bomb extends Item {
    
    private double speed;              // the bomb's speed
    private SoundClip sound;           // the bomb's sound effect
    private Game game;                 // the running game
    
    /**
     * Constructor for the bomb
     * @param x
     * @param y
     * @param game 
     */
    public Bomb(int x, int y, Game game) {
      //send x and y to the Item constructor
      super(x, y);
      this.width = 2;
      this.height = 5;
      this.game = game;
      this.speed = 1;
      this.visible = true;
      this.hitbox = new Rectangle(x, y, width, height);
      this.sound = new SoundClip("/sounds/explosion.wav");
    }
    
    /**
     * Get speed
     * @return speed
     */
    public double getSpeed() {
        return speed;
    }
    
    /**
     * Set speed
     * @param speed 
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    @Override
    public void tick() {
        
        // Move bomb
        setY( (int)(getY() + getSpeed()) );
        
        // Hide if out of screen
        if (getX() < 0 || getX() + getWidth() > game.getWidth() || getY() < 0 || getY() + getHeight() > 290 - 5) {
            setSpeed(0);
            setVisible(false);
        }
        
        // Relocate hitbox
        hitbox.setLocation(getX(), getY());
        
        // Check for collision with player
        if (getHitbox().intersects(game.getPlayer().getHitbox())) {
            setVisible(false);
            sound.play();
            game.setGameState(Game.GameState.LOST);
        }
    }
    
    // Display the bomb
    @Override
    public void render(Graphics g) {
        g.drawImage(Assets.bomb, getX(), getY(), getWidth(), getHeight(), null);
      
        // Draw the hitbox (for debugging)
        // g.drawRect((int)getHitbox().getX(), (int)getHitbox().getY(), (int)getHitbox().getWidth(), (int)getHitbox().getHeight());
    }
}