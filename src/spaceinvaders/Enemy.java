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

/**
 *
 * @author Kyle
 */
public class Enemy extends Actor {
    
    private Velocity velocity;
    private Point position;
    private int size;
    private int width;
    private int height;
    private int timer;
    private int displacement;
    
    private int health;
    private int type;
    private boolean targetShip;
    private boolean centering;
    private int frame;
    
    public static int SMALL = 0;
    public static int MEDIUM = 1;
    public static int LARGE = 2;
    
    private final EnemyMovementPaternIntf limiter;
    
    private final SpriteProviderIntf imageProvider;
    
    public Enemy(BufferedImage image, Point position, int size, Velocity velocity, int health, int type, SpriteProviderIntf imageProvider, EnemyMovementPaternIntf limiter) {
        super(image, position, velocity, new Point((image.getWidth() * 3), (image.getHeight() * 3)));
        this.position = position;
        this.size = size;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.velocity = velocity;
        this.health = health;
        this.type = type;
        this.imageProvider = imageProvider;
        this.limiter = limiter;
    }
    
    public void draw(Graphics graphics) {
        if (frame == 1) {
            if (type == LARGE) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_ALIEN_MOVE), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            } else if (type == MEDIUM) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_ALIEN_MOVE), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            } else if (type == SMALL) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.YELLOW_ALIEN_MOVE), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            } else {
                graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            }
        } else {
            graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
        }
    }
    
    public void enemyTimeTaskHandler() {
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
    void Damage(int damage) {
        health -= damage;
    }
    int getHealth() {
        return health;
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
    void targetShip() {
        targetShip = !targetShip;
    }
    boolean targetingShip() {
        return targetShip;
    }
    void setFrame(int frame) {
        this.frame = frame;
    }
    int getMinStartY() {
        return limiter.getMinStartY();
        }
    int getMaxY() {
        return limiter.getMaxY();
    }
    int getMinY() {
        return limiter.getMinY();
    }
    void center() {
        centering = !centering;
    }
    boolean isCentering() {
        return centering;
    }
}
