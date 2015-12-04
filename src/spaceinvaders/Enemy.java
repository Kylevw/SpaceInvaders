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
    
    public static int SMALL = 0;
    public static int MEDIUM = 1;
    public static int LARGE = 2;
    
    public Enemy(BufferedImage image, Point position, int size, Velocity velocity, int health, int type) {
        super(image, position, velocity, new Point((image.getWidth() * 3), (image.getHeight() * 3)));
        this.position = position;
        this.size = size;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.velocity = velocity;
        this.health = health;
        this.type = type;
    }
    
    public void draw(Graphics graphics) {
            graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
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
}
