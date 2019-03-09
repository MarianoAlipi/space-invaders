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
    public enum GameState {PLAYING, LOST, WON};
    private GameState gameState;        // flag for the game state.
    private int pauseInterval;          // to set an interval for pausing
    private int pauseIntervalCounter;   // to count the frames between pauses
    private Font gameFont;              // the game's font
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
        gameState = GameState.PLAYING;
        pauseInterval = 10;
        gameFont = new Font("Helvetica", Font.BOLD, 14);
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
        player = new Player(270, 280, 15, 10, this);
        setShot(null);

        int alienNo = 0;
        aliensLeft = 24;
        aliens = new Alien[aliensLeft];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                aliens[alienNo] = new Alien(150 + 18 * j, 5 + 18 * i, this);
                alienNo++;
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
            setGameState(GameState.PLAYING);
        }
        
        // Restart the game
        if (keyManager.r) {
            restartGame();
        }
        
        // To pause the game
        pauseIntervalCounter++;
        if (keyManager.p) {
            if (pauseIntervalCounter > pauseInterval) {
                paused = !paused;
                pauseIntervalCounter = 0;
            }
        }
        
        // If not paused and game running (not lost or won)
        if (!paused && getGameState() == GameState.PLAYING) {

            // Move the player and handle events
            player.tick();

            for (Alien alien : aliens) {
                if (alien.isVisible()) {
                    // Make the aliens check for collisions and shoot bombs
                    alien.tick();
                    
                    // Make the aliens go down when reaching the left wall
                    if (alien.getX() <= 5 && alien.getDirection() == Alien.Direction.left) {
                        for (Alien alien2 : aliens) {
                            alien2.setY(alien2.getY() + 15);
                            alien2.setDirection(Alien.Direction.right);
                        }
                    // Make the aliens go down when reaching the right wall
                    } else if (alien.getX() >= getWidth() - 30 && alien.getDirection() == Alien.Direction.right) {
                        for (Alien alien2 : aliens) {
                            alien2.setY(alien2.getY() + 15);
                            alien2.setDirection(Alien.Direction.left);
                        }
                    }
                }
                // Tick the bomb (if it exists)
                if (alien.getBomb() != null) {
                    if (alien.getBomb().isVisible())
                        alien.getBomb().tick();
                    else
                        alien.setBomb(null);
                }
            }
            
            // Player wins
            if (getAliensLeft() <= 0) {
                // GAME OVER: Player wins
                setGameState(GameState.WON);
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
            
            // Draw the green horizontal line
            g.setColor(Color.green);
            g.drawLine(0, 290, width, 290);
            
            // Draw the player
            player.render(g);
           
            // Draw the shot
            if (getShot() != null)
                shot.render(g);

            // Draw the aliens
            for (Alien alien : aliens) {
                if (alien.isVisible()) {
                    alien.render(g);
                }
                // Draw the bomb (if it exists)
                if (alien.getBomb() != null) {
                    if (alien.getBomb().isVisible())
                        alien.getBomb().render(g);
                    else
                        alien.setBomb(null);
                }
            }
            
            // Display the score
            g.setFont(gameFont);
            g.setColor(Color.white);
            g.drawString("SCORE: " + getScore(), 5, getHeight() - 5);
           
            // Display "PAUSED"
            if (paused) {
                showMessage(g, "PAUSED");
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
            if (getGameState() == GameState.LOST) {
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());
                showMessage(g, "GAME OVER");
                g.drawString("Press R to restart.", getWidth() / 2 - 70, getHeight() / 2 + 18);
                g.drawString("SCORE: " + getScore(), getWidth() / 2 - 35, getHeight() / 2 + 50);
            }
            // If the player won
            else if (getGameState() == GameState.WON) {
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());
                showMessage(g, "YOU WIN!");
                g.drawString("Press R to restart.", getWidth() / 2 - 70, getHeight() / 2 + 18);
                g.drawString("SCORE: " + getScore(), getWidth() / 2 - 35, getHeight() / 2 + 50);
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
        setGameState(GameState.PLAYING);
        setPaused(false);
        player = new Player(270, 280, 15, 10, this);
        setShot(null);
        
        int alienNo = 0;
        setAliensLeft(24);
        aliens = new Alien[aliensLeft];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                aliens[alienNo] = new Alien(150 + 18 * j, 5 + 18 * i, this);
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
            // Print the player's x position.
            fileOut.print(getPlayer().getX() + " ");
            // Print if shot is visible (1) or not (0) and its x and y position.
            if (getShot() != null) {
                fileOut.println(1 + " " + getShot().getX() + " " + getShot().getY());
            } else {
                fileOut.println("0 0 0");
            }
            
            // Print the aliens' data.
            for (Alien alien : aliens) {
                // Print if the alien is visible (1) or not (0).
                if (alien.isDying() || !alien.isVisible()) {
                    fileOut.print(0 + " ");
                } else if (alien.isVisible()) {
                    fileOut.print(1 + " ");
                }
                // Print the alien's x, y and direction.
                fileOut.println(alien.getX() + " " + alien.getY() + " " + (alien.getDirection() == Alien.Direction.left ? 0 : 1));
                
                // Print the bomb's data.
                // Print if the bomb is visible (1) or not (0).
                if (alien.getBomb() == null) {
                    fileOut.println("0 0 0");
                } else if (alien.getBomb().isVisible()) {
                    fileOut.println( 1 + " " + alien.getBomb().getX() + " " + alien.getBomb().getY());
                }
            }
            
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
        // Background
        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, getWidth() / 2 - 30, getWidth() - 100, 50);

        // Text
        g.setColor(Color.white);
        g.setFont(gameFont);
        //g.drawString(text, getWidth() / 3 + 6 * text.length(), getWidth() / 2);
        //g.drawString(text, 50 + getWidth() / 3 - 3 * text.length(), getWidth() / 2);
        g.drawString(text, (getWidth() / 2) - (10 * text.length() / 2), getWidth() / 2);
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
    public Player getPlayer() {
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
    public GameState getGameState() {
        return gameState;
    }
    
    /**
     * Set shot
     * @param shot 
     */
    public void setShot(Shot shot) {
        this.shot = shot;
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
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
