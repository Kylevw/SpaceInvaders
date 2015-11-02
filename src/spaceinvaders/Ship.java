/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import images.ResourceTools;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

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
        
        ship = ResourceTools.loadImageFromResource("spaceinvaders/spritesheet.png");
        
        if (ship != null) {
            graphics.drawImage(ship, x, y, x + 48, y + 48, 0, 112, 16, 128, null);
        }
        
        if (isFast == true) {
            graphics.drawImage(ship, x - 3, y - 3, x + 51, y + 51, 36, 110, 54, 128, null);
        }
        
    }
    
    Image ship = null;
    
    void setX(int newX) {
        this.x = newX;
    }
    
    void moveX(int xChange) {
        
        if (isFast == true) {
            xChange = xChange * 2;
        }
        
        this.x = this.x + xChange;
        
        if (x >= 568) {
            x = 568;
        }
        if (x <= 24) {
            x = 24;
        }
        
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

    void toggleSpeed() {
        if (isFast == false) {
            isFast = true;
        } else {
            isFast = false;
        }
    }
    
}
