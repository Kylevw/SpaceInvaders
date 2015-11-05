/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import audio.Playlist;
import audio.SoundManager;
import audio.Source;
import audio.Track;
import environment.Velocity;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Kyle
 */
public class Ship {
    
    {
        health = 1;
        energy = 16;
        speed = 12;
        
        ArrayList<Track> tracks = new ArrayList<>();
        tracks.add(new Track("FIRE", Source.RESOURCE, "/spaceinvaders/fire.wav"));
        
        sm = new SoundManager(new Playlist(tracks));
        
        projectiles = new ArrayList<>();
        
    }
    
    SoundManager sm;
    
    ArrayList<Projectile> projectiles;
    
    int x;
    int y;
    int size;
    int speed;
    
    int invulTimer;
    int meterTimer;
    int healthRegen;
    int health;
    
    int energy;
    int fireCooldown;
    
    int powerUpTimer;
    boolean hasSpeed;
    boolean hasRapidFire;
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
        
        if (hasRapidFire == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_TINT), x - (size / 16), y - (size / 16), size * 9 / 8, size * 9 / 8, null);
        }
        
        if (hasShield == true) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.SHIELD), x - (size / 4), y - (3 * size / 16), size * 3 / 2, size * 3 / 2, null);
        }
        
        for (int i = 0; i < 16; i++) {
            if (health > 4) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER), 480 + (9 * i), 568, 12, 18, null);
            } else if (meterTimer > 2) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER), 480 + (9 * i), 568, 12, 18, null);
            } else {
                graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER_FLASH), 480 + (9 * i), 568, 12, 18, null);
            }
        }
        
        
        for (int i = 0; i < 16; i++) {
            if (energy > 0) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER), 480 + (9 * i), 589, 12, 18, null);
            } else if (meterTimer > 2) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER), 480 + (9 * i), 589, 12, 18, null);
            } else {
                graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER_FLASH), 480 + (9 * i), 589, 12, 18, null);
            }
        }
        
        for (int i = 1; i <= health; i++) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER_FILL), 471 + (9 * i), 568, 12, 18, null);
        }
        
        for (int i = 1; i <= energy; i++) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER_FILL), 471 + (9 * i), 589, 12, 18, null);
        }
        projectiles.stream().forEach((theProjectile) -> {
            theProjectile.draw(graphics);
            theProjectile.applyVelocity();
            if (theProjectile.getY() < 100) {
                projectiles.remove(theProjectile);
            }
        });
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
    
    boolean hasRapidFire() {
        return this.hasRapidFire;
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
    
    void toggleRapidFire() {
        hasRapidFire = !hasRapidFire;
    }
    
    void toggleShield() {
        hasShield = !hasShield;
    }

    int getY() {
        return this.y;
    }
    
    void moveY(int yChange) {
        this.y = y + yChange;
    }
    
    void fire() {
        
        if (energy > 0 || hasRapidFire == true) {
            sm.play("FIRE");
            projectiles.add(new Projectile(imageProvider.getImage(SpriteManager.PROJECTILE), new Point(x + (3 * size / 16), y), (size / 16), new Velocity(0, -36)));
            projectiles.add(new Projectile(imageProvider.getImage(SpriteManager.PROJECTILE), new Point(x + (3 * size / 4), y), (size / 16), new Velocity(0, -36)));
            energy--;
        }
        
        if (hasRapidFire == true) {
            energy++;
        } else if (energy == 0) {
            fireCooldown = 80;
        } else {
            fireCooldown = 40;
        }
    }
    
    void shipTimerTaskHandler() {
        
        if (y >= 504) {
            y--;
        }
        
        if (meterTimer < 4) {
            meterTimer++;
        } else {
            meterTimer = 0;
        }
        
        if (fireCooldown == 0) {
            if (energy <= 15) {
                energy ++;
            }
        } else {
            fireCooldown--;
        }
        if (health < 16) {
            healthRegen++;
            if (healthRegen >= 160) {
                health++;
                healthRegen = 0;
            }
        }
        
    }

    int getSpeed() {
        return speed;
    }
}
