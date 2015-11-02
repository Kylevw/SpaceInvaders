/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import environment.Actor;
import environment.Velocity;
import images.ResourceTools;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kyle
 */
public class Projectile extends Actor {

    public Projectile(BufferedImage image, Point position, Velocity velocity) {
//        ResourceTools.loadImageFromResource("spaceinvaders/spritesheet.png").
        super(image, position, velocity);
    }
    
}
