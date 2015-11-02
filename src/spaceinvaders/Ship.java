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
    int health;
    
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
//       BufferedImage.
//        ship = ResourceTools.loadImageFromResource("spaceinvaders/spritesheet.png");
        
        graphics.drawImage(imageProvider.getImage(SpriteManager.SHIP), (x * size / 48), (y * size / 48), x + (size), y + (size), 0, 112, 16, 128, null);
        graphics.drawRect((x * size / 48), (y * size / 48), x + (size), y + (size));
        
        if (hasSpeed == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_TINT), x - 3, y - 3, x + 51, y + 51, 38, 110, 56, 128, null);
        }
        
        if (hasDoubleFire == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_TINT), x - 3, y - 3, x + 51, y + 51, 56, 110, 74, 128, null);
        }
        
        if (hasShield == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.SHIELD), x - 9, y - 6, x + 57, y + 60, 16, 106, 38, 128, null);
        }
//        if (ship != null) {
//            graphics.drawImage(ship, (x * size / 48), (y * size / 48), x + (size), y + (size), 0, 112, 16, 128, null);
//        }
//        
//        if (hasSpeed == true) {
//            graphics.drawImage(ship, x - 3, y - 3, x + 51, y + 51, 38, 110, 56, 128, null);
//        }
//        
//        if (hasDoubleFire == true) {
//            graphics.drawImage(ship, x - 3, y - 3, x + 51, y + 51, 56, 110, 74, 128, null);
//        }
//        
//        if (hasShield == true) {
//            graphics.drawImage(ship, x - 9, y - 6, x + 57, y + 60, 16, 106, 38, 128, null);
//        }
//        
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
    
}
