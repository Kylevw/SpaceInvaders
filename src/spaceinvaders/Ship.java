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
public class Ship extends Actor {
    
    {
        health = 1;
        energy = 16;
    }
    
    private Point position;
    private Velocity velocity;
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
    
    public Ship(BufferedImage image, Point position, int size, Velocity velocity, int speed, MovementLimitProviderIntf limiter, SpriteProviderIntf imageProvider, AudioPlayerIntf audioPlayer) {
        
        super(image, position, velocity);
        this.limiter = limiter;
        this.position = position;
        this.size = size;
        this.speed = speed;
        this.imageProvider = imageProvider;
        this.audioPlayer = audioPlayer;
        
    }
    
    public void draw(Graphics graphics) {
        
        if (powerUp == SPEED) {
            graphics.drawImage(getImage(), ((int) position.getX() + (size / 4)), ((int) position.getY() + (size / 4)), size / 2, size / 2, null);
        } else {
            graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size, size, null);
        }
        
        // draws power-up tints/overlays
        if (powerUp == RAPID_FIRE) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_TINT), (int) position.getX() - (size / 16), (int) position.getY() - (size / 16), size * 9 / 8, size * 9 / 8, null);
        } else if (powerUp == SHIELD) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.SHIELD), (int) position.getX() - (size / 4), (int) position.getY() - (3 * size / 16), size * 3 / 2, size * 3 / 2, null);
        } else if (powerUp == SPEED) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_TINT), (int) position.getX() + (size * 7 / 32), (int) position.getY() + (size * 7 / 32), size * 9 / 16, size * 9 / 16, null);
        }
    }
    
    void setX(int x) {
        position.setLocation(x, position.getY());
    }
    
    void setY(int y) {
        position.setLocation(position.getX(), y);
    }
    
    int getX() {
        return (int) position.getX();
    }
    int getY() {
        return (int) position.getY();
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
        
        shipLimiter();
        
        if (position.getY() > getMinY() + 1) {
            setVelocity(0, -1);
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
        
        move();
        
    }
    
    private void shipLimiter() {
        if (limiter.getMinX() > getX()) {
            setPosition(limiter.getMinX(), getY());
        } else if (limiter.getMaxX() < getX()) {
            setPosition(limiter.getMaxX(), getY());
        }
        
        if (limiter.getMinY() > getY()) {
            setPosition(getX(), limiter.getMinY());
        } else if (limiter.getMaxY() < getY()) {
            setPosition(getX(), limiter.getMaxY());
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
    int getMaxX() {
        return limiter.getMaxX();
    }
    int getMinX() {
        return limiter.getMinX();
    }
    int getMaxY() {
        return limiter.getMaxY();
    }
    int getMinY() {
        return limiter.getMinY();
    }
}
