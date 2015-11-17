/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import environment.Actor;
import environment.Velocity;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kyle
 */
public class Projectile extends Actor {
    
    private Velocity velocity;
    private Point position;
    private int size;
    
    public Projectile(BufferedImage image, Point position, int size, Velocity velocity) {
        super(image, position, velocity);
        this.position = position;
        this.size = size;
        this.velocity = velocity;
    }
    
    public void draw(Graphics graphics) {
        graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size, size * 6, null);
    }
    
    public void projectileTimeTaskHandler() {
        position = new Point((int) (position.getX() + (velocity.getX())), (int) (position.getY() + (velocity.getY())));
    }
    
    public int getX() {
        return (int) (position.getX());
    }
    
    public int getY() {
        return (int) (position.getY());
    }

    int getSize() {
        return size;
    }
}
