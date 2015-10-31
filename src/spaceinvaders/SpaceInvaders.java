/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import environment.ApplicationStarter;
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
        ApplicationStarter.run(new String[0], "Space Invaders", new Dimension(640, 640), new SpaceEnvironment());    }
}
