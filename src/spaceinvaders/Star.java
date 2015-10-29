/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import java.awt.Color;
import static java.awt.Color.blue;
import java.awt.Graphics;

/**
 *
 * @author Kyle
 */
public class Star {
    
    {
        size = 3;
        x = 10;
        y = 0;

    }
    
    public Star(int x, int y, int size, int speed) {
        
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        
    }
    
    public void draw(Graphics graphics) {
        
        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(x, y, size + 2, (size + 2) * 5, size, size * 2);
        
    }
    
    int x;
    int y;
    int size;
    int speed;
  
}
