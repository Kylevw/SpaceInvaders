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
public class Enemy extends Actor {
    
    private Velocity velocity;
    private Point position;
    private int size;

    public Enemy(BufferedImage image, Point position, int size, Velocity velocity) {
        super(image, position, velocity);
        this.position = position;
        this.size = size;
        this.velocity = velocity;
        
    }
    
    public void draw(Graphics graphics) {
        graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size, size, null);
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
    
    
}
