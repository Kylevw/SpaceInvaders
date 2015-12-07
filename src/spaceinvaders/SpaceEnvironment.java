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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import path.TrigonometryCalculator;

/**
 *
 * @author Kyle
 */
class SpaceEnvironment extends Environment {
    
//<editor-fold defaultstate="collapsed" desc="Properties">
    private ArrayList<Star> stars;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Enemy> enemies;
    Ship ship;
    
    private int direction;
    private int yStarChange;
    private boolean spacebarDebug;
    private boolean isMenu;
    private boolean paused;
    
    private int alienTimer;
    private int timerTick;
    private int displacement;
    
    private boolean leftDebug;
    private boolean rightDebug;
    
    private int level;
    private int difficulty;
    
    StatMeter healthMeter;
    StatMeter energyMeter;
    StatMeter powerMeter;
    
    AudioManager am;
    SpriteManager im;
    
    private boolean menuMusic;
    private boolean gameMusic;
    
    private ArrayList<Projectile> getProjectiles(){
        return new ArrayList<>(projectiles);
    }
    
    private ArrayList<Enemy> getEnemies(){
        return new ArrayList<>(enemies);
    }
    
//</editor-fold>
    
    public SpaceEnvironment() {
        
        level = 1;
        difficulty = 0;
        
        loadImages();
        loadAudio();
        
        this.setBackground(Color.BLACK);
        
        // creates ship and ship statistic meters (health + ammo)
        ship = new Ship(im.getImage(SpriteManager.SHIP), new Point(296, 640), 3, new Velocity(0, 0), 9, new ShipMovementLimitProvider(24, 568, 505, 640), im, am);
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
            yStarChange = difficulty + 3;
            
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
            getProjectiles().stream().map((projectile) -> {
                projectile.projectileTimeTaskHandler();
                // removes projectiles that go off screen
                return projectile;
            }).filter((projectile) -> (projectile.getX() < -projectile.getWidth() || projectile.getY() < -projectile.getHeight() || projectile.getX() > 640 || projectile.getY() > 640)).forEach((projectile) -> {
                outOfBounds.add(projectile);
            });
            projectiles.removeAll(outOfBounds);
        }
        
