/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import environment.Actor;
import environment.Velocity;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kyle
 */
public class Enemy extends Actor {
    
    private Velocity velocity;
    private Point position;
    private int size;
    private int width;
    private int height;
    private int timer;
    private int beamTimer;
    private boolean shootingBeam;
    private int displacement;
    private int attackTimer;
    private int deathTimer;
    private int alienSummonTimer;
    
    private int health;
    private int type;
    private boolean targetShip;
    private boolean centering;
    private int frame;
    private boolean motherShipBeam;
    
    public static int SMALL = 0;
    public static int MEDIUM = 1;
    public static int LARGE = 2;
    public static int MOTHERSHIP = 3;
    
    private final EnemyMovementPaternIntf limiter;
    
    private final SpriteProviderIntf imageProvider;
    
    private final AudioPlayerIntf audioPlayer;
    
    public Enemy(BufferedImage image, Point position, int size, Velocity velocity, int health, int type, SpriteProviderIntf imageProvider, EnemyMovementPaternIntf limiter, AudioPlayerIntf audioPlayer) {
        super(image, position, velocity, new Point((image.getWidth() * 3), (image.getHeight() * 3)));
        this.position = position;
        this.size = size;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.velocity = velocity;
        this.health = health;
        this.type = type;
        this.imageProvider = imageProvider;
        this.limiter = limiter;
        this.audioPlayer = audioPlayer;
    }
    
    @Override
    public Rectangle getObjectBoundary() {
        if (type == MOTHERSHIP) {
            return new Rectangle(position.x, position.y + (size * 10), size * width, size * 24);
        } else {
            return super.getObjectBoundary();
        }
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        if (type == MOTHERSHIP && shootingBeam) {
            for (int i = (int) (position.getY() + (size * (height / 3 * ((beamTimer + 3) / 3)))); i < 640; i += size * 46) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.MOTHERSHIP_BEAM), (int) position.getX() + (((width / 2) - 19) * size), i, size * 38, size * 46, null);
            }
        }
        if (frame == 1 && type != MOTHERSHIP) {
            if (type == LARGE) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_ALIEN_MOVE), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            } else if (type == MEDIUM) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_ALIEN_MOVE), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            } else if (type == SMALL) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.YELLOW_ALIEN_MOVE), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            } else {
                graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
            }
        } else {
            graphics.drawImage(getImage(), (int) position.getX(), (int) position.getY(), size * width, size * height, null);
        }
    }
    
    public void enemyTimeTaskHandler() {
        if (type == MOTHERSHIP) {
            beamTimer++;
            if (beamTimer >= 9) {
                beamTimer = 0;
            }
            xVelocityLimiter();
            if (deathTimer >= 1) {
                deathTimer++;
                if ((deathTimer - 2) / 20 == (deathTimer + 17) / 20) {
                    audioPlayer.playAudio(AudioManager.MOTHERSHIP_EXPLODE, false);
                }
                if ((deathTimer + 2) / 4 == (deathTimer + 4) / 4) {
                    setVelocity(4, 0);
                } else {
                    setVelocity(-4, 0);
                }
                if (deathTimer >= 195) {
                    setVelocity(size * 12, -(size));
                    width -= 6 * size;
                    height += size * 2;
                    if (deathTimer == 199) {
                        audioPlayer.playAudio(AudioManager.MOTHERSHIP_EXPLODE, false);
                        audioPlayer.playAudio(AudioManager.MOTHERSHIP_EXPLODE, false);
                    }
                }
            }
        }
        move();
    }
    
    private void xVelocityLimiter() {
        if (position.x <= limiter.getMinX()) {
            velocity.x = Math.abs(velocity.x);
            attackTimer++;
        } else if (position.x >= limiter.getMaxX()) {
            velocity.x = -Math.abs(velocity.x);
            attackTimer++;
        }
    }
    
    public int getAttackTimer() {
        return attackTimer;
    }
    
    public void setAttackTimer(int attackTimer) {
        this.attackTimer = attackTimer;
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
    void Damage(int damage) {
        health -= damage;
    }
    int getHealth() {
        return health;
    }
    void killMotherShip() {
        deathTimer = 1;
    }
    int getDeathTimer() {
        return deathTimer;
    }
    int getType() {
        return type;
    }
    int getWidth() {
        return width;
    }
    int getHeight() {
        return height;
    }
    void targetShip() {
        targetShip = !targetShip;
    }
    boolean targetingShip() {
        return targetShip;
    }
    void setFrame(int frame) {
        this.frame = frame;
    }
    int getMinStartY() {
        return limiter.getMinStartY();
        }
    int getMaxY() {
        return limiter.getMaxY();
    }
    int getMinY() {
        return limiter.getMinY();
    }
    int getMinX() {
        return limiter.getMinX();
    }
    int getMaxX() {
        return limiter.getMaxX();
    }
    void center() {
        centering = !centering;
    }
    boolean isCentering() {
        return centering;
    }
    int getVelocityX() {
        return velocity.getX();
    }
    boolean shootingBeam() {
        return shootingBeam;
    }
    void shootBeam(boolean shootingBeam) {
        this.shootingBeam = shootingBeam;
    }
    Rectangle getBeamHitbox() {
        return new Rectangle((int) position.getX() + (((width / 2) - 10) * size), (int) position.getY() + (height * size), 20 * size, 640);
    }
    void setAlienSummonTimer(int alienSummonTimer) {
        this.alienSummonTimer = alienSummonTimer;
    }
    int getAlienSummonTimer() {
        return alienSummonTimer;
    }
}
