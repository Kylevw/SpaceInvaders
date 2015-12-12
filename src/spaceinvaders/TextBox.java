/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import java.awt.Font;
import java.awt.Graphics;

/**
 *
 * @author Kyle
 */
public class TextBox {
    
    int x;
    int y;
    int time;
    int maxTime;
    boolean rise;
    Font font;
    String text;
    
    public TextBox(int x, int y, int maxTime, boolean rise, Font font, String text){
        this.x = x;
        this.y = y;
        this.maxTime = maxTime;
        this.rise = rise;
        this.font = font;
        this.text = text;
    }
    
    public void draw(Graphics graphics){
        graphics.setFont(font);
        graphics.drawString(text, x, y);
    }
    
    public void textBoxTimeTaskHandler(){
        if (rise) {
            y--;
        }
        time++;
    }
    
    public int getTime() {
        return time;
    }
    
    public int getMaxTime() {
        return maxTime;
    }
}
