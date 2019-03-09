package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;
/**
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Alien extends Item {
    
    private Game game;
    private boolean visible;        // to show or hide the alien whether it's dead or not
    private boolean dying;          // to indicate if the alien has been hit and is in the process of disappearing
    private enum Direction {left, right}; // to indicate the current direction in which aliens are moving
    private Direction direction;
    private Bomb bomb;              // the alien's bomb
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
        this.direction = Direction.left;
        this.game = g;
        this.hitbox = new Rectangle(getX(), getY(), getWidth(), getHeight());
        this.dying = false;
        this.bomb = null;
        this.framesCounter = 0;
        this.explosion = new SoundClip("/sounds/explosion.wav");
    }

    @Override
    public void tick() {
        // Check for collision with shot
        if (!isDying()) {
            
            // Relocate the hitbox.
            hitbox.setLocation(getX(), getY());
            
            if (game.getShot() != null) {
                if (getHitbox().intersects(game.getShot().getHitbox())) {
                    setDying(true);
                    game.setShot(null);
                    explosion.play();
                }
            }
            
            // To move downwards every time the aliens reach a border
            // Right border
            if ((getX() >= game.getWidth() - 30) && (getDirection() != Direction.left)) {
                setDirection(Direction.left);
                setY(getY() + 15); // 15 is the offset to move downwards
            }
            // Left border
            if ((getX() <= 5) && (getDirection() != Direction.right)) {
                setDirection(Direction.right);
                setY(getY() + 15); // 15 is the offset to move downwards
            }
            
            // If the aliens reach the green line, game over.
            if (getY() > 290 - getHeight()) {
                    game.setGameState(Game.GameState.LOST);
            }
            
            // Drop bombs randomly
            Random generator = new Random();
            if ((generator.nextInt(25) == 5) && (bomb == null)) {
                    bomb = new Bomb(getX(), getY(), game);
            }
            
            // Tick bomb (if it exists)
            if (bomb != null) {
                if (bomb.isVisible())
                    bomb.tick();
                else
                    bomb = null;
            }
            
            // Move alien horizontally
            setX( getX() + (getDirection() == Direction.left ? -1 : 1 ) );
            
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
        
        // Render bomb (if it exists)
        if (bomb != null) {
            if (bomb.isVisible())
                bomb.render(g);
        }
        
        // Draw hitbox (for debugging)
        // g.setColor(Color.red);
        // g.drawRect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
    }

    /**
     * Get dying
     * @return dying
     */
    public boolean isDying() {
        return dying;
    }

    /**
     * Get direction
     * @return direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Set dying
     * @param dying 
     */
    public void setDying(boolean dying) {
        this.dying = dying;
    }

    /**
     * Set direction
     * @param direction 
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
