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

/**
 *
 * @author Kyle
 */
public class SpaceInvaders {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startUp();
    }

    private static void startUp() {
//        Application application = Application.getApplication();
//        application.setDockIconImage(ResourceTools.loadImageFromResource("spaceinvaders/icon.png"));
        ApplicationStarter.run("Space Invaders... Kinda", new Dimension(640, 640), new SpaceEnvironment(), ResourceTools.loadImageFromResource("spaceinvaders/icon.png"));
    }
}
