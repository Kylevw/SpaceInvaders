/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

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
    }
    
    private int x;
    private int y;
    private int size;
    private int speed;
    
    private int invulTimer;
    private int meterTimer;
    private int healthRegen;
    private int health;
    
    private int energy;
    private int fireCooldown;
    
    private int powerUpTimer;
    private int powerUp;
    private boolean createMeter;
    
    public static int RAPID_FIRE = 1;
    public static int SHIELD = 2;
    public static int SPEED = 3;
    
    
    private final MovementLimitProviderIntf limiter;
    private final SpriteProviderIntf imageProvider;
    private final AudioPlayerIntf audioPlayer;
    
    public Ship(int x, int y, int size, MovementLimitProviderIntf limiter, SpriteProviderIntf imageProvider, AudioPlayerIntf audioPlayer) {
        
        this.limiter = limiter;
        this.x = x;
        this.y = y;
        this.size = size;
        this.imageProvider = imageProvider;
        this.audioPlayer = audioPlayer;
        
    }
    
    public void draw(Graphics graphics) {
        
        graphics.drawImage(imageProvider.getImage(SpriteManager.SHIP), (x * size / 48), (y * size / 48), size, size, null);
        
        if (powerUp == RAPID_FIRE) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_TINT), x - (size / 16), y - (size / 16), size * 9 / 8, size * 9 / 8, null);
        } else if (powerUp == SHIELD) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.SHIELD), x - (size / 4), y - (3 * size / 16), size * 3 / 2, size * 3 / 2, null);
        } else if (powerUp == SPEED) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_TINT), x - (size / 16), y - (size / 16), size * 9 / 8, size * 9 / 8, null);
        }
    }
    
    void setX(int newX) {
        this.x = newX;
    }
    
    void moveX(int xChange) {
        
        if (powerUp == SPEED) {
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
    int powerUp() {
        return powerUp;
    }
    void togglePowerUp(int powerUpID) {
        powerUp = powerUpID;
        powerUpTimer = 400;
        createMeter = true;
        audioPlayer.playAudio(AudioManager.POWER_UP, false);
    }
    
    void fire() {
        
        audioPlayer.playAudio(AudioManager.FIRE, false);
        energy--;
        
        if (powerUp == RAPID_FIRE) {
            energy++;
        }
    }
    
    void shipTimerTaskHandler() {
        
        if (y >= 504) {
            y--;
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
        
        if (powerUpTimer > 0) {
            powerUpTimer--;
            if (powerUp == RAPID_FIRE) {
                powerUpTimer--;
            }
        } else if (powerUp > 0) {
            powerUp = 0;
            audioPlayer.playAudio(AudioManager.LOSE_POWER_UP, false);
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
    boolean createPowerMeter() {
        return createMeter;
    }
    void enablePowerUp() {
        createMeter = false;
    }
    int getPowerUpTime() {
        return powerUpTimer;
    }
    int getPowerUp() {
        return powerUp;
    }
    int getSize() {
        return size;
    }
    void setFireCooldown(int fireCooldown) {
        this.fireCooldown = fireCooldown;
    }
}
