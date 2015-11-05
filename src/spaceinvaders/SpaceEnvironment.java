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
import environment.Environment;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author Kyle
 */
class SpaceEnvironment extends Environment {
    
    private ArrayList<Star> stars;
    Ship ship;

    private int direction;
    private int yStarChange;
    private int shipVelocity;
    
    SoundManager sm;
    SpriteManager im;
    
    public SpaceEnvironment() {
        
        loadImages();
        
        this.setBackground(Color.BLACK);
        
        ship = new Ship(292, 640, 48, new ShipMovementLimitProvider(24, 568, 504, 640), im);
        
        stars = new ArrayList<>();
        int starCount = 64;
        
        for (int i = 0; i < starCount; i++) {
            stars.add(new Star(random(640), random(640), random(3)));
        }
        
        ArrayList<Track> tracks = new ArrayList<>();
        tracks.add(new Track("GAME", Source.RESOURCE, "/spaceinvaders/game_new.wav"));
        
        sm = new SoundManager(new Playlist(tracks));
        
        sm.play("GAME", Integer.MAX_VALUE);
        
    }
    
    public void loadImages() {
        im = new SpriteManager();
    }
    
    
    public int random(int value) {
        return (int) (Math.random() * value);
    }

    @Override
    public void initializeEnvironment() {
        
    }

    @Override
    public void timerTaskHandler() {
        
        if (stars != null) {
            yStarChange = (1) + 2;
            
            stars.stream().forEach((theStar) -> {
            theStar.setY(yStarChange);
            
            if (theStar.getY() >= 640) {
                theStar.resetStar();
            }
            
        });
            
        }
        
        if (ship != null) {
            
            if (shipVelocity >= ship.getSpeed()) {
                shipVelocity = ship.getSpeed();
            } else if (shipVelocity <= -ship.getSpeed()) {
                shipVelocity = -ship.getSpeed();
            }
            
            ship.moveX(shipVelocity);
            ship.shipTimerTaskHandler();
            
        }
        
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            shipVelocity = ship.getSpeed();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            shipVelocity = -ship.getSpeed();
        } else if (e.getKeyCode() == KeyEvent.VK_F) {
            ship.toggleSpeed();
        } else if (e.getKeyCode() == KeyEvent.VK_G) {
            ship.toggleRapidFire();
        } else if (e.getKeyCode() == KeyEvent.VK_H) {
            ship.toggleShield();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE && ship.getY() <= 504) {
            ship.fire();
        }
                

    }
    
    @Override
    public void keyReleasedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                shipVelocity = shipVelocity - ship.getSpeed();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                shipVelocity = shipVelocity + ship.getSpeed();
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
        
    }
}
