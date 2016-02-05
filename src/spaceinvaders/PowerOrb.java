/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import environment.Actor;
import environment.Velocity;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kyle
 */
public class PowerOrb extends Actor {
    private Velocity velocity;
    private Point position;
    private int size;
    private int type;
    private int width;
    private int height;
    private int timer;
    
    private final SpriteProviderIntf imageProvider;
    
    public static int RAPID_FIRE = 1;
    public static int SHIELD = 2;
    public static int SPEED = 3;
    
    public PowerOrb(BufferedImage image, Point position, int size, Velocity velocity, int type, SpriteProviderIntf imageProvider) {
        super(image, position, velocity, new Point((image.getWidth() * 3), (image.getHeight() * 3)));
        this.position = position;
        this.size = size;
        this.velocity = velocity;
        this.width = image.getWidth() * size;
        this.height = image.getHeight() * size;
        this.type = type;
        this.imageProvider = imageProvider;
    }
    
    @Override
    public Rectangle getObjectBoundary() {
        return new Rectangle(position.x - (2 * size), position.y - (2 * size), width - (4 * size), height - (4 * size));
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        if (timer >= 0 && timer <= 2) {
            if (type == RAPID_FIRE) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.POWERUP_RAPID_FIRE_SMALL), (int) position.getX() + size, (int) position.getY() + size, width - (size * 2), height - (size * 2), null);
            } else if (type == SHIELD) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.POWERUP_SHIELD_SMALL), (int) position.getX() + size, (int) position.getY() + size, width - (size * 2), height - (size * 2), null);
            } else if (type == SPEED) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.POWERUP_SPEED_SMALL), (int) position.getX() + size, (int) position.getY() + size, width - (size * 2), height - (size * 2), null);
            } else {
                graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), width, height, null);
            }
        } else if (timer >= 6 && timer <= 8) {
            if (type == RAPID_FIRE) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.POWERUP_RAPID_FIRE_LARGE), (int) position.getX() - size, (int) position.getY() - size, width + (size * 2), height + (size * 2), null);
            } else if (type == SHIELD) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.POWERUP_SHIELD_LARGE), (int) position.getX() - size, (int) position.getY() - size, width + (size * 2), height + (size * 2), null);
            } else if (type == SPEED) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.POWERUP_SPEED_LARGE), (int) position.getX() - size, (int) position.getY() - size, width + (size * 2), height + (size * 2), null);
            } else {
                graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), width, height, null);
            }
        } else {
            graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), width, height, null);
        }
    }
    
    public void powerOrbTimeTaskHandler() {
        timer++;
        if (timer >= 12) {
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
    
    int getType() {
        return type;
    }
    
    int getWidth() {
        return width;
    }
    
    int getHeight() {
        return height;
    }
}
