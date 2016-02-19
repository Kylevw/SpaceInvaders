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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private final ArrayList<Star> stars;
    private final ArrayList<Projectile> projectiles;
    private final ArrayList<Enemy> enemies;
    private final ArrayList<PowerOrb> powerOrbs;
    private final ArrayList<TextBox> textBoxs;
    Ship ship;
    
    private int direction;
    private int yStarChange;
    private boolean spacebarDebug;
    private boolean paused;
    private int selectedButton;
    private int cheatProgress;
    
    private int alienTimer;
    private int timerTick;
    private int textBoxTimer;
    private int displacement;
    
    private boolean leftDebug;
    private boolean rightDebug;
    
    private int menuState;
    private int musicTimer;
    private int level;
    private int levelUpTimer;
    private int difficulty;
    private int score;
    private int highScore;
    private boolean newHighScore;
    
    StatMeter healthMeter;
    StatMeter energyMeter;
    StatMeter powerMeter;
    
    private boolean endingGame;
    
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
        
        highScore = -1;
        
        loadImages();
        loadAudio();
        
        am.playAudio(AudioManager.MENU, true);
        
        this.setBackground(Color.BLACK);
        
        textBoxs = new ArrayList<>();
        stars = new ArrayList<>();
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        powerOrbs = new ArrayList<>();
        int starCount = 164; // number of stars on screen
        for (int i = 0; i < starCount; i++) {
              stars.add(new Star(random(640), random(640), random(5)));
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
        
        highScore = getHighScore();
        
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
                checkScore();
                textBoxs.removeAll(textBoxs);
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
        
        if (score > 99999999) {
            score = 99999999;
        }
        
        if (selectedButton < 0) {
            selectedButton = 2;
        } else if (selectedButton > 2) {
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
                
                if (alienTimer >= 20 - ((level - 1 + (difficulty * 10)) / 3) - difficulty) {
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
                if (levelUpTimer >= 80 && !endingGame) {
                    levelUpTimer = 0;
                    int addScore = (level + (difficulty * 10)) * 10000;
                    score += addScore;
                    String displayScore = "" + addScore;
                    if (ship != null && addScore > 0) {
                        textBoxs.add(new TextBox(ship.getX() + (ship.getWidth() / 2) - (displayScore.length() * 10), ship.getY() + 20, 80, true, false, spacefont_20, "" + addScore));
                        am.playAudio(AudioManager.POWER_UP, false);
                    }
                    level++;
                    if (level == 10) {
                        musicTimer = 1;
                    }
                    if (level > 10) {
                        am.playAudio(AudioManager.LEVEL_UP, false);
                        difficulty++;
                        if (ship != null && difficulty < 3) textBoxs.add(new TextBox(ship.getX() + (ship.getWidth() / 2) - 96, ship.getY() - 4, 80, true, false, spacefont_24, "SPEED UP"));
                        level = 1;
                    }
                    displacement = 0;
                    alienTimer = 0;
                    timerTick = 0;
                    if (difficulty < 3) {
                        summonAlienWave();
                        stateLevel();
                    } else {
                        endGame();
                    }
                } else if (levelUpTimer >= 240) {
                    ship = null;
                    menuState = 3;
                    checkScore();
                    textBoxs.removeAll(textBoxs);
                    ArrayList<Enemy> aboveScreen = new ArrayList<>();
                    getEnemies().stream().filter((enemy) -> (enemy.getY() <= -enemy.getWidth() * enemy.getSize())).forEach((enemy) -> {
                        aboveScreen.add(enemy);
                    });
                    enemies.removeAll(aboveScreen);
                    stopMusic();
                    am.playAudio(AudioManager.MENU, true);
                    difficulty = -1;
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
    
    private void endGame() {
        textBoxs.add(new TextBox(160, 320, 320, false, false, spacefont_32, "YOU WIN!!!"));
        endingGame = true;
        ship.flyUp();
    }
    
    public int getHighScore() {
        FileReader readFile = null;
        BufferedReader reader = null;
        try {
            readFile = new FileReader("highscore.dat");
            reader = new BufferedReader(readFile);
            return Integer.valueOf(reader.readLine());
        } catch (Exception e){
            return 0;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SpaceEnvironment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    private void checkScore() {
        if (score > highScore) {
            newHighScore = true;
            File scoreFile = new File("highscore.dat");
            if (!scoreFile.exists()) {
                try {
                    scoreFile.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(SpaceEnvironment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            FileWriter writeFile = null;
            BufferedWriter writer = null;
            try {
                writeFile = new FileWriter(scoreFile);
                writer = new BufferedWriter(writeFile);
                writer.write("" + score);
            } catch (IOException ex) {
                Logger.getLogger(SpaceEnvironment.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SpaceEnvironment.class.getName()).log(Level.SEVERE, null, ex);
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
                textBoxs.add(new TextBox(ship.getX() + (ship.getWidth() / 2) - 20, ship.getY() + 20, 40, true, false, spacefont_20, "25"));
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
            enemy.damageTimer();
        if (enemy.getY() >= 640) {
                    noHealth.add(enemy);
                } else if (enemy.getHealth() <= 0) {
                    if (enemy.getType() != Enemy.MOTHERSHIP) {
                        score += 100 + (enemy.getType() * 50) + ((enemy.getType() + 1) * 5 * (level + (difficulty * 10) - (enemy.getType() + 1)));
                        String displayScore = "" + (100 + (enemy.getType() * 50) + ((enemy.getType() + 1) * 5 * (level + (difficulty * 10)- (enemy.getType() + 1))));
                        textBoxs.add(new TextBox(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - (displayScore.length() * 10), enemy.getY() + (enemy.getHeight() * enemy.getSize()) - 8, 40, true, false, spacefont_20, displayScore));
                        noHealth.add(enemy);
                        am.playAudio(AudioManager.KILL_ALIEN, false);
                        int powerUpRandomizer = random(12);
                        if (powerUpRandomizer == 0) {
                            if (enemy.getType() == Enemy.LARGE) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_RAPID_FIRE), new Point(enemy.getX() + 18, enemy.getY()), 3, new Velocity(0, 8), PowerOrb.RAPID_FIRE, im));
                            } else if (enemy.getType() == Enemy.SMALL) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SHIELD), new Point(enemy.getX() + 6, enemy.getY()), 3, new Velocity(0, 8), PowerOrb.SHIELD, im));
                            } else if (enemy.getType() == Enemy.MEDIUM) {
                                powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SPEED), new Point(enemy.getX() + 12, enemy.getY()), 3, new Velocity(0, 8), PowerOrb.SPEED, im));
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
                            enemy.setVelocity(2, 0);
                        } else {
                            enemy.setVelocity(0, 0);
                        }
                    }
                    enemy.enemyTimeTaskHandler();
                    if (enemy.getDeathTimer() >= 200) {
                        noHealth.add(enemy);
                        score += 5000 * (difficulty + 1);
                        String displayScore = "" + (5000 * (difficulty + 1));
                        textBoxs.add(new TextBox(enemy.getX() + (enemy.getWidth() * enemy.getSize() / 2) - (displayScore.length() * 10), enemy.getY() + (enemy.getHeight() * enemy.getSize() / 2), 40, true, false, spacefont_20, displayScore));
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
                            powerOrbs.add(new PowerOrb(im.getImage(SpriteManager.POWERUP_SHIELD), new Point(enemy.getX() + ((enemy.getWidth() / 2 * enemy.getSize()) - 18), enemy.getY() + ((enemy.getHeight() / 2 * enemy.getSize()) - 12)), 3, new Velocity(0, 8), PowerOrb.SHIELD, im));
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
            }).filter((enemy) -> (alienTimer >= 20 - ((level - 1 + (difficulty * 10)) / 3) - difficulty && enemy.getType() != Enemy.MOTHERSHIP && !enemy.targetingShip())).forEach((enemy) -> {
                if (ship != null && random(6) == 0) {
                    if (enemy.getType() == Enemy.MEDIUM) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_SMALL_GREEN), new Point(enemy.getX() + (7 * enemy.getSize()), enemy.getY() + (5 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + difficulty), 1, false, im));
                    } else if (enemy.getType() == Enemy.LARGE) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_MEDIUM_BLUE), new Point(enemy.getX() + (7 * enemy.getSize()), enemy.getY() + (3 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + difficulty), 2, false, im));
                    } else if (enemy.getType() == Enemy.SMALL) {
                        projectiles.add(new Projectile(im.getImage(SpriteManager.PROJECTILE_SMALL_YELLOW), new Point(enemy.getX() + (5 * enemy.getSize()), enemy.getY() + (5 * enemy.getSize())), (enemy.getSize()), TrigonometryCalculator.calculateVelocity(enemy.getCenterOfMass(), ship.getCenterOfMass(), 10.0 + difficulty), 1, false, im));
                    }
                }
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
        String stateLevel = "LEVEL " + (level + (10 * difficulty));
        if (level % 10 == 0) textBoxs.add(new TextBox(320 - (stateLevel.length() * 16), 320, 320, false, true, spacefont_32, stateLevel));
        else textBoxs.add(new TextBox(320 - (stateLevel.length() * 16), 320, 120, false, false, spacefont_32, stateLevel));
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (menuState == 0) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                paused = !paused;
            } else if (!paused && level == 0) {
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    cheatProgress = 1;
                } else if (e.getKeyCode() == KeyEvent.VK_O) {
                    if (cheatProgress == 1) {
                        cheatProgress++;
                    } else {
                        cheatProgress = 0;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_T) {
                    if (cheatProgress == 2) {
                        cheatProgress++;
                    } else {
                        cheatProgress = 0;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_H) {
                    if (cheatProgress == 3 || cheatProgress == 7) {
                        cheatProgress++;
                    } else {
                        cheatProgress = 0;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    if (cheatProgress == 4) {
                        cheatProgress++;
                    } else {
                        cheatProgress = 0;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    if (cheatProgress == 5) {
                        cheatProgress++;
                    } else {
                        cheatProgress = 0;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    if (cheatProgress == 6) {
                        cheatProgress++;
                    } else {
                        cheatProgress = 0;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_I) {
                    if (cheatProgress == 8) {
                        cheatProgress++;
                    } else {
                        cheatProgress = 0;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (cheatProgress == 9) {
                        level = 10;
                        am.playAudio(AudioManager.POWER_UP, false);
                    }
                    cheatProgress = 0;
                }
            }
        }
        
        if (ship != null && menuState == 0 && !paused && ship.getY() == ship.getMinY()) {
            
            if (e.getKeyCode() == KeyEvent.VK_SPACE && !spacebarDebug) {
                if (ship != null) {
                    fire();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_D && !rightDebug || e.getKeyCode() == KeyEvent.VK_RIGHT && !rightDebug) {
                // right movement for ship
                if (ship != null) {
                    if (ship.getDirection() == Direction.LEFT) ship.setDirection(Direction.BOTH);
                    else ship.setDirection(Direction.RIGHT);
                }
                rightDebug = true;
                
            } else if (e.getKeyCode() == KeyEvent.VK_A && !leftDebug || e.getKeyCode() == KeyEvent.VK_LEFT && !leftDebug) {
                if (ship != null) {
                    if (ship.getDirection() == Direction.RIGHT) ship.setDirection(Direction.BOTH);
                    else ship.setDirection(Direction.LEFT);
                }
                leftDebug = true;
//            } else if (e.getKeyCode() == KeyEvent.VK_Q) {
//                getEnemies().stream().forEach((enemy) -> {
//                    enemy.Damage(10000000);
//                });
//            } else if (e.getKeyCode() == KeyEvent.VK_1) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 0;
//            } else if (e.getKeyCode() == KeyEvent.VK_2) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 1;
//            } else if (e.getKeyCode() == KeyEvent.VK_3) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 2;
//            } else if (e.getKeyCode() == KeyEvent.VK_4) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 3;
//            } else if (e.getKeyCode() == KeyEvent.VK_5) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 4;
//            } else if (e.getKeyCode() == KeyEvent.VK_6) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 5;
//            } else if (e.getKeyCode() == KeyEvent.VK_7) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 6;
//            } else if (e.getKeyCode() == KeyEvent.VK_8) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 7;
//            } else if (e.getKeyCode() == KeyEvent.VK_9) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 8;
//            } else if (e.getKeyCode() == KeyEvent.VK_0) {
//                textBoxs.removeAll(textBoxs);
//                levelUpTimer = 80;
//                enemies.removeAll(enemies);
//                level = 9;
//            } else if (e.getKeyCode() == KeyEvent.VK_P) {
//                // speeds up background
//                difficulty += 1;
//            } else if (e.getKeyCode() == KeyEvent.VK_O && difficulty > 0) {
//                // slows up background
//                difficulty -= 1;
            }
        } else if (menuState == 0 && paused) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (selectedButton == 0) {
                    paused = false;
                } else if (selectedButton == 1) {
                    menuState = 1;
                } else if (selectedButton == 2) {
                    projectiles.removeAll(projectiles);
                    stopMusic();
                    ship = null;
                    endingGame = false;
                    checkScore();
                    enemies.removeAll(enemies);
                    difficulty = -1;
                    menuState = 4;
                    score = 0;
                    textBoxs.removeAll(textBoxs);
                    musicTimer = 0;
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
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                newHighScore = false;
                endingGame = false;
                enemies.removeAll(enemies);
                score = 0;
                musicTimer = 0;
                menuState = 4;
            }
        } else if (menuState == 4) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                level = 0;
                difficulty = 0;
                levelUpTimer = -140;
                menuState = 0;
                ship = new Ship(im.getImage(SpriteManager.SHIP), new Point(296, 640), 3, new Velocity(0, 0), 9, new ShipMovementLimitProvider(24, 568, 505, 640), im, am);
                textBoxs.add(new TextBox(32, 280, 220, false, false, spacefont_24, "LEFT/RIGHT ARROWS: Move"));
                textBoxs.add(new TextBox(164, 310, 220, false, false, spacefont_24, "SPACE: Shoot"));
                textBoxs.add(new TextBox(128, 340, 220, false, false, spacefont_24, "ESC: Pause Game"));
                stopMusic();
                am.playAudio(AudioManager.GAME, true);
            }
        } else if (menuState == 1) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                menuState = 0;
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
                enemies.add(new Enemy(im.getImage(SpriteManager.MOTHERSHIP), new Point(76, -508), 4, new Velocity(0, 1), 400 + (100 * difficulty), Enemy.MOTHERSHIP, im, new EnemyMovementLimitProvider(-48, 36, 216, 0, 152), am));
            }
    }
    
    private void summonAliens(int x, int y, int type, int amount, int distance, int size, int marchYPoint, int attackYPoint) {
        if (type == Enemy.SMALL){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.YELLOW_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 13, type, im, new EnemyMovementLimitProvider(-1008, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else if (type == Enemy.MEDIUM){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.GREEN_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 22, type, im, new EnemyMovementLimitProvider(-1008, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else if (type == Enemy.LARGE){
            for (int i = 0; i < amount; i++) {
                enemies.add(new Enemy(im.getImage(SpriteManager.BLUE_ALIEN), new Point(x + (distance * i) + displacement, y), size, new Velocity(0, size * 2), 36, type, im, new EnemyMovementLimitProvider(-1008, marchYPoint, attackYPoint, 0, 640), am));
            }
        } else {
            System.out.println("ERROR summoning alien wave: Alien type not valid");
        }
    }
    
    @Override
    public void keyReleasedHandler(KeyEvent e) {
        // when releasing the right key, stops ship from moving right
        if (ship != null) {
            if (e.getKeyCode() == KeyEvent.VK_D && rightDebug || e.getKeyCode() == KeyEvent.VK_RIGHT && rightDebug) {
                    if (ship.getDirection() == Direction.BOTH) ship.setDirection(Direction.LEFT);
                    else ship.setDirection(Direction.NONE);
                    rightDebug = false;
                    // when releasing the left key, stops ship from moving right
            } else if (e.getKeyCode() == KeyEvent.VK_A && leftDebug || e.getKeyCode() == KeyEvent.VK_LEFT && leftDebug) {
                    if (ship.getDirection() == Direction.BOTH) ship.setDirection(Direction.RIGHT);
                    else ship.setDirection(Direction.NONE);
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
    public void paintEnvironment(Graphics g) {
        
        AffineTransform atrans;

        Graphics2D graphics = (Graphics2D) g;
        atrans = AffineTransform.getScaleInstance((double) SpaceInvaders.getWindowSize().width / 640, (double) SpaceInvaders.getWindowSize().height / 640);
        if (atrans != null)
          graphics.setTransform(atrans);
        
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
        
        if (ship != null) {
            ship.draw(graphics);
        }
        
        getTextBoxs().stream().forEach((theTextBox) -> {
            theTextBox.draw(graphics);
        });
        
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
            graphics.drawString(String.format("Score:%08d%n", score), 3, 28);
            String currentLevel = String.format("Level:%02d%n", (difficulty * 10) + level);
            if (difficulty >= 3) graphics.drawString("Level:30", 640 - ((currentLevel.length() - 1) * 24), 28);
            else graphics.drawString(currentLevel, 640 - ((currentLevel.length() - 1) * 24), 28);
        }
        
        if (paused || menuState > 0) {
            if (menuState == 1) {
                graphics.setColor(new Color(0, 0, 0, 200));
            } else {
                graphics.setColor(new Color(0, 0, 0, 124));
            }
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
            graphics.drawString("Kyle van Wiltenburg - 2016", 4, 624);
            graphics.setFont(spacefont_24);
            graphics.drawString(String.format("HIGH:%08d%n", highScore), 3, 28);
            if (textBoxTimer <= 60) {
                graphics.setFont(spacefont_24);
                graphics.drawString("- Insert Coin(s) -", 104, 500);
                graphics.setFont(spacefont_12);
                graphics.drawString("(Press SPACE)", 242, 530);
            }
        } else if (menuState == 1) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_32);
            graphics.drawString("HOW TO PLAY", 144, 72);
            graphics.setFont(spacefont_12);
            graphics.drawString("Aliens are descending towards your ship! Fire at", 10, 120);
            graphics.drawString("them by pressing SPACE, and dodge their attacks by", 10, 135);
            graphics.drawString("moving left + right. Your health is", 10, 150);
            graphics.drawString("displayed by the red metre, and your", 10, 165);
            graphics.drawString("ammunition by the blue metre. If your", 10, 180);
            graphics.drawString("health depletes to 0, you lose the game. Also, your", 10, 195);
            graphics.drawString("ammunition has a cooldown before it reloads, meaning", 10, 210);
            graphics.drawString("you must wait before hitting the spacebar again for", 10, 225);
            graphics.drawString("it to recharge.", 10, 240);
            graphics.drawString("Upon the death of an alien, you will be rewarded", 10, 270);
            graphics.drawString("points, along with the possibilty of a", 10, 285);
            graphics.drawString("power-up. A power-up can be collected by", 10, 300);
            graphics.drawString("running into the pulsing orb with your", 10, 315);
            graphics.drawString("ship. Upon picking up a power-up, a timer will", 10, 330);
            graphics.drawString("appear on the bottom-left of the screen, indicating", 10, 345);
            graphics.drawString("how long the power-up has left before depleting.", 10, 360);
            graphics.drawString("You will complete the level by clearing out each", 10, 390);
            graphics.drawString("wave of aliens in the level, each getting faster and", 10, 405);
            graphics.drawString("stronger as you progress. Can you survive all 30", 10, 420);
            graphics.drawString("waves of aliens and their mothership?", 10, 435);
            graphics.drawString("Press ESCAPE to return to menu", 140, 560);
            
            graphics.drawImage(im.getImage(SpriteManager.YELLOW_ALIEN), 136, 470, 48, 48, this);
            graphics.drawImage(im.getImage(SpriteManager.GREEN_ALIEN), 290, 470, 60, 48, this);
            graphics.drawImage(im.getImage(SpriteManager.BLUE_ALIEN), 444, 470, 72, 48, this);
            graphics.drawImage(im.getImage(SpriteManager.POWERUP_SHIELD_LARGE), 504, 278, 36, 36, this);
            graphics.drawImage(im.getImage(SpriteManager.POWERUP_SPEED_LARGE), 546, 278, 36, 36, this);
            graphics.drawImage(im.getImage(SpriteManager.POWERUP_RAPID_FIRE_LARGE), 588, 278, 36, 36, this);
            for (int i = 0; i < 16; i++) {
                graphics.drawImage(im.getImage(SpriteManager.RED_METER), 464 + (9 * i), 140, 12, 18, this);
                graphics.drawImage(im.getImage(SpriteManager.RED_METER_FILL), 464 + (9 * i), 140, 12, 18, this);
                graphics.drawImage(im.getImage(SpriteManager.BLUE_METER), 464 + (9 * i), 161, 12, 18, this);
                graphics.drawImage(im.getImage(SpriteManager.BLUE_METER_FILL), 464 + (9 * i), 161, 12, 18, this);
            }
        } else if (paused) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_32);
            graphics.drawString("PAUSED", 224, 120);
            graphics.setFont(spacefont_20);
            graphics.drawString(">            <", 179, 270 + selectedButton * 40);
            graphics.drawString("Resume Game", 210, 270);
            graphics.drawString("How To Play", 210, 310);
            graphics.drawString("Quit Game", 230, 350);
            graphics.drawString(String.format("Highscore:%08d%n", highScore), 140, 160);
        } else if (menuState == 3) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(spacefont_32);
            if (endingGame) graphics.drawString("CONGRATULATIONS!", 64, 100);
            else graphics.drawString("GAME OVER", 176, 100);
            if (newHighScore) {
                graphics.drawString("NEW HIGHSCORE!", 96, 306);
            }
            graphics.setFont(spacefont_20);
            graphics.drawString(String.format("Final Score: %08d%n", score), 110, 140);
            if (endingGame) graphics.drawString(String.format("Survived All Levels!", level), 120, 170);
            else graphics.drawString(String.format("Died On Level: %02d%n", level), 150, 170);
            if (textBoxTimer <= 60) {
                graphics.drawString(String.format("Press ENTER to continue", level), 90, 450);
            }
        }
    }
}
