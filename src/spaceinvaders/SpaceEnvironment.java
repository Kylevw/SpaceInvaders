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
    private boolean paused;
    private int selectedButton;
    
    
    private int alienTimer;
    private int timerTick;
    private int textBoxTimer;
    private int displacement;
    
    private boolean leftDebug;
    private boolean rightDebug;
    
    private int menuState;
    private boolean inGame;
    private int musicTimer;
    private int level;
    private int levelUpTimer;
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
        
        menuState = 4;
        difficulty = -1;
        
        loadImages();
        loadAudio();
        
        am.playAudio(AudioManager.MENU, true);
        
        this.setBackground(Color.BLACK);
        
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
    
    //<editor-fold defaultstate="collapsed" desc="IntfLoaders">
    
    public void loadImages() {
        im = new SpriteManager();
    }
    
    public void loadAudio() {
        am = new AudioManager();
    }
    
    //</editor-fold>
    
    // random number generator
    public int random(int value) {
        return (int) (Math.random() * value);
    }
    
    Font spacefont, spacefont_20, spacefont_12, spacefont_24, spacefont_32;

    @Override
    public void initializeEnvironment() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("spaceinvaders/spacefont.ttf");

            spacefont = Font.createFont(Font.TRUETYPE_FONT, input);
            spacefont_24 = spacefont.deriveFont((float)24.0);
            spacefont_32 = spacefont.deriveFont((float)32.0);
            spacefont_20 = spacefont.deriveFont((float)20.0);
            spacefont_12 = spacefont.deriveFont((float)12.0);

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
                menuState = 3;
                ArrayList<Enemy> aboveScreen = new ArrayList<>();
                getEnemies().stream().filter((enemy) -> (enemy.getY() <= -enemy.getWidth() * enemy.getSize())).forEach((enemy) -> {
                    aboveScreen.add(enemy);
                });
                enemies.removeAll(aboveScreen);
                am.playAudio(AudioManager.MOTHERSHIP_EXPLODE, false);
                stopMusic();
                am.playAudio(AudioManager.MENU, true);
                level += difficulty * 10;
                difficulty = -1;
            }
            if (healthMeter == null) {
                healthMeter = new StatMeter(StatMeter.RED, 16, 4, 480, 568, 3, im);
            }
            if (energyMeter == null) {
                energyMeter = new StatMeter(StatMeter.BLUE, 16, 0, 480, 589, 3, im);
            }
        }
        textBoxTimer++;
        if (textBoxTimer >= 120) {
            textBoxTimer = 0;
        }
        
        if (selectedButton < 0) {
            selectedButton = 3;
        } else if (selectedButton > 3) {
            selectedButton = 0;
        }
        
        if (!paused) {
            // controls movement of the stars
            if (stars != null) {
                yStarChange = difficulty + 3;
                stars.stream().forEach((theStar) -> {
                    theStar.setY(yStarChange);
                    if (theStar.getY() >= 640) {
                        theStar.resetStar();
                    }
                });
            }
        
            if (textBoxs != null) {
                ArrayList<TextBox> maxTime = new ArrayList<>();
                getTextBoxs().stream().map((textBox) -> {
                    textBox.textBoxTimeTaskHandler();
                    return textBox;
                }).filter((textBox) -> (textBox.getTime() >= textBox.getMaxTime())).forEach((textBox) -> {
                    maxTime.add(textBox);
                });
                textBoxs.removeAll(maxTime);
            }
        
            if (powerOrbs != null) {
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
            if (projectiles != null) {
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
        
            if (enemies != null) {
                alienTimer++;
                enemyAI();
                
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
            
            if (enemies != null && enemies.isEmpty() && menuState == 0) {
                levelUpTimer++;
                if (levelUpTimer >= 80) {
                    levelUpTimer = 0;
                    level++;
                    if (level == 10) {
                        musicTimer = 1;
                    }
                    if (level > 10) {
                        difficulty++;
                        level = 1;
                    }
                    summonAlienWave();
                    stateLevel();
                }
            }
            
            if (musicTimer > 0) {
                    musicTimer++;
                    if (musicTimer >= 340) {
                        am.playAudio(AudioManager.MOTHERSHIP, true);
                        musicTimer = 0;
                    }
                }
            
            
            
            // controls movement of ship and stat meters
            if (ship != null) {
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
    
    private void enemyAI(){
        ArrayList<Enemy> noHealth = new ArrayList<>();
        getEnemies().stream().map((enemy) -> {
        if (enemy.getY() >= 640) {
                    noHealth.add(enemy);
                } else if (enemy.getHealth() <= 0) {
                    if (enemy.getType() != Enemy.MOTHERSHIP) {
                        textBoxs.add(new TextBox(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - 30, enemy.getY() + (enemy.getHeight() * enemy.getSize()) - 8, 40, true, false, spacefont_20, "" + (100 + (50 * enemy.getType()) + (25 * difficulty))));
                        score += 100 + (enemy.getType() * 50) + (25 * difficulty);
                        noHealth.add(enemy);
                        am.playAudio(AudioManager.KILL_ALIEN, false);
                        int powerUpRandomizer = random(12);
                        if (powerUpRandomizer == 0) {
                            if (enemy.getType() == Enemy.LARGE) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_RAPID_FIRE), new Point(enemy.getX() + 12, enemy.getY()), 4, new Velocity(0, 8), PowerOrb.RAPID_FIRE, im));
                            } else if (enemy.getType() == Enemy.SMALL) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SHIELD), new Point(enemy.getX(), enemy.getY()), 4, new Velocity(0, 8), PowerOrb.SHIELD, im));
                            } else if (enemy.getType() == Enemy.MEDIUM) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SPEED), new Point(enemy.getX() + 6, enemy.getY()), 4, new Velocity(0, 8), PowerOrb.SPEED, im));
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
                        textBoxs.add(new TextBox(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - 40, enemy.getY() + (enemy.getHeight() * enemy.getSize() / 2), 40, true, false, spacefont_20, "" + (5000 + (1000 * difficulty))));
                        score += 5000 + (1500 * difficulty);
                    }
                } else if (ship != null && enemy.getType() == Enemy.MOTHERSHIP && !enemy.isCentering()) {
                    enemy.enemyTimeTaskHandler();
                    if (alienTimer == 1 && timerTick <= 2) {
                        enemy.setAlienSummonTimer(enemy.getAlienSummonTimer() + 1);
                        if (enemy.getAlienSummonTimer() >= 16 && enemy.getHealth() > 0) {
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
                            powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SHIELD), new Point(enemy.getX() + ((enemy.getWidth() / 2 * enemy.getSize()) - 20), enemy.getY() + ((enemy.getHeight() / 2 * enemy.getSize()) - 12)), 4, new Velocity(0, 8), PowerOrb.SHIELD, im));
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
        enemies.removeAll(noHealth);
    }
    
    private void stateLevel(){
        if (level % 10 == 0) {
            textBoxs.add(new TextBox(208 - (((((difficulty + 9) / 10) * 10 + level) / 10) * 16), 300, 320, false, true, spacefont_32, "LEVEL " + (level + (10 * difficulty))));
        } else {
            textBoxs.add(new TextBox(208 - (((((difficulty + 9) / 10) * 10 + level) / 10) * 16), 300, 120, false, false, spacefont_32, "LEVEL " + (level + (10 * difficulty))));
        }
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (menuState == 0 && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            paused = !paused;
        } else if (ship != null && menuState == 0 && !paused && ship.getY() == ship.getMinY()) {
            
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
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 2;
            } else if (e.getKeyCode() == KeyEvent.VK_4) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 3;
            } else if (e.getKeyCode() == KeyEvent.VK_5) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 4;
            } else if (e.getKeyCode() == KeyEvent.VK_6) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 5;
            } else if (e.getKeyCode() == KeyEvent.VK_7) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 6;
            } else if (e.getKeyCode() == KeyEvent.VK_8) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 7;
            } else if (e.getKeyCode() == KeyEvent.VK_9) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 8;
            } else if (e.getKeyCode() == KeyEvent.VK_0) {
                textBoxs.removeAll(textBoxs);
                levelUpTimer = 80;
                enemies.removeAll(enemies);
                level = 9;
            } else if (e.getKeyCode() == KeyEvent.VK_P) {
                // speeds up background
                difficulty += 1;
            } else if (e.getKeyCode() == KeyEvent.VK_O && difficulty > 0) {
                // slows up background
                difficulty -= 1;
            }
        } else if (menuState == 0 && paused) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (selectedButton == 0) {
                    paused = false;
                } else if (selectedButton == 3) {
                    inGame = false;
                    stopMusic();
                    ship = null;
                    enemies.removeAll(enemies);
                    difficulty = -1;
                    menuState = 4;
                    paused = false;
                    selectedButton = 0;
                    am.playAudio(AudioManager.MENU, true);
                    
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                selectedButton++;
            } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                selectedButton--;
            }
        } else if (menuState == 3) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                enemies.removeAll(enemies);
                inGame = false;
                menuState = 4;
            }
        } else if (menuState == 4) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                inGame = true;
                level = 0;
                difficulty = 0;
                levelUpTimer = -140;
                menuState = 0;
                ship = new Ship(im.getImage(SpriteManager.SHIP), new Point(296, 640), 3, new Velocity(0, 0), 9, new ShipMovementLimitProvider(24, 568, 505, 640), im, am);
                stopMusic();
                am.playAudio(AudioManager.GAME, true);
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
        am.stopAudio(AudioManager.MOTHERSHIP_WARNING);
    }
    
    private void summonAlienWave(){
        if (level == 1) {
                summonAliens(36, -1008, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1062, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1116, Enemy.SMALL, 9, 60, 3, 40, 264);
            } else if (level == 2) {
                summonAliens(36, -1008, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1062, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1116, Enemy.SMALL, 9, 60, 3, 40, 264);
            } else if (level == 3) {
                summonAliens(36, -1008, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1062, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1116, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1170, Enemy.SMALL, 9, 60, 3, 40, 264);
            } else if (level == 4) {
                summonAliens(36, -1008, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1062, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1116, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1170, Enemy.MEDIUM, 7, 78, 3, 40, 264);
            } else if (level == 5) {
                summonAliens(36, -1008, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1062, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1116, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1170, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                stateLevel();
            } else if (level == 6) {
                summonAliens(36, -1008, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1062, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1116, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1170, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1224, Enemy.LARGE, 5, 114, 3, 40, 264);
            } else if (level == 7) {
                summonAliens(36, -1008, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1062, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1116, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1170, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1224, Enemy.LARGE, 5, 114, 3, 40, 264);
            } else if (level == 8) {
                summonAliens(36, -1008, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1062, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1116, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1170, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1224, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1278, Enemy.SMALL, 9, 60, 3, 40, 264);
            } else if (level == 9) {
                summonAliens(36, -1008, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1062, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1116, Enemy.SMALL, 9, 60, 3, 40, 264);
                summonAliens(36, -1170, Enemy.MEDIUM, 7, 78, 3, 40, 264);
                summonAliens(36, -1224, Enemy.LARGE, 5, 114, 3, 40, 264);
                summonAliens(36, -1278, Enemy.MEDIUM, 7, 78, 3, 40, 264);
            } else if (level == 10) {
                stopMusic();
                am.playAudio(AudioManager.MOTHERSHIP_WARNING, false);
                enemies.add(new Enemy(im.getImage(SpriteManager.MOTHERSHIP), new Point(76, -508), 4, new Velocity(0, 1), 400 + (difficulty * 120), Enemy.MOTHERSHIP, im, new EnemyMovementLimitProvider(-48, 36, 216, 0, 152), am));
            }
    }
    
    private void summonAliens(int x, int y, int type, int amount, int distance, int size, int marchYPoint, int attackYPoint) {
        if (type == Enemy.SMALL){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.YELLOW_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 13 + (difficulty * 4) + ((level - 1) * 4 / 10), type, im, new EnemyMovementLimitProvider(-1008, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else if (type == Enemy.MEDIUM){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.GREEN_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 22 + (difficulty * 6) + ((level - 1) * 6 / 10), type, im, new EnemyMovementLimitProvider(-1008, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else if (type == Enemy.LARGE){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.BLUE_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 36 + (difficulty * 8) + ((level - 1) * 8 / 10), type, im, new EnemyMovementLimitProvider(-1008, marchYPoint, attackYPoint, 0, 640), am));
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
        
        if (menuState == 0) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_24);
            graphics.drawString(String.format("Score:%010d%n", score), 3, 28);
            graphics.drawString(String.format("Level:%02d%n", (difficulty * 10) + level), 445, 28);
        }
        
        if (paused || menuState > 0 && menuState < 4 && inGame == true) {
            graphics.setColor(new Color(0, 0, 0, 124));
            graphics.fillRect(0, 0, 640, 640);
        }
        
        if (menuState == 4) {
            graphics.drawImage(im.getImage(SpriteManager.LOGO), 64, 8, 512, 512, this);
            graphics.setFont(spacefont_32);
            graphics.setColor(new Color(0, 0, 0, 63));
            graphics.drawString("...Kinda", 312, 424);
            graphics.setColor(new Color(255, 255, 0));
            graphics.drawString("...Kinda", 320, 400);
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_12);
            graphics.drawString("Kyle van Wiltenburg - 2015", 4, 608);
            if (textBoxTimer <= 60) {
                graphics.setFont(spacefont_24);
                graphics.drawString("- Insert Coin(s) -", 104, 500);
                graphics.setFont(spacefont_12);
                graphics.drawString("(Press SPACE)", 242, 530);
            }
        } else if (paused) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_32);
            graphics.drawString("PAUSED", 224, 120);
            graphics.setFont(spacefont_20);
            graphics.drawString(">            <", 179, 250 + selectedButton * 40);
            graphics.drawString("Resume Game", 210, 250);
            graphics.drawString("Quit Game", 230, 370);
            graphics.setColor(new Color(128, 128, 128));
            graphics.drawString("High Scores", 210, 290);
            graphics.drawString("How To Play", 210, 330);
        } else if (menuState == 3) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_32);
            graphics.drawString("GAME OVER", 176, 100);
            graphics.setFont(spacefont_20);
            graphics.drawString(String.format("Final Score: %010d%n", score), 90, 140);
            graphics.drawString(String.format("Died On Level: %02d%n", level), 150, 170);
            if (textBoxTimer <= 60) {
                graphics.drawString(String.format("Press SPACE to continue", level), 90, 400);
            }
        }
    }
}
