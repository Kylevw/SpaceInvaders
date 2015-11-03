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
    
    {
        health = 12;
        energy = 12;
    }
    
    int x;
    int y;
    int size;
    
    int invulTimer;
    int health;
    
    int energy;
    int fireCooldown;
    
    int powerUpTimer;
    boolean hasSpeed;
    boolean hasDoubleFire;
    boolean hasShield;
    private MovementLimitProviderIntf limiter;
    Image ship;
    private final SpriteProviderIntf imageProvider;
    
    
    
    public Ship(int x, int y, int size, MovementLimitProviderIntf limiter, SpriteProviderIntf imageProvider) {
        
        this.limiter = limiter;
        this.x = x;
        this.y = y;
        this.size = size;
        this.imageProvider = imageProvider;
    }
    
    public void draw(Graphics graphics) {
        
        graphics.drawImage(imageProvider.getImage(SpriteManager.SHIP), (x * size / 48), (y * size / 48), size, size, null);
        
        if (hasSpeed == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_TINT), x - (size / 16), y - (size / 16), size * 9 / 8, size * 9 / 8, null);
        }
        
        if (hasDoubleFire == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_TINT), x - (size / 16), y - (size / 16), size * 9 / 8, size * 9 / 8, null);
        }
        
        if (hasShield == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.SHIELD), x - (size / 4), y - (3 * size / 16), size * 3 / 2, size * 3 / 2, null);
        }
        
        for (int i = 0; i < 16; i++) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER), 480 + (9 * i), 568, 12, 18, null);
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER), 480 + (9 * i), 589, 12, 18, null);

        }
        
        for (int i = 1; i <= health; i++) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER_FILL), 471 + (9 * i), 568, 12, 18, null);
        }
        
        for (int i = 1; i <= energy; i++) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER_FILL), 471 + (9 * i), 589, 12, 18, null);
        }
        
        
        
    }
    
    void setX(int newX) {
        this.x = newX;
    }
    
    void moveX(int xChange) {
        
        if (hasSpeed == true) {
            xChange = xChange * 2;
        }
        
        if (y >= limiter.getMinY()) {
            xChange = 0;
        }
        
        this.x = this.x + xChange;
        
        if (x >= limiter.getMaxX()) {
            x = limiter.getMaxX();
        }
        if (x <= limiter.getMinX()) {
            x = limiter.getMinX();
        }
        
    }
    
    int getX() {
        return this.x;
    }
    
    boolean hasDoubleFire() {
        return this.hasDoubleFire;
    }
    
    boolean hasSpeed() {
        return this.hasSpeed;
    }
    
    boolean hasShield() {
        return this.hasShield;
    }

    void toggleSpeed() {
        hasSpeed = !hasSpeed;
    }
    
    void toggleDoubleFire() {
        hasDoubleFire = !hasDoubleFire;
    }
    
    void toggleShield() {
        hasShield = !hasShield;
    }

    int getY() {
        return this.y;
    }
    
    void moveY(int yChange) {
        this.y = y - yChange;
    }
    
    void fire() {
        fireCooldown = 40;
        if (energy > 0) {
            System.out.println("*Fire Sound*");
        } else {
            System.out.println("No ammo :(");
        }
        energy--;
    }
    
    int getFireCooldown() {
        return fireCooldown;
    }
    
    void fireCooldown() {
        fireCooldown--;
    }

    void addEnergy() {
        if (energy <= 15) {
            energy ++;
        }
    }
    
}
