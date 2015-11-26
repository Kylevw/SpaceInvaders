/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import environment.Environment;
import environment.Velocity;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author Kyle
 */
class SpaceEnvironment extends Environment {
    
    private ArrayList<Star> stars;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Enemy> enemies;
    Ship ship;

    private int direction;
    private int yStarChange;
    private boolean spacebarDebug;
    private boolean isMenu;
    private boolean paused;
    
    private boolean leftDebug;
    private boolean rightDebug;
    
    private int level;
    
    StatMeter healthMeter;
    StatMeter energyMeter;
    StatMeter powerMeter;
    
    AudioManager am;
    SpriteManager im;
    
    private boolean menuMusic;
    private boolean gameMusic;
    
    public SpaceEnvironment() {
        
        level = 1;
        
        loadImages();
        loadAudio();
        
        this.setBackground(Color.BLACK);
        
        // creates ship and ship statistic meters (health + ammo)
        ship = new Ship(im.getImage(SpriteManager.SHIP), new Point(292, 640), 48, new Velocity(0, 0), 9, new ShipMovementLimitProvider(24, 568, 504, 640), im, am);
        healthMeter = new StatMeter(StatMeter.RED, 16, 4, 480, 568, 3, im);
        energyMeter = new StatMeter(StatMeter.BLUE, 16, 0, 480, 589, 3, im);

        // creates background stars
        stars = new ArrayList<>();
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        int starCount = 48; // number of stars on screen
        for (int i = 0; i < starCount; i++) {
            stars.add(new Star(random(640), random(640), random(3)));
        }
    }
    
    public void loadImages() {
        im = new SpriteManager();
    }
    
    public void loadAudio() {
        am = new AudioManager();
    }
    
    // random number generator
    public int random(int value) {
        return (int) (Math.random() * value);
    }

    @Override
    public void initializeEnvironment() {
        
    }

    @Override
    public void timerTaskHandler() {
        
        // controls movement of the stars
        if (stars != null && !paused) {
            yStarChange = ((level + 1) / 2) + 2;
            
            stars.stream().forEach((theStar) -> {
            theStar.setY(yStarChange);
            
            if (theStar.getY() >= 640) {
                theStar.resetStar();
            }
            
        });
            
        }
        
        // controls movement of projeciltes
        ArrayList<Projectile> outOfBounds = new ArrayList<>();
        if (projectiles != null && !paused) {
            projectiles.stream().forEach((theProjectile) -> {
                
                    theProjectile.projectileTimeTaskHandler();
                    // removes projectiles that go off screen
                    if (theProjectile.getY() < -theProjectile.getSize()) {
                        outOfBounds.add(theProjectile);
                    }
            });
            projectiles.removeAll(outOfBounds);
        }
        
        if (enemies != null && !paused) {
            enemies.stream().forEach((theEnemy) -> {
                
                    theEnemy.enemyTimeTaskHandler();
                
            });
        }
        
        // controls movement of ship and stat meters
        if (ship != null && !paused) {
            
            ship.shipTimerTaskHandler();
            
            if (healthMeter != null) {
                healthMeter.setValue(ship.getHealth());
                healthMeter.meterTimeTaskHandler();
            }
            if (energyMeter != null) {
                energyMeter.setValue(ship.getEnergy());
                energyMeter.meterTimeTaskHandler();
            }
            
            if (ship.createPowerMeter()) {
                powerMeter = new StatMeter(ship.getPowerUp(), 16, 4, 16, 589, 3, im);
            }
            
            if (powerMeter != null) {
                if (ship.getPowerUpTime() > 0) {
                    powerMeter.setValue(((ship.getPowerUpTime() - 1) / 25) + 1);
                    powerMeter.meterTimeTaskHandler();
                }
            }
        }
        
        checkHitStatus();
    }
    
