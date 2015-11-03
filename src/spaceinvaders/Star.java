/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import static environment.Utility.random;
import java.awt.Color;
import static java.awt.Color.blue;
import java.awt.Graphics;

/**
 *
 * @author Kyle
 */
public class Star {
    
    public Star(int x, int y, int size) {
        
        this.x = x;
        this.y = y;
        this.size = size;
        
    }
    
    public void draw(Graphics graphics) {
        
        graphics.setColor(Color.WHITE);
        graphics.fillRect(x, y, size + 1, (size + 1) * 5);
        
    }
    
    private int x;
    private int y;
    private int size;
    
    void setY(int yChange) {
        
        this.y = y + ((yChange * (size + 3)));
        
    }
    
    void resetStar() {
        
        this.y = this.y - 640 - random(640);
        this.x = random(640);
        this.size = random(3);
        
    }
    
    int getY() {
        
        return this.y;
        
    }
  
}
