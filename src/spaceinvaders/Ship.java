/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import static environment.Utility.random;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Kyle
 */
public class Ship {
    
    int x;
    int y;
    int size;
    int invulTimer;
    boolean isFast;
    int health;
    boolean rapidFire;
    int fireCooldown;
    int shieldTimer;
    
    public Ship(int x, int y, int size) {
        
        this.x = x;
        this.y = y;
        this.size = size;
        
    }
    
    public void draw(Graphics graphics) {
        
        graphics.setColor(Color.WHITE);
        graphics.fillRect(x, y, size, size);
        
    }
    
    void setX(int newX) {
        this.x = newX;
    }
    
    void moveX(int xChange) {
        this.x = this.x + xChange;
    }
    
    int getX() {
        return this.x;
    }
    
    boolean hasRapidFire() {
        return this.rapidFire;
    }
    
    boolean hasSpeed() {
        return this.isFast;
    }
    
}