    private void checkHitStatus(){
        if (enemies != null && projectiles != null) {
            for (Enemy enemy : enemies) {
                ArrayList<Projectile> hitEnemy = new ArrayList<>();
                for (Projectile projectile : projectiles) {
                    if (projectile.getObjectBoundary().intersects(enemy.getObjectBoundary())) {
                        System.out.println("HIT");
                        // damage the enemy
                    
                        //get rid of projectile
                        hitEnemy.add(projectile);
                        
                        //make an explosiion sound
                        
                    }
                }
                projectiles.removeAll(hitEnemy);
            }
        }
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            paused = !paused;
        } else if (e.getKeyCode() == KeyEvent.VK_1) {
            menuMusic = !menuMusic;
            if (menuMusic == true) {
                am.playAudio(AudioManager.MENU, true);
            } else {
                am.stopAudio(AudioManager.MENU);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_2) {
            gameMusic = !gameMusic;
            if (gameMusic == true) {
                am.playAudio(AudioManager.GAME, true);
            } else {
                am.stopAudio(AudioManager.GAME);
            }
        } else if (!paused && ship.getY() < ship.getMinY()) {
            
            if (e.getKeyCode() == KeyEvent.VK_SPACE && !spacebarDebug) {
                if (ship != null) {
                    fire();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !rightDebug) {
                // right movement for ship
                ship.accelerate(ship.getSpeed());
                rightDebug = true;
                
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && !leftDebug) {
                // left movement for ship
                ship.accelerate(-ship.getSpeed());
                leftDebug = true;
            } else if (e.getKeyCode() == KeyEvent.VK_F) {
                // grants RAPID_FIRE Power-up
                ship.togglePowerUp(Ship.RAPID_FIRE);
                
            } else if (e.getKeyCode() == KeyEvent.VK_G) {
                // grants SHIELD Power-up
                ship.togglePowerUp(Ship.SHIELD);
                
            } else if (e.getKeyCode() == KeyEvent.VK_H) {
                // grants SPEED Power-up
                ship.togglePowerUp(Ship.SPEED);
                
            } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                // grants SPEED Power-up
                summonAlien();
                
            } else if (e.getKeyCode() == KeyEvent.VK_P && level < 10) {
                // speeds up background
                level = level + 2;
                
            } else if (e.getKeyCode() == KeyEvent.VK_O && level > 1) {
                // slows up background
                level = level - 2;
            }
        }
    }
    
    private void fire(){
        // tests if thie ship has ammunition
                if (0 < ship.getEnergy() || ship.getPowerUp() == Ship.RAPID_FIRE) {
                    // creates projectiles, removes 1 energy, resets cooldown
                    ship.fire();
                    projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE), new Point(ship.getX() + (3 * ship.getSize() / 16), ship.getY() + (ship.getSize() / 16)), (ship.getSize() / 16), new Velocity(0, -36)));
                    projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE), new Point(ship.getX() + (3 * ship.getSize() / 4), ship.getY() + (ship.getSize() / 16)), (ship.getSize() / 16), new Velocity(0, -36)));
                    if (ship.getEnergy() == 0 && ship.getPowerUp() != Ship.RAPID_FIRE) {
                        ship.setFireCooldown(80);
                    } else if (ship.getPowerUp() != Ship.RAPID_FIRE) {
                        ship.setFireCooldown(40);
                    }
                    
                    // otherwise, resets the cooldown if no energy is present
                } else if (ship.getEnergy() == 0 && ship.getPowerUp() != Ship.RAPID_FIRE) {
                    ship.setFireCooldown(80);
                }
                spacebarDebug = true;
    }
    
    private void summonAlien(){
        enemies.add(new Enemy(im.getImage(SpriteManager.YELLOW_ALIEN), new Point(296, 0), 48, new Velocity(0, 1)));
    }
    
    @Override
    public void keyReleasedHandler(KeyEvent e) {
        // when releasing the right key, stops ship from moving right
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && rightDebug) {
                ship.accelerate(-ship.getSpeed(), 0);
                rightDebug = false;
                // when releasing the left key, stops ship from moving right
        } else if ( e.getKeyCode() == KeyEvent.VK_LEFT && leftDebug) {
                ship.accelerate(ship.getSpeed(), 0);
                leftDebug = false;
                // allows the SPACE key to be pressed again after being released
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacebarDebug = false;
        }
    }

    @Override
    public void environmentMouseClicked(MouseEvent e) {
    }

    @Override
    public void paintEnvironment(Graphics graphics) {
        
        stars.stream().forEach((theStar) -> {
            theStar.draw(graphics);
        });
        
        if (ship != null) {
            ship.draw(graphics);
        }
        
        if (healthMeter != null) {
            healthMeter.draw(graphics);
        }
        
        if (energyMeter != null) {
            energyMeter.draw(graphics);
        }
        
        if (powerMeter != null && ship.getPowerUpTime() > 0) {
            powerMeter.draw(graphics);
        }
        
        projectiles.stream().forEach((theProjectile) -> {
            theProjectile.draw(graphics);
        });
        
        enemies.stream().forEach((theEnemy) -> {
            theEnemy.draw(graphics);
        });
        
        graphics.drawImage(im.getImage(SpriteManager.MOTHER_SHIP), 24, 24, 122 * 4, 47 * 4, this);
        
        // pause menu
        if (paused) {
            graphics.setColor(new Color(0, 0, 0, 100));
            graphics.fillRect(0, 0, 640, 640);
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("Calibri", Font.BOLD, 36));
            graphics.drawString("PAUSED", 244, 100);
        }
    }
}
