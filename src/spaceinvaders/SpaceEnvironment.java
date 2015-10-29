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
    
    ArrayList<Star> stars;

    public SpaceEnvironment() {
      
        stars = new ArrayList<>();
        int starCount = 40;
        
        
        for (int i = 0; i < starCount; i++) {
            stars.add(new Star(random(900), random(580), random(3), random(random(8))));
        }        
        
        this.setBackground(Color.BLACK);
        
    }
    
    public int random(int value) {
        
        return (int) (Math.random() * value);
        
    }

    @Override
    public void initializeEnvironment() {
    }

    @Override
    public void timerTaskHandler() {
    }

    @Override
    public void keyPressedHandler(KeyEvent e) {
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
        
    }
    
}
