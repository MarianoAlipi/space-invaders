package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Ball extends Item{
    
    private double xSpeed, ySpeed, speed;   // the ball's horizontal, vertical and total speed
    private boolean collision;              // to check if there's a collision this frame
    private Animation grenadeRotate;        // the animation to rotate the ball
    private SoundClip bounce;               // sound effect when the ball bounces off the bar
    private Game game;                      // the running game
    
    /**
     * Constructor for the ball
     * @param x
     * @param y
     * @param width
     * @param height
     * @param game 
     */
    public Ball(int x, int y, int width, int height, Game game) {
      //send x and y to the Item constructor
      super(x, y);
      this.width = width;
      this.height = height;
      this.game = game;
      this.xSpeed = 0; // 5
      this.ySpeed = 0; // 6
      this.speed = Math.sqrt( (xSpeed * xSpeed) + (ySpeed * ySpeed) );
      this.collision = false;
      this.hitbox = new Rectangle(x + 12, y + 12, width - 22, height - 22);
      this.bounce = new SoundClip("/sounds/bounce.wav");
      this.grenadeRotate = new Animation(Assets.grenadeRotate,100);
    }
    
    /**
     * Get xSpeed
     * @return xSpeed
     */
    public double getXSpeed() {
        return xSpeed;
    }
    
    /**
     * Get ySpeed
     * @return ySpeed
     */
    public double getYSpeed() {
        return ySpeed;
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
     * Set xSpeed
     * @param xSpeed 
     */
    public void setXSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }
    
    /**
     * Set ySpeed
     * @param ySpeed 
     */
    public void setYSpeed(double ySpeed) {
        this.ySpeed = ySpeed;
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
        
        // Move ball
        setX( getX() + (int)getXSpeed() );
        setY( getY() + (int)getYSpeed() );
        
        // Reset position and bounce if out of screen
        if (getX() + getWidth() > game.getWidth()) {
            setX(game.getWidth() - getWidth());
            setXSpeed(-1 * getXSpeed());
            bounce.play();
        } else if (getX() < 0) {
            setX(0);
            setXSpeed(-1 * getXSpeed());
            bounce.play();
        }
        if (getY() + getHeight() > game.getHeight()) {
            game.setGameState((byte)1);
            bounce.play();
        } else if (getY() < 0) {
            setY(0);
            setYSpeed(-1 * getYSpeed());
        }
        
        // Relocate hitbox
        hitbox.setLocation(getX() + 12, getY() + 12);
        setCollision(false);
        
        //update collision animation
        this.grenadeRotate.tick();
        
        // Check for collision with bar
        // The farther from the center, the more horizontal the angle is.
        // The closer to the center, the more vertical the angle is.
        // Left side launches the ball to the left.
        // Right side launches the ball to the right.
        if (getHitbox().intersects(game.getPlayer().getHitbox())) {
            setYSpeed(-1 * Math.abs(getYSpeed()));
            
            // Get the bar's width.
            int barWidth = (int) game.getPlayer().getHitbox().getWidth();
            
            // Calculate the position where the ball hit the bar.
            int pos = (getX() + getWidth() / 2) - (int)game.getPlayer().getHitbox().getX();
            
            // If the position is off limits, set it to the limit.
            if (pos < 0)
                pos = 0;
            else if (pos > barWidth)
                pos = barWidth;
            
            // The resulting angle the ball will be bounced at.
            double angle;
            // Flag: bounced on the left or right side?
            boolean left = false;
            
            // Check if the ball hit the bar's left or right side.
            if (pos <= barWidth / 2) {
                left = true;
                // Calculate the new angle.
                angle = (pos / (barWidth / 2.0) ) * 40 + 30;
            } else {
                pos -= barWidth / 2;
                // Calculate the new angle.
                angle = ( ( barWidth / 2.0 - pos) / (barWidth / 2.0) ) * 40 + 30;
            }
            
            // Convert the angle from degrees to radians.
            angle = Math.toRadians(angle);
            
            // Update x and y speed.
            setXSpeed( (left ? -1 : 1) * getSpeed() * Math.cos(angle) );
            setYSpeed( -1 * getSpeed() * Math.sin(angle) );
            bounce.play();
        }
    }
    
    //displays aka renders
    @Override
    public void render(Graphics g) {
        g.drawImage(grenadeRotate.getCurrentFrame(), getX(), getY(), getWidth(), getHeight(), null);
      
        // Draw the hitbox (for debugging)
        // g.drawRect((int)getHitbox().getX(), (int)getHitbox().getY(), (int)getHitbox().getWidth(), (int)getHitbox().getHeight());
    }
}