/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import images.ImageManager;
import images.ResourceTools;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author Kyle
 */
public class SpriteManager implements SpriteProviderIntf {
    
    public static String SHIP = "SHIP";
    public static String PROJECTILE = "PROJECTILE";
    public static String GREEN_TINT = "GREENT_INT";
    public static String BLUE_TINT = "BLUE_TINT";
    public static String SHIELD = "SHIELD";
    
    
    public static String RED_METER = "RED_METER";
    public static String RED_METER_FILL = "RED_METER_FILL";
    public static String BLUE_METER = "BLUE_METER";
    public static String BLUE_METER_FILL = "BLUE_METER_FILL";
    
    private ImageManager im;
    
    {
        
        HashMap<String, Image> imageMap = new HashMap<>();
        BufferedImage spriteSheet = (BufferedImage) ResourceTools.loadImageFromResource("spaceinvaders/spritesheet.png");
        imageMap.put(SHIP, spriteSheet.getSubimage(0, 112, 16, 16));
        imageMap.put(PROJECTILE, spriteSheet.getSubimage(0, 74, 1, 6));
        imageMap.put(GREEN_TINT, spriteSheet.getSubimage(40, 110, 18, 18));
        imageMap.put(BLUE_TINT, spriteSheet.getSubimage(58, 110, 18, 18));
        imageMap.put(SHIELD, spriteSheet.getSubimage(16, 104, 24, 24));
        imageMap.put(RED_METER, spriteSheet.getSubimage(0, 98, 4, 6));
        imageMap.put(RED_METER_FILL, spriteSheet.getSubimage(8, 98, 4, 6));
        imageMap.put(BLUE_METER, spriteSheet.getSubimage(12, 98, 4, 6));
        imageMap.put(BLUE_METER_FILL, spriteSheet.getSubimage(20, 98, 4, 6));
        
        im = new ImageManager(imageMap);
    }
    
    public BufferedImage getImage(String name){
        return (BufferedImage) im.getImage(name);
    }
    
}