        if (enemies != null && !paused) {
            alienTimer++;
            getEnemies().stream().map((enemy) -> {
                if (random(800 / (difficulty + 4)) == 0) {
                    if (enemy.getType() == Enemy.MEDIUM) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_SMALL_GREEN), new Point(enemy.getX() + (7 * enemy.getSize()), enemy.getY() + (5 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + (difficulty * 3)), 1, false, im));
                    } else if (enemy.getType() == Enemy.LARGE) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_MEDIUM_BLUE), new Point(enemy.getX() + (7 * enemy.getSize()), enemy.getY() + (3 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + (difficulty * 3)), 2, false, im));
                    } else if (enemy.getType() == Enemy.SMALL) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_SMALL_YELLOW), new Point(enemy.getX() + (5 * enemy.getSize()), enemy.getY() + (5 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + (difficulty * 3)), 1, false, im));
                    }
                }
                if (enemy.getY() >= enemy.getMaxY() && !enemy.targetingShip()) {
                    enemy.targetShip();
                } else if (enemy.targetingShip()) {
                    enemy.setVelocity(TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 16.0));
                    enemy.enemyTimeTaskHandler();
                } else if (enemy.getY() >= enemy.getMinStartY() && !enemy.isCentering()) {
                    enemy.center();
                } else if (enemy.isCentering()) {
                    enemy.setVelocity(0, 6);
                    if (enemy.getY() >= enemy.getMinY()) {
                        enemy.center();
                        enemy.setVelocity(0, 0);
                    }
                    enemy.enemyTimeTaskHandler();
                }
                return enemy;
            }).filter((enemy) -> (alienTimer >= 20 - (difficulty * 2) && !enemy.targetingShip())).forEach((enemy) -> {
                if (timerTick <= 5) {
                    enemy.setVelocity(6, 0);
                } else if (timerTick >= 7 && timerTick <= 12) {
                    enemy.setVelocity(-6, 0);
                } else {
                    enemy.setVelocity(0, 6);
                }
                if (timerTick == 1 || timerTick == 3 || timerTick == 5 || timerTick == 7 || timerTick == 9 || timerTick == 11 || timerTick == 13) {
                    enemy.setFrame(1);
                } else {
                    enemy.setFrame(0);
                }
                enemy.enemyTimeTaskHandler();
            });
            if (alienTimer >= 20 - (difficulty * 2)) {
                alienTimer = 0;
                timerTick++;
                if (timerTick <= 6) {
                    displacement += 6;
                } else if (timerTick >= 8 && timerTick <= 13) {
                    displacement -= 6;
                } else if (timerTick >= 14) {
                    timerTick = 0;
                }
            }
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
            ArrayList<Enemy> noHealth = new ArrayList<>();
            ArrayList<Projectile> hitEnemy = new ArrayList<>();
            getProjectiles().stream().filter((projectile) -> (projectile.isFriendly())).forEach((projectile) -> {
                getEnemies().stream().filter((enemy) -> (projectile.getObjectBoundary().intersects(enemy.getObjectBoundary()))).map((enemy) -> {
                    enemy.Damage(projectile.getDamage());
                    return enemy;
                }).map((enemy) -> {
                    if (enemy.getHealth() <= 0) {
                        noHealth.add(enemy);
                        am.playAudio(AudioManager.KILL_ALIEN, false);
                    } else {
                        am.playAudio(AudioManager.HURT_ALIEN, false);
                    }
                    return enemy;
                }).forEach((_item) -> {
                    hitEnemy.add(projectile);
                });
                getProjectiles().stream().filter((enemyProjectile) -> (!enemyProjectile.isFriendly() && enemyProjectile.getObjectBoundary().intersects(projectile.getObjectBoundary()))).forEach((enemyProjectile) -> {
                    hitEnemy.add(projectile);
                    hitEnemy.add(enemyProjectile);
                });
            });
            getProjectiles().stream().filter((projectile) -> (!projectile.isFriendly() && projectile.getObjectBoundary().intersects(ship.getObjectBoundary()))).map((projectile) -> {
                if (ship.getPowerUp() != Ship.SHIELD) {
                    ship.Damage(projectile.getDamage());
                }
                return projectile;
            }).forEach((projectile) -> {
                hitEnemy.add(projectile);
            });
            for (Enemy enemy : enemies) {
                if (enemy.getObjectBoundary().intersects(ship.getObjectBoundary())) {
                    if (enemy.getType() == Enemy.LARGE) {
                        ship.Damage(8);
                    } else if (enemy.getType() == Enemy.MEDIUM) {
                        ship.Damage(6);
                    } else {
                        ship.Damage(4);
                    }
                    noHealth.add(enemy);
                    
                    //TODO play sound on hit
                    
                }
            }
            enemies.removeAll(noHealth);
            projectiles.removeAll(hitEnemy);
        }
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            paused = !paused;
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            menuMusic = !menuMusic;
            if (menuMusic == true) {
                am.playAudio(AudioManager.MENU, true);
            } else {
                am.stopAudio(AudioManager.MENU);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_W) {
            gameMusic = !gameMusic;
            if (gameMusic == true) {
                am.playAudio(AudioManager.GAME, true);
            } else {
                am.stopAudio(AudioManager.GAME);
            }
        } else if (!paused && ship.getY() == ship.getMinY()) {
            
            if (e.getKeyCode() == KeyEvent.VK_SPACE && !spacebarDebug) {
                if (ship != null) {
                    fire();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_D && !rightDebug) {
                // right movement for ship
                if (ship != null) {
                    ship.accelerate(ship.getSpeed());
                }
                rightDebug = true;
                
            } else if (e.getKeyCode() == KeyEvent.VK_A && !leftDebug) {
                if (ship != null) {
                    ship.accelerate(-ship.getSpeed());
                }
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
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_U) {
                // summons medium alien row
                summonAliens(36, -48, Enemy.MEDIUM, 7, 78, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_I) {
                // summons large alien row
                summonAliens(36, -48, Enemy.LARGE, 5, 114, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_1) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -102, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -210, Enemy.SMALL, 9, 60, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_4) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_5) {
                // summons small alien row
                summonAliens(36, -48, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_6) {
                // summons small alien row
                summonAliens(36, -48, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -102, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -156, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -210, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_7) {
                // summons small alien row
                summonAliens(36, -48, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -102, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_8) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -316, Enemy.SMALL, 9, 60, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_9) {
                // summons small alien row
                summonAliens(36, -48, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3);
                summonAliens(36, -316, Enemy.MEDIUM, 7, 78, 3);
            } else if (e.getKeyCode() == KeyEvent.VK_P) {
                // speeds up background
                difficulty += 1;
            } else if (e.getKeyCode() == KeyEvent.VK_O && difficulty > 0) {
                // slows up background
                difficulty -= 1;
            }
        }
    }
    
    private void fire(){
        // tests if thie ship has ammunition
                if (0 < ship.getEnergy() || ship.getPowerUp() == Ship.RAPID_FIRE) {
                    // creates projectiles, removes 1 energy, resets cooldown
                    ship.fire();
                    projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE), new Point(ship.getX() + (3 * ship.getSize()), ship.getY() + ship.getSize()), ship.getSize(), new Velocity(0, -36), 1, true, im));
                    projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE), new Point(ship.getX() + (3 * ship.getSize() * 4), ship.getY() + ship.getSize()), ship.getSize(), new Velocity(0, -36), 1, true, im));
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
    
    private void summonAliens(int x, int y, int type, int amount, int distance, int size){
        if (type == Enemy.SMALL){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.YELLOW_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 15 + (difficulty * 3), type, im, new EnemyMovementLimitProvider(-48, 18, 216)));
            }
        } else if (type == Enemy.MEDIUM){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.GREEN_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 24 + (difficulty * 4), type, im, new EnemyMovementLimitProvider(-48, 18, 216)));
            }
        } else if (type == Enemy.LARGE){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.BLUE_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 38 + (difficulty * 6), type, im, new EnemyMovementLimitProvider(-48, 18, 216)));
            }
        } else {
            System.out.println("ERROR summoning alien wave: Alien type not valid");
        }
    }
    
    @Override
    public void keyReleasedHandler(KeyEvent e) {
        // when releasing the right key, stops ship from moving right
        if (e.getKeyCode() == KeyEvent.VK_D && rightDebug) {
                ship.accelerate(-ship.getSpeed(), 0);
                rightDebug = false;
                // when releasing the left key, stops ship from moving right
        } else if ( e.getKeyCode() == KeyEvent.VK_A && leftDebug) {
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
        
        getProjectiles().stream().forEach((theProjectile) -> {
            theProjectile.draw(graphics);
        });
        
        if (ship != null) {
            ship.draw(graphics);
        }
        
        getEnemies().stream().forEach((theEnemy) -> {
            theEnemy.draw(graphics);
        });
        
        if (healthMeter != null) {
            healthMeter.draw(graphics);
        }
        
        if (energyMeter != null) {
            energyMeter.draw(graphics);
        }
        
        if (powerMeter != null && ship.getPowerUpTime() > 0) {
            powerMeter.draw(graphics);
        }
        
//        graphics.drawImage(im.getImage(SpriteManager.MOTHER_SHIP), 24, 24, 122 * 3, 47 * 3, this);
        
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
