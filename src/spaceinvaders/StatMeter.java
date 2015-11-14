/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author Kyle
 */
public class StatMeter {
    
    public static int RED = 0;
    public static int BLUE = 1;
    public static int YELLOW = 2;
    public static int GREEN = 3;
    
    {
        meterTimer = 0;
        maxValue = 16;
        maxFlash = 0;
        value = 0;
    }
    
    private int meterTimer;
    private int maxValue;
    private int value;
    private int x;
    private int y;
    private int size;
    private int color;
    private int maxFlash;
    
    Image meter;
    private final SpriteProviderIntf imageProvider;
    
    public StatMeter(int color, int maxValue, int maxFlash, int x, int y, int size, SpriteProviderIntf imageProvider) {
        
        this.color = color;
        this.maxValue = maxValue;
        this.maxFlash = maxFlash;
        this.x = x;
        this.y = y;
        this.size = size;
        this.imageProvider = imageProvider;
                
    }
    
    public void draw(Graphics graphics) {
        
        if (color == 1) {
            for (int i = 0; i < maxValue; i++) {
                if (value > maxFlash || meterTimer < 2) {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER), x + (9 * i), y, 4 * size, 6 * size, null);
                } else {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER_FLASH), x + (9 * i), y, 4 * size, 6 * size, null);
                }
            }
            
            for (int i = 0; i < value; i++) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.BLUE_METER_FILL), x + (9 * i), y, 4 * size, 6 * size, null);
            }
        } else if (color == 2) {
            for (int i = 0; i < maxValue; i++) {
                if (value > maxFlash || meterTimer < 2) {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.YELLOW_METER), x + (9 * i), y, 4 * size, 6 * size, null);
                } else {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.YELLOW_METER_FLASH), x + (9 * i), y, 4 * size, 6 * size, null);
                }
            }
            for (int i = 0; i < value; i++) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.YELLOW_METER_FILL), x + (9 * i), y, 4 * size, 6 * size, null);
            }
        } else if (color == 3) {
            for (int i = 0; i < maxValue; i++) {
                if (value > maxFlash || meterTimer < 2) {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_METER), x + (9 * i), y, 4 * size, 6 * size, null);
                } else {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_METER_FLASH), x + (9 * i), y, 4 * size, 6 * size, null);
                }
            }
            
            for (int i = 0; i < value; i++) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.GREEN_METER_FILL), x + (9 * i), y, 4 * size, 6 * size, null);
            }
        } else {
            for (int i = 0; i < maxValue; i++) {
                if (value > maxFlash || meterTimer < 2) {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER), x + (9 * i), y, 4 * size, 6 * size, null);
                } else {
                    graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER_FLASH), x + (9 * i), y, 4 * size, 6 * size, null);
                }
            }
            
            for (int i = 0; i < value; i++) {
                graphics.drawImage(imageProvider.getImage(SpriteManager.RED_METER_FILL), x + (9 * i), y, 4 * size, 6 * size, null);
            }
        }
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public void meterTimeTaskHandler() {
        if (meterTimer < 4) {
            meterTimer++;
        } else {
            meterTimer = 0;
        }
        if (value > maxValue) {
            value = maxValue;
        }
    }
    
}
