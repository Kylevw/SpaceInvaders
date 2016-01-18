/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

//import com.apple.eawt.Application;
import environment.ApplicationStarter;
import images.ResourceTools;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

/**
 *
 * @author Kyle
 */
public class SpaceInvaders {

    public static JFrame frame;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startUp();
    }

    private static void startUp() {
//        Application application = Application.getApplication();
//        application.setDockIconImage(ResourceTools.loadImageFromResource("spaceinvaders/icon.png"));
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        frame = ApplicationStarter.run("Space Invaders... Kinda", new Dimension(640, 640), new SpaceEnvironment(), ResourceTools.loadImageFromResource("spaceinvaders/icon.png"));
    }
    
    public static Dimension getWindowSize() {
        if (frame != null) {
            return frame.getContentPane().getSize();
        } else {
            return new Dimension(640, 640);
        }
    }
}
