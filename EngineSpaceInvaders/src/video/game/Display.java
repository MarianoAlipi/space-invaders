package video.game;

import java.awt.Canvas;
import javax.swing.JFrame;
import java.awt.Dimension;

/**
 *
 * @author MarcelA00821875
 * @author MarianoA00822247
 */
public class Display {
    private JFrame jframe; 
    private Canvas canvas; 
    private String title; 
    private int width; 
    private int height; 

    public Display(String title, int width, int height){
        this.title = title;
        this.width = width;
        this.height = height;
        createDisplay();
    }
     public void createDisplay(){
     jframe = new JFrame(title);
     jframe.setSize(width, height);
     
     jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     jframe.setResizable(false);
     jframe.setLocationRelativeTo(null);
     jframe.setVisible(true);
     
     //create the canvas to paint and setting size
     canvas = new Canvas();
     canvas.setPreferredSize(new Dimension(width,height));
     canvas.setMaximumSize(new Dimension(width, height));
     canvas.setFocusable(false); //ignore display while keyboard input from jFrame
     jframe.add(canvas);
     jframe.pack();
     }
     
     public JFrame getJframe() {return jframe;}
     public Canvas getCanvas(){return canvas;}
     
}
