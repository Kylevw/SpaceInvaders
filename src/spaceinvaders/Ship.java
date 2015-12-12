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
public class Ship extends Actor {
    
    {
        health = 16;
        energy = 16;
    }
    
    private Point position;
    private Velocity velocity;
    private int size;
    private int width;
    private int height;
    private int speed;
    
    private int invulTimer;
    private boolean flash;
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
        
        super(image, position, velocity, new Point((image.getWidth() * size), (image.getHeight() * size)));
        this.limiter = limiter;
        this.position = position;
        this.size = size;
        this.width = image.getWidth() * size;
        this.height = image.getHeight() * size;
        this.speed = speed;
        this.imageProvider = imageProvider;
        this.audioPlayer = audioPlayer;
        
    }
    
    @Override
    public Rectangle getObjectBoundary() {
        if (powerUp == SPEED) {
        return new Rectangle(position.x + (size * 4), position.y + (size * 4), width / 2 - (size * 2), height / 2);
        } else {
        return new Rectangle(position.x + (size * 2), position.y, width - (size * 4), height);
        }
    }
    
    public void draw(Graphics graphics) {
        if (!flash) {
            if (powerUp == SPEED) {
                graphics.drawImage(getImage(), ((int) position.getX() + (size * 4)), ((int) position.getY() + (size * 4)), size * 8, size * 8, null);
            } else {
                graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size * 16, size * 16, null);
            }
            
            // draws power-up tints/overlays
            if (powerUp == RAPID_FIRE) {
            graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_TINT), (int) position.getX() - (size), (int) position.getY() - (size), size * 18, size * 18, null);
            } else if (powerUp == SHIELD) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.SHIELD), (int) position.getX() - (size * 4), (int) position.getY() - (3 * size), size * 24, size * 24, null);
            } else if (powerUp == SPEED) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_TINT), (int) position.getX() + (size * 7 / 2), (int) position.getY() + (size * 7 / 2), size * 9, size * 9, null);
            }
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
        if (invulTimer > 0) {
            invulTimer--;
        }
        if ((invulTimer + 3) / 6 == (invulTimer + 6) / 6) {
            flash = true;
        } else {
            flash = false;
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
        shipLimiter();
    }
    
    private void shipLimiter() {
        
        if (position.getY() > limiter.getMinY() + 1) {
            setVelocity(0, -1);
        }
        
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
    void removePowerUp() {
        powerUpTimer = 0;
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
    void Damage(int damage) {
        if (invulTimer == 0 && powerUp != SHIELD) {
            health -= damage;
            healthRegen = -320;
            audioPlayer.playAudio(AudioManager.HURT_SHIP, false);
            invulTimer = 32;
        }
    }
    
    void setInvulTimer(int invulTimer) {
        this.invulTimer = invulTimer;
    }
    
    int getWidth() {
        return width;
    }
    int getHeight() {
        return height;
    }
}
