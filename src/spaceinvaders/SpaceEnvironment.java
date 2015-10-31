/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

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
    private Ship ship;

    private int direction;
    
    private int yChange;
    private Object event;

    public SpaceEnvironment() {
        
        this.setBackground(Color.BLACK);
        
        ship = new Ship(32, 496, 64);
        
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
        
        yChange = (1) + 2;
        
        stars.stream().forEach((theStar) -> {
            theStar.setY(yChange);
            
            if (theStar.getY() >= 640) {
                
                theStar.resetStar();
            }
            
        });
        
        }
        
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
        
        if (e.getKeyCode() == 39) {
            ship.moveX(64);
            if (ship.getX() >= 544) {
                ship.setX(544);
            }
        }
        
        if (e.getKeyCode() == 37) {
            ship.moveX(-64);
            if (ship.getX() <= 32) {
                ship.setX(32);
            }
        }
        
    }

    @Override
    public void keyReleasedHandler(KeyEvent e) {
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
