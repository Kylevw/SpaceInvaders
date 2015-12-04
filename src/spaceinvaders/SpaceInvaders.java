/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import com.apple.eawt.Application;
import environment.ApplicationStarter;
import java.awt.Dimension;
import javax.swing.ImageIcon;

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
        Application.getApplication().setDockIconImage(new ImageIcon("spaceinvaders/icon").getImage());
        ApplicationStarter.run(new String[0], "Space Invaders", new Dimension(640, 640), new SpaceEnvironment());
        
    }
}
