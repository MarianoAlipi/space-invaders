package video.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Game implements Runnable {

    private BufferStrategy bs;
    private Graphics g;
    private Display display;
    String title;
    private int width;
    private int height;
    private Thread thread;
    private boolean running;            //sets up the game
    private boolean paused;             // to pause the game
    private byte gameState;             // flag for the game state. 0: playing 1: lost 2: won
    private int pauseInterval;          // to set an interval for pausing
    private int pauseIntervalCounter;   // to count the frames between pauses
    private Font pauseFont;             // the font for the "PAUSED" text
    private Font scoreFont;             // the font for the score display
    private Player player;              // the  player
    private Shot shot;                  // the player's shot
    private Alien[] aliens;             // the aliens
    private int aliensLeft;             // the number of blocks left
    private int score;                  // the player's score
    private KeyManager keyManager;      //manages the keyboard
    private String fileName;            // save-file's name
    private byte savedLoaded;           // flag to show a saved message for a few frames. 0: none 1: saved 2; loaded
    private int framesCounter;          // to count the duration of the save/loaded message

    /**
     * Constructor
     * @param title
     * @param width
     * @param height 
     */
    public Game(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        running = false;
        paused = false;
        gameState = 0;
        pauseInterval = 10;
        pauseFont = new Font("Arial", Font.BOLD, 70);
        scoreFont = new Font("Arial", Font.BOLD, 30);
        score = 0;
        keyManager = new KeyManager();
        fileName = "SpaceInvaders_save.txt";
        savedLoaded = 0;
        framesCounter = 0;
    }

    /**
     * Initialize the game
     */
    private void init() {
        display = new Display(title, getWidth(), getHeight());
        Assets.init();
        player = new Bar(getWidth() / 2 - 50, getHeight() - 50, 100, 50, this);
        shot = new Shot(getWidth() / 2 - 30, getHeight() - 110, 50, 50, this);

        int blockNo = 0, hits = 3, counter = 0;
        aliensLeft = 48;
        aliens = new Alien[aliensLeft];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                aliens[blockNo] = new Alien(j * 80 + 10, i * 30 + 40, hits, this);
                blockNo++;
            }
            
            // Decrease by one the number of hits every two rows.
            if (++counter >= 2) {
                counter = 0;
                hits = (hits - 1 <= 0) ? 1 : hits - 1;
            }
            
        }

        //starts to listen the keyboard input
        display.getJframe().addKeyListener(keyManager);
    }

    @Override
    public void run() {
        init();
        //frames per second
        int fps = 60;
        double timeTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        while (running) {
            now = System.nanoTime();
            //acumulates times in delta
            delta += (now - lastTime) / timeTick;
            lastTime = now;

            if (delta >= 1) {
                tick();
                render();
                delta--;
            }
        }
        stop();
    }

    private void tick() {
        
        // Get keyboard input
        keyManager.tick();
        
        // Save the game data
        if (keyManager.g) {
            saveData();
        }
        // Load the game data
        if (keyManager.c) {
            loadData();
            setGameState((byte)0);
        }
        
        // To pause the game
        pauseIntervalCounter++;
        if (keyManager.p) {
            if (pauseIntervalCounter > pauseInterval) {
                paused = !paused;
                pauseIntervalCounter = 0;
            }
        }
        
        // If the player won or lost
        if (gameState == 1 || gameState == 2) {
            if (keyManager.r) {
                // Restart the game
                restartGame();
            }
        }
        

        // If not paused and game running (not lost or won)
        if (!paused && gameState == 0) {

            // Move the player with collision
            player.tick();

            // Make the blocks check for collisions with the shot
            for (Alien alien : aliens) {
                if (alien.isVisible()) {
                    alien.tick();
                }
            }
                            
            // Player wins
            if (getAliensLeft() <= 0) {
                // GAME OVER: Player wins
                gameState = 2;
            }

            // Tick the shot
            if (getShot() != null) {
                if (!getShot().isVisible())
                    setShot(null);
                else
                    shot.tick();
            }
        }
        
    }

    private void render() {
        //get the buffer strategy from the display
        bs = display.getCanvas().getBufferStrategy();
        /*if its null, we define one with 3 buffers to display images of the game but 
        after clearing the Rectangle, getting the grapic object frome the buffer 
        strategy element. show the graphic and dispose it to the trash system.
         */
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
        } else {
            g = bs.getDrawGraphics();
            
            // Draw the black background
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            
            player.render(g);
            
            if (getShot() != null)
                shot.render(g);

            for (Alien alien : aliens) {
                if (alien.isVisible()) {
                    alien.render(g);
                }
            }
            
            // Display the score
            g.setFont(scoreFont);
            g.setColor(Color.white);
            g.drawString("Score: " + getScore(), 40, 30);
           
            if (paused) {
                g.setFont(pauseFont);
                g.drawString("PAUSED", getWidth() / 6 + 18, getHeight() / 2);
            }
            
            if (savedLoaded == 1) {
                if (framesCounter++ <= 60)
                    showMessage(g, "SAVED");
                else {
                    savedLoaded = 0;
                    framesCounter = 0;
                }
            } else if (savedLoaded == 2) {
                if (framesCounter++ <= 60)
                    showMessage(g, "LOADED");
                else {
                    savedLoaded = 0;
                    framesCounter = 0;
                }
            }
            
            // If the player lost
            if (gameState == 1) {
                g.setFont(pauseFont);
                g.drawString("GAME OVER", getWidth() / 12, getHeight() / 2); 
                g.setFont(new Font("Arial", Font.PLAIN, 40));
                g.drawString("Press R to restart.", getWidth() / 6 + 15, getHeight() / 2 + 40);
            }
            // If the player won
            else if (gameState == 2) {
                g.setFont(pauseFont);
                g.drawString("YOU WIN!", getWidth() / 6 + 5, getHeight() / 2); 
                g.setFont(new Font("Arial", Font.PLAIN, 40));
                g.drawString("Press R to restart.", getWidth() / 6 + 15, getHeight() / 2 + 40);
            }
            
            // Prevents stutter on Linux.
            Toolkit.getDefaultToolkit().sync();
            bs.show();
            g.dispose();
        }
    }

    public synchronized void start() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    public synchronized void stop() {
        if (running) {
            running = false;
            try {
                //waits till thread dieas
                thread.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    /**
     * Restarts the game.
     */
    public void restartGame() {
        setScore(0);
        setGameState((byte)0);
        setPaused(false);
        player = new Bar(getWidth() / 2 - 50, getHeight() - 50, 100, 50, this);
        setShot(null);
        
        int alienNo = 0;
        aliensLeft = 48;
        aliens = new Alien[aliensLeft];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                aliens[alienNo] = null;
                aliens[alienNo] = new Alien(j * 80 + 10, i * 30 + 40, this);
                alienNo++;
            }
            
        }
    }
    
    /**
     * Saves the data to a file.
     */
    public void saveData() {
        try {
            PrintWriter fileOut = new PrintWriter(new FileWriter(fileName));
        
            // Print score.
            fileOut.println(getScore());
            // Print the player's x and y.
            fileOut.println(getPlayer().getX() + " " + getPlayer().getY());
            // Print the shot's x, y, xSpeed and ySpeed.
            fileOut.println(getShot().getX() + " " + getShot().getY() + " " + getShot().getXSpeed() + " " + getShot().getYSpeed());
            
            /*
            
                IMPLEMENT THIS
            
             */
            
            fileOut.close();
            
            System.out.println("Game saved.");
            savedLoaded = 1;
            
        } catch (IOException ex) {
            System.out.println("ERROR: couldn't save data.");
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Reads the saved data from a file.
     */
    public void loadData() {
        BufferedReader fileIn;
        try {
            fileIn = new BufferedReader(new FileReader(fileName));
            
            try {
                String line;
                
                // Score
                line = fileIn.readLine();
                setScore( Integer.parseInt(line) );
                
                String values[];
                
                // Player x and y
                line = fileIn.readLine();
                values = line.split(" ");
                getPlayer().setX( Integer.parseInt(values[0]) );
                getPlayer().setY( Integer.parseInt(values[1]) );
                
                // Shot x, y, xSpeed, ySpeed
                line = fileIn.readLine();
                values = line.split(" ");
                
                getShot().setX( Integer.parseInt(values[0]) );
                getShot().setY( Integer.parseInt(values[1]) );
                
                // Aliens' position and visible flag
                line = fileIn.readLine();
                values = line.split(" ");
                
                setAliensLeft(aliens.length);
                
                /*
                    
                    IMPLEMENT THIS
                
                */
                
                fileIn.close();
                
                savedLoaded = 2;
                
            } catch (IOException ex) {
                System.out.println("ERROR: IOException");
                ex.printStackTrace();
            }
            
        } catch (FileNotFoundException e) {
            // The file doesn't exist.
        }
    }
    
    /**
     * To display titles in the middle of the screen
     * @param g
     * @param text
     */
    public void showMessage(Graphics g, String text) {
        g.setColor(Color.black);
        g.setFont(pauseFont);
        
        g.drawString(text, getWidth() / 5, getHeight() / 2 + 60);
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
     * Get the player
     *
     * @return
     */
    public Bar getPlayer() {
        return player;
    }

    /**
     * Get the shot
     * @return shot
     */
    public Shot getShot() {
        return shot;
    }

    /**
     * Get keyManager
     * @return keyManager
     */
    public KeyManager getKeyManager() {
        return keyManager;
    }
    
    /**
     * Get score
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Get aliensLeft
     * @return aliensLeft
     */
    public int getAliensLeft() {
        return aliensLeft;
    }

    /**
     * Get gameState
     * @return 
     */
    public byte getGameState() {
        return gameState;
    }
    
    /**
     * Set shot
     * @param shot 
     */
    setShot(Shot shot) {
        return shot;
    }
    
    /**
     * Set score
     * @param score 
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Set aliensLeft
     * @param aliensLeft 
     */
    public void setAliensLeft(int aliensLeft) {
        this.aliensLeft = aliensLeft;
    }

    /**
     * Set paused
     * @param paused 
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
    /**
     * Set gameState
     * @param gameState 
     */
    public void setGameState(byte gameState) {
        this.gameState = gameState;
    }
}
