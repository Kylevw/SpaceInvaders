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
    private boolean isBlue;
    private int maxFlash;
    
    Image meter;
    private final SpriteProviderIntf imageProvider;
    
    public StatMeter(boolean isBlue, int maxValue, int maxFlash, int x, int y, int size, SpriteProviderIntf imageProvider) {
        
        this.isBlue = isBlue;
        this.maxValue = maxValue;
        this.maxFlash = maxFlash;
        this.x = x;
        this.y = y;
        this.size = size;
        this.imageProvider = imageProvider;
                
    }
    
    public void draw(Graphics graphics) {
        
        if (isBlue == true) {
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
    }
    
}
