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
    private int damage;
    private boolean isFriendly;
    private int width;
    private int height;
    private int timer;
    
    private final SpriteProviderIntf imageProvider;
    
    public Projectile(BufferedImage image, Point position, int size, Velocity velocity, int damage, boolean isFriendly, SpriteProviderIntf imageProvider) {
        super(image, position, velocity, new Point((image.getWidth() * 3), (image.getHeight() * 3)));
        this.position = position;
        this.size = size;
        this.velocity = velocity;
        this.damage = damage;
        this.width = image.getWidth() * size;
        this.height = image.getHeight() * size;
        this.isFriendly = isFriendly;
        this.imageProvider = imageProvider;
    }
    
    public void draw(Graphics graphics) {
        if (!isFriendly && timer > 1) {
            if (width == 10 * size) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.PROJECTILE_LARGE_WHITE), (int) position.getX(), (int) position.getY(), width, height, null);
            } else if (width == 8 * size) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.PROJECTILE_MEDIUM_WHITE), (int) position.getX(), (int) position.getY(), width, height, null);
            } else if (width == 6 * size) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.PROJECTILE_SMALL_WHITE), (int) position.getX(), (int) position.getY(), width, height, null);
            } else {
                graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), width, height, null);
            }
        } else {
            graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), width, height, null);
        }
    }
    
    public void projectileTimeTaskHandler() {
        timer++;
        if (timer >= 4) {
            timer = 0;
        }
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
