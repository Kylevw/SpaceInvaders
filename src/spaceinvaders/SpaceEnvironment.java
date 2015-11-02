/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import environment.Environment;
import images.ResourceTools;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
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
    
    private int shipSpeed;
    private int shipVelocity;

    public SpaceEnvironment() {
        
        shipSpeed = 12;
        
        this.setBackground(Color.BLACK);
        
        ship = new Ship(292, 504, 64);
        
        stars = new ArrayList<>();
        int starCount = 50;
        
        for (int i = 0; i < starCount; i++) {
            stars.add(new Star(random(640), random(640), random(3)));
        }        
        
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
        
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == 39) {
                shipVelocity = shipSpeed;
        }
        
        if (e.getKeyCode() == 37) {
                shipVelocity = -shipSpeed;
        }
        
        if (e.getKeyCode() == 70) {
                ship.toggleSpeed();
        }
        
        if (e.getKeyCode() == 71) {
            System.out.println("Bleh");
        }
        
    }
    
    @Override
    public void keyReleasedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == 39) {
                shipVelocity = shipVelocity - shipSpeed;
        }
        
        if (e.getKeyCode() == 37) {
                shipVelocity = shipVelocity + shipSpeed;
        }
        
        if (shipVelocity >= shipSpeed) {
            shipVelocity = shipSpeed;
        }
        
        if (shipVelocity <= -shipSpeed) {
            shipVelocity = -shipSpeed;
        }
        
    }

    @Override
    public void environmentMouseClicked(MouseEvent e) {
    }

    @Override
    public void paintEnvironment(Graphics graphics) {
        
        ship.moveX(shipVelocity);
        
        stars.stream().forEach((theStar) -> {
            theStar.draw(graphics);
        });
        
        if (ship != null) {
            
            ship.draw(graphics);
            
        }
        
    }
    
}
