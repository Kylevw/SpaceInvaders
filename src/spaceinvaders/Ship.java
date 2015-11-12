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
    private final MovementLimitProviderIntf limiter;
    private final SpriteProviderIntf imageProvider;
    
    public Ship(int x, int y, int size, MovementLimitProviderIntf limiter, SpriteProviderIntf imageProvider, AudioPlayerIntf audioPlayer) {
        
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
        
        ArrayList<Projectile> outOfBounds = new ArrayList<>();
        
        projectiles.stream().forEach((theProjectile) -> {
            theProjectile.draw(graphics);
            theProjectile.applyVelocity();
            if (theProjectile.getY() < -24) {
                outOfBounds.add(theProjectile);
            }
        });

        projectiles.removeAll(outOfBounds);
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
    void moveY(int yChange) {
        this.y = y + yChange;
    }
    int getX() {
        return this.x;
    }
    int getY() {
        return this.y;
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
//        
//        if (meterTimer < 4) {
//            meterTimer++;
//        } else {
//            meterTimer = 0;
//        }
        
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
    int getHealth() {
        return health;
    }
    int getEnergy() {
        return energy;
    }
}
