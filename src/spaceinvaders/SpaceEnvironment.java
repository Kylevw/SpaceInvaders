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
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private ArrayList<PowerOrb> powerOrbs;
    private ArrayList<TextBox> textBoxs;
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
    private int score;
    
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
    
    private ArrayList<PowerOrb> getPowerOrbs(){
        return new ArrayList<>(powerOrbs);
    }
    
    private ArrayList<TextBox> getTextBoxs(){
        return new ArrayList<>(textBoxs);
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
        
        am.playAudio(AudioManager.GAME, true);
        // creates background stars
        textBoxs = new ArrayList<>();
        stars = new ArrayList<>();
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        powerOrbs = new ArrayList<>();
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
    
    Font spacefont, spacefont_40, spacefont_24, spacefont_32;

    @Override
    public void initializeEnvironment() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("spaceinvaders/spacefont.ttf");

            spacefont = Font.createFont(Font.TRUETYPE_FONT, input);
            spacefont_24 = spacefont.deriveFont((float)24.0);
            spacefont_32 = spacefont.deriveFont((float)32.0);
            spacefont_40 = spacefont.deriveFont((float)40.0);

        } catch (FontFormatException ex) {
            Logger.getLogger(SpaceEnvironment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SpaceEnvironment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void timerTaskHandler() {
        
        if (ship == null) {
            if (healthMeter != null) {
                healthMeter = null;
            }
            if (energyMeter != null) {
                energyMeter = null;
            }
        } else {
            if (ship.getHealth() <= 0) {
                ship = null;
                am.playAudio(AudioManager.MOTHERSHIP_EXPLODE, false);
                stopMusic();
                am.playAudio(AudioManager.MENU, true);
                difficulty = -1;
            }
            if (healthMeter == null) {
                healthMeter = new StatMeter(StatMeter.RED, 16, 4, 480, 568, 3, im);
            }
            if (energyMeter == null) {
                energyMeter = new StatMeter(StatMeter.BLUE, 16, 0, 480, 589, 3, im);
            }
        }
        
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
        
        if (textBoxs != null && !paused) {
            ArrayList<TextBox> maxTime = new ArrayList<>();
            getTextBoxs().stream().map((textBox) -> {
                textBox.textBoxTimeTaskHandler();
                return textBox;
            }).filter((textBox) -> (textBox.getTime() >= textBox.getMaxTime())).forEach((textBox) -> {
                maxTime.add(textBox);
            });
            textBoxs.removeAll(maxTime);
        }
        
        if (powerOrbs != null && !paused) {
            ArrayList<PowerOrb> outOfBounds = new ArrayList<>();
            getPowerOrbs().stream().map((powerOrb) -> {
                powerOrb.powerOrbTimeTaskHandler();
                return powerOrb;
            }).filter((powerOrb) -> (powerOrb.getX() >= 640 || powerOrb.getX() <= -powerOrb.getWidth() || powerOrb.getY() >= 640 || powerOrb.getY() <= -powerOrb.getHeight())).forEach((powerOrb) -> {
                outOfBounds.add(powerOrb);
            });
            powerOrbs.removeAll(outOfBounds);
        }
        
        // controls movement of projeciltes
        if (projectiles != null && !paused) {
            ArrayList<Projectile> outOfBounds = new ArrayList<>();
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
            ArrayList<Enemy> noHealth = new ArrayList<>();
            getEnemies().stream().map((enemy) -> {
                if (enemy.getY() >= 640) {
                    noHealth.add(enemy);
                } else if (enemy.getHealth() <= 0) {
                    if (enemy.getType() != Enemy.MOTHERSHIP) {
                        textBoxs.add(new TextBox(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - 26, enemy.getY() + (enemy.getHeight() * enemy.getSize()) - 8, 40, true, spacefont_24, "" + (100 + (50 * enemy.getType()) + (25 * difficulty))));
                        score += 100 + (enemy.getType() * 50) + (25 * difficulty);
                        noHealth.add(enemy);
                        am.playAudio(AudioManager.KILL_ALIEN, false);
                        int powerUpRandomizer = random(12);
                        if (powerUpRandomizer == 0) {
                            if (enemy.getType() == Enemy.LARGE) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_RAPID_FIRE), new Point(enemy.getX() + 18, enemy.getY() + 12), 4, new Velocity(0, 8), PowerOrb.RAPID_FIRE, im));
                            } else if (enemy.getType() == Enemy.SMALL) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SHIELD), new Point(enemy.getX() + 12, enemy.getY() + 12), 4, new Velocity(0, 8), PowerOrb.SHIELD, im));
                            } else if (enemy.getType() == Enemy.MEDIUM) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SPEED), new Point(enemy.getX() + 15, enemy.getY() + 12), 4, new Velocity(0, 8), PowerOrb.SPEED, im));
                            }
                        }
                    } else {
                            if (enemy.getDeathTimer() == 0) {
                                enemy.killMotherShip();
                                enemy.setAttackTimer(0);
                                enemy.shootBeam(false);
                                stopMusic();
                                am.playAudio(AudioManager.GAME, true);
                            }
                        }
                    }
                if (ship != null && random(800 / (difficulty + 4)) == 0) {
                    if (enemy.getType() == Enemy.MEDIUM) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_SMALL_GREEN), new Point(enemy.getX() + (7 * enemy.getSize()), enemy.getY() + (5 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + (difficulty * 2)), 1, false, im));
                    } else if (enemy.getType() == Enemy.LARGE) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_MEDIUM_BLUE), new Point(enemy.getX() + (7 * enemy.getSize()), enemy.getY() + (3 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + (difficulty * 2)), 2, false, im));
                    } else if (enemy.getType() == Enemy.SMALL) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_SMALL_YELLOW), new Point(enemy.getX() + (5 * enemy.getSize()), enemy.getY() + (5 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + (difficulty * 2)), 1, false, im));
                    }
                }
                if (ship != null && enemy.getY() >= enemy.getMaxY() && !enemy.targetingShip()) {
                    enemy.targetShip();
                } else if (enemy.targetingShip()) {
                    if (ship == null) {
                        enemy.targetShip();
                    } else {
                        enemy.setVelocity(TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 16.0));
                        enemy.enemyTimeTaskHandler();
                    }
                } else if (enemy.getY() >= enemy.getMinStartY() && !enemy.isCentering() && enemy.getY() < enemy.getMinY()) {
                    enemy.center();
                } else if (enemy.isCentering()) {
                    if (enemy.getType() == Enemy.MOTHERSHIP) {
                        enemy.setVelocity(0, 1);
                    } else {
                        enemy.setVelocity(0, 6);
                    }
                    if (enemy.getY() >= enemy.getMinY() && enemy.isCentering()) {
                        enemy.center();
                        if (enemy.getType() == Enemy.MOTHERSHIP) {
                            enemy.setVelocity(2 + difficulty, 0);
                        } else {
                         enemy.setVelocity(0, 0);
                        }
                    }
                    enemy.enemyTimeTaskHandler();
                    if (enemy.getDeathTimer() >= 200) {
                        noHealth.add(enemy);
                        textBoxs.add(new TextBox(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - 34, enemy.getY() + (enemy.getHeight() * enemy.getSize() / 2), 40, true, spacefont_24, "" + (5000 + (1000 * difficulty))));
                        score += 5000 + (1000 * difficulty);
                    }
                } else if (ship != null && enemy.getType() == Enemy.MOTHERSHIP && !enemy.isCentering()) {
                    enemy.enemyTimeTaskHandler();
                    if (alienTimer == 1 && timerTick <= 2) {
                        enemy.setAlienSummonTimer(enemy.getAlienSummonTimer() + 1);
                        if (enemy.getAlienSummonTimer() >= 16) {
                            int i = random(3);
                            if (i == 0) {
                                summonAliens(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - 144, enemy.getY() + (enemy.getHeight() * enemy.getSize() / 2), Enemy.SMALL, 5, 60, 3, enemy.getY() + ((enemy.getHeight() + 1) * enemy.getSize()), 306);
                            } else if (i == 1) {
                                summonAliens(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - 168, enemy.getY() + (enemy.getHeight() * enemy.getSize() / 2), Enemy.MEDIUM, 4, 72, 3, enemy.getY() + ((enemy.getHeight() + 1) * enemy.getSize()), 306);
                            } else if (i == 2) {
                                summonAliens(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - 120, enemy.getY() + (enemy.getHeight() * enemy.getSize() / 2), Enemy.LARGE, 3, 84, 3, enemy.getY() + ((enemy.getHeight() + 1) * enemy.getSize()), 306);
                            }
                            enemy.setAlienSummonTimer(0);
                        }
                    }
                    if (enemy.getAttackTimer() >= 4) {
                        if (random(4) == 0) {
                            enemy.shootBeam(true);
                            enemy.setAttackTimer(0);
                            am.playAudio(AudioManager.MOTHERSHIP_EXPLODE, false);
                        } else if (random(4) == 0) {
                            enemy.setAttackTimer(-300);
                            powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SHIELD), new Point(enemy.getX() + ((enemy.getWidth() / 2 * enemy.getSize()) - 12), enemy.getY() + ((enemy.getHeight() / 2 * enemy.getSize()) - 12)), 4, new Velocity(0, 8), PowerOrb.SHIELD, im));
                        } else {
                            enemy.setAttackTimer(-60);
                        }
                    } else if (enemy.getAttackTimer() < -1) {
                        if (enemy.getAttackTimer() < -60 && enemy.getAttackTimer() > -220) {
                            if (enemy.getAttackTimer() == -61) {
                                enemy.setAttackTimer(0);
                            }
                            projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_LARGE_YELLOW), new Point(enemy.getX() + (((enemy.getWidth() * 3 / 4) - 1) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() * 3 / 4) * enemy.getSize())), (enemy.getSize() - 1), TrigonometryCalculator.calculateVelocity(new Point(enemy.getX() + ((enemy.getWidth() * 3 / 4) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() / 2) * enemy.getSize())), ship.getCenterOfMass(), 12.0 + (difficulty * 2)), 3, false, im));
                            projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_LARGE_YELLOW), new Point(enemy.getX() + (((enemy.getWidth() / 2) - 1) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() * 3 / 4) * enemy.getSize())), (enemy.getSize() - 1), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 12.0 + (difficulty * 2)), 3, false, im));
                            projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_LARGE_YELLOW), new Point(enemy.getX() + (((enemy.getWidth() / 4) - 1) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() * 3 / 4) * enemy.getSize())), (enemy.getSize() - 1), TrigonometryCalculator.calculateVelocity(new Point(enemy.getX() + ((enemy.getWidth() / 4) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() / 2) * enemy.getSize())), ship.getCenterOfMass(), 12.0 + (difficulty * 2)), 3, false, im));
                        } else if (ship != null && enemy.getAttackTimer() >= -60) {
                            projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_LARGE_BLUE), new Point(enemy.getX() + (((enemy.getWidth() * 3 / 4) - 1) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() * 3 / 4) * enemy.getSize())), (enemy.getSize() - 1), TrigonometryCalculator.calculateVelocity(new Point(enemy.getX() + ((enemy.getWidth() * 3 / 4) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() / 2) * enemy.getSize())), ship.getCenterOfMass(), 12.0 + (difficulty * 2)), 3, false, im));
                            projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_LARGE_BLUE), new Point(enemy.getX() + (((enemy.getWidth() / 2) - 1) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() * 3 / 4) * enemy.getSize())), (enemy.getSize() - 1), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 12.0 + (difficulty * 2)), 3, false, im));
                            projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_LARGE_BLUE), new Point(enemy.getX() + (((enemy.getWidth() / 4) - 1) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() * 3 / 4) * enemy.getSize())), (enemy.getSize() - 1), TrigonometryCalculator.calculateVelocity(new Point(enemy.getX() + ((enemy.getWidth() / 4) * enemy.getSize()), enemy.getY() + ((enemy.getHeight() / 2) * enemy.getSize())), ship.getCenterOfMass(), 12.0 + (difficulty * 2)), 3, false, im));
                        }
                        enemy.setAttackTimer(enemy.getAttackTimer() + 1);
                    } else if (enemy.shootingBeam() && enemy.getAttackTimer() == 2) {
                        enemy.shootBeam(false);
                    }
                } else if (ship == null) {
                    if (enemy.shootingBeam()) {
                        enemy.shootBeam(false);
                    } else if (enemy.getAttackTimer() != 0) {
                        enemy.setAttackTimer(0);
                    }
                }
                return enemy;
            }).filter((enemy) -> (alienTimer >= 20 - (difficulty * 2) && enemy.getType() != Enemy.MOTHERSHIP && !enemy.targetingShip())).forEach((enemy) -> {
                if (timerTick <= 5) {
                    enemy.setVelocity(6, 0);
                } else if (timerTick >= 7 && timerTick <= 12) {
                    enemy.setVelocity(-6, 0);
                } else {
                    enemy.setVelocity(0, 6);
                }
                if ((timerTick + 1) / 2 == (timerTick + 2) / 2) {
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
            enemies.removeAll(noHealth);
        }
        
        // controls movement of ship and stat meters
        if (ship != null && !paused) {
            checkHitStatus();
            
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
    }
    
    private void checkHitStatus(){
        if (enemies != null && projectiles != null) {
            ArrayList<Enemy> noHealth = new ArrayList<>();
            ArrayList<Projectile> hitEnemy = new ArrayList<>();
            ArrayList<PowerOrb> hitShip = new ArrayList<>();
            getProjectiles().stream().filter((projectile) -> (projectile.isFriendly())).forEach((projectile) -> {
                getEnemies().stream().filter((enemy) -> (projectile.getObjectBoundary().intersects(enemy.getObjectBoundary()))).map((enemy) -> {
                    enemy.Damage(projectile.getDamage());
                    return enemy;
                }).map((enemy) -> {
                    am.playAudio(AudioManager.HURT_ALIEN, false);
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
                ship.Damage(projectile.getDamage());
                return projectile;
            }).forEach((projectile) -> {
                hitEnemy.add(projectile);
            });
            getEnemies().stream().filter((enemy) -> (enemy.getType() == Enemy.MOTHERSHIP && enemy.shootingBeam() && enemy.getBeamHitbox().intersects(ship.getObjectBoundary()))).forEach((_item) -> {
                ship.removePowerUp();
                ship.Damage(4);
            });
            getEnemies().stream().filter((enemy) -> (enemy.getObjectBoundary().intersects(ship.getObjectBoundary()))).map((enemy) -> {
                ship.setInvulTimer(0);
                ship.removePowerUp();
                if (enemy.getType() == Enemy.LARGE) {
                    ship.Damage(8);
                } else if (enemy.getType() == Enemy.MEDIUM) {
                    ship.Damage(6);
                } else {
                    ship.Damage(4);                    
                }
                return enemy;
            }).forEach((enemy) -> {
                noHealth.add(enemy);
                
                //TODO play sound on hit
            });
            getPowerOrbs().stream().filter((powerOrb) -> (powerOrb.getObjectBoundary().intersects(ship.getObjectBoundary()))).map((powerOrb) -> {
                ship.togglePowerUp(powerOrb.getType());
                score += 25;
                return powerOrb;
            }).forEach((powerOrb) -> {
                hitShip.add(powerOrb);
                
                //TODO play sound on hit
            });
            enemies.removeAll(noHealth);
            projectiles.removeAll(hitEnemy);
            powerOrbs.removeAll(hitShip);
        }
    }
    
    private void stateLevel(){
        textBoxs.add(new TextBox(212 - (((((difficulty + 9) / 10) * 10 + level) / 10) * 18), 300, 120, false, spacefont_40, "LEVEL " + (level + (10 * difficulty))));
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            paused = !paused;
        } else if (ship != null && !paused && ship.getY() == ship.getMinY()) {
            
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
            } else if (e.getKeyCode() == KeyEvent.VK_1) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -102, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3, 40, 264);
                level = 1;
                stateLevel();
            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3, 40, 264);
                level = 2;
                stateLevel();
            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -210, Enemy.SMALL, 9, 60, 3, 40, 264);
                textBoxs.add(new TextBox(208, 300, 120, false, spacefont_40, "LEVEL " + (3 + (10 * difficulty))));
            } else if (e.getKeyCode() == KeyEvent.VK_4) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                textBoxs.add(new TextBox(208, 300, 120, false, spacefont_40, "LEVEL " + (4 + (10 * difficulty))));
            } else if (e.getKeyCode() == KeyEvent.VK_5) {
                // summons small alien row
                summonAliens(36, -48, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                textBoxs.add(new TextBox(210, 300, 120, false, spacefont_40, "LEVEL " + (5 + (10 * difficulty))));
            } else if (e.getKeyCode() == KeyEvent.VK_6) {
                // summons small alien row
                summonAliens(36, -48, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -102, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -156, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -210, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3, 40, 264);
                textBoxs.add(new TextBox(208, 300, 120, false, spacefont_40, "LEVEL " + (6 + (10 * difficulty))));
            } else if (e.getKeyCode() == KeyEvent.VK_7) {
                // summons small alien row
                summonAliens(36, -48, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -102, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3, 40, 264);
                textBoxs.add(new TextBox(210, 300, 120, false, spacefont_40, "LEVEL " + (7 + (10 * difficulty))));
            } else if (e.getKeyCode() == KeyEvent.VK_8) {
                // summons small alien row
                summonAliens(36, -48, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -156, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -316, Enemy.SMALL, 9, 60, 3, 40, 264);
                textBoxs.add(new TextBox(208, 300, 120, false, spacefont_40, "LEVEL " + (8 + (10 * difficulty))));
            } else if (e.getKeyCode() == KeyEvent.VK_9) {
                // summons small alien row
                summonAliens(36, -48, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -102, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -156, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -210, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -262, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -316, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                textBoxs.add(new TextBox(208, 300, 120, false, spacefont_40, "LEVEL " + (9 + (10 * difficulty))));
            } else if (e.getKeyCode() == KeyEvent.VK_0) {
                stopMusic();
                am.playAudio(AudioManager.MOTHERSHIP, true);
                enemies.add(new Enemy(im.getImage(SpriteManager.MOTHERSHIP), new Point(76, -188), 4, new Velocity(0, 1), 320 + (difficulty * 120), Enemy.MOTHERSHIP, im, new EnemyMovementLimitProvider(-48, 36, 216, 0, 152), am));
                textBoxs.add(new TextBox(208, 300, 120, false, spacefont_40, "LEVEL " + (10 + (10 * difficulty))));
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
    
    public void stopMusic() {
        am.stopAudio(AudioManager.GAME);
        am.stopAudio(AudioManager.MENU);
        am.stopAudio(AudioManager.MOTHERSHIP);
    }
    
    private void summonAliens(int x, int y, int type, int amount, int distance, int size, int marchYPoint, int attackYPoint) {
        if (type == Enemy.SMALL){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.YELLOW_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 13 + (difficulty * 2), type, im, new EnemyMovementLimitProvider(-48, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else if (type == Enemy.MEDIUM){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.GREEN_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 22 + (difficulty * 3), type, im, new EnemyMovementLimitProvider(-48, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else if (type == Enemy.LARGE){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.BLUE_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 36 + (difficulty * 4), type, im, new EnemyMovementLimitProvider(-48, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else {
            System.out.println("ERROR summoning alien wave: Alien type not valid");
        }
    }
    
    @Override
    public void keyReleasedHandler(KeyEvent e) {
        // when releasing the right key, stops ship from moving right
        if (ship != null) {
            if (e.getKeyCode() == KeyEvent.VK_D && rightDebug) {
                    ship.accelerate(-ship.getSpeed(), 0);
                    rightDebug = false;
                    // when releasing the left key, stops ship from moving right
            } else if (e.getKeyCode() == KeyEvent.VK_A && leftDebug) {
                    ship.accelerate(ship.getSpeed(), 0);
                    leftDebug = false;
                    // allows the SPACE key to be pressed again after being released
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
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
        
        getPowerOrbs().stream().forEach((thePowerOrb) -> {
            thePowerOrb.draw(graphics);
        });
        
        getProjectiles().stream().forEach((theProjectile) -> {
            theProjectile.draw(graphics);
        });
        
        getEnemies().stream().filter((theEnemy) -> (theEnemy.getType() != Enemy.MOTHERSHIP)).forEach((theEnemy) -> {
            theEnemy.draw(graphics);
        });
        
        getEnemies().stream().filter((theEnemy) -> (theEnemy.getType() == Enemy.MOTHERSHIP)).forEach((theEnemy) -> {
            theEnemy.draw(graphics);
        });
        
        getTextBoxs().stream().forEach((theTextBox) -> {
            theTextBox.draw(graphics);
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
        
        if (powerMeter != null && ship != null && ship.getPowerUpTime() > 0) {
            powerMeter.draw(graphics);
        }
        
        graphics.setColor(Color.WHITE);
        graphics.setFont(spacefont_32);
        graphics.drawString("SCORE: " + score, 0, 28);
        
        
        // pause menu
        if (paused) {
            graphics.setColor(new Color(0, 0, 0, 100));
            graphics.fillRect(0, 0, 640, 640);
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_40);
            graphics.drawString("PAUSED", 218, 100);
        }
    }
}
