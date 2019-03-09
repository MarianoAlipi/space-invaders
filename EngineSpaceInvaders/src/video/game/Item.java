package video.game;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public abstract class Item {
    protected int x;        // Stores x position
    protected int y;        // Stores y position
    protected int width;    // The width  
    protected int height;   // The height
    protected Rectangle hitbox; // The hitbox
    protected boolean visible;  // Flag to show or hide the item
    
    /**
     * Set initial values to create the item
     * @param x <b>x</b> position of the object
     * @param y <b>y</b> position of the object
     */
    public Item(int x, int y){
        this.x = x;
        this.y = y;
        this.visible = true;
    }

    public Item(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;
    }    
    
    /**
     * Get x
     * @return x 
     */
    public int getX() {
        return x;
    }
    
    /**
     * Get y
     * @return y 
     */
    public int getY() {
        return y;
    }
    
    /**
     * Get width
     * @return width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Get height
     * @return height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Get hitbox
     * @return hitbox
     */
    public Rectangle getHitbox() {
        return hitbox;
    }

    /**
     * Get visible
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Set x
     * @param x 
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Set y
     * @param y 
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Set width
     * @param width 
     */
    public void setWidth(int width) {
        this.width = width;
    }
    
    /**
     * Set height
     * @param height 
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Set visible
     * @param visible 
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * To update the positions of the item for every tick
     */
    public abstract void tick();
    /**
     * To paint the item
     * @param g <b>Graphics</b> object to paint the item
     */
    public abstract void render(Graphics g);
}
