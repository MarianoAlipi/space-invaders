/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package video.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Block extends Item {
    
    private Game game;
    private int hits;           // the number of hits left to destroy the block
    private boolean visible;    // to show or hide the block whether it's destroyed or not
    
    private Rectangle hitboxSides;  // the sides' hitbox
    private Rectangle hitboxUpDown; // the top and bottom hitbox
    private SoundClip explosion;    // sound effect for when the block breaks
    
    /**
     * Constructor for the blocks
     * @param x
     * @param y
     * @param hits
     * @param g 
     */
    public Block(int x, int y, int hits, Game g) {
        super(x, y);
        this.width = 75;
        this.height = 25;
        this.hits = hits;
        this.game = g;
        this.hitboxSides = new Rectangle(x, y + (height / 6), width, (int) (height * 0.7));
        this.hitboxUpDown = new Rectangle(x + (width / 15), y, (int) (width * 0.87), height);
        this.explosion = new SoundClip("/sounds/explosion.wav");
        this.visible = true;
    }

    @Override
    public void tick() {
        // Check for collision with ball
        
        // To limit the ball to only one hit per tick
        if (!game.getBall().isCollision()) {
             // Check for top and bottom collision
            if (getHitboxUpDown().intersects(game.getBall().getHitbox())) {
                if (--hits <= 0) {
                    setVisible(false);
                    game.setBlocksLeft(game.getBlocksLeft() - 1);
                    
                    if (Math.floor(Math.random() * 5) == 0)
                        game.setPower(new Power(getX() + getWidth() / 2, getY(), 32, 32, Power.Type.values()[(int)Math.floor(Math.random() * Power.Type.values().length)], game));
                    
                }

                game.getBall().setYSpeed(-1 * game.getBall().getYSpeed());
                game.getBall().setCollision(true);
                game.setScore(game.getScore() + 1);
            }
            // Check for side collision
            else if (getHitboxSides().intersects(game.getBall().getHitbox())) {
                if (--hits <= 0) {
                    setVisible(false);
                    game.setBlocksLeft(game.getBlocksLeft() - 1);
                }

                game.getBall().setXSpeed(-1 * game.getBall().getXSpeed());
                game.getBall().setCollision(true);
                game.setScore(game.getScore() + 1);
            }
            
            if (hits <= 0)
                explosion.play();
        }
    }

    @Override
    public void render(Graphics g) {
        
        g.setColor(Color.black);
        g.drawRect(x, y, width, height);
        
        g.drawImage(Assets.meth[getHits() - 1], getX(), getY(), getWidth(), getHeight(), null);
    }
    
    /**
     * Get the sides' hitbox
     * @return hitboxSides
     */
    public Rectangle getHitboxSides() {
        return hitboxSides;
    }

    /**
     * Get the top and bottom hitbox
     * @return hitboxUpDown
     */
    public Rectangle getHitboxUpDown() {
        return hitboxUpDown;
    }

    /**
     * Get hits
     * @return hits
     */
    public int getHits() {
        return hits;
    }

    /**
     * Get visible
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set hits
     * @param hits 
     */
    public void setHits(int hits) {
        this.hits = hits;
    }

    /**
     * Set visible
     * @param visible 
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
