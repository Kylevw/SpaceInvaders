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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Kyle
 */
public class Projectile extends Actor {
    
    private Velocity velocity;
    private Point position;
    private int size;
    private int damage;
    private boolean isFriendly;
    private int width;
    private int height;
    
    public Projectile(BufferedImage image, Point position, int size, Velocity velocity, int damage, boolean isFriendly) {
        super(image, position, velocity, new Point((image.getWidth() * 3), (image.getHeight() * 3)));
        this.position = position;
        this.size = size;
        this.velocity = velocity;
        this.damage = damage;
        this.width = image.getWidth() * size;
        this.height = image.getHeight() * size;
        this.isFriendly = isFriendly;
    }
    
    public void draw(Graphics graphics) {
        graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), width, height, null);
    }
    
    public void projectileTimeTaskHandler() {
        move();
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
    
    int getDamage() {
        return damage;
    }
    
    boolean isFriendly() {
        return isFriendly;
    }
    
    int getWidth() {
        return width;
    }
    
    int getHeight() {
        return height;
    }
}
