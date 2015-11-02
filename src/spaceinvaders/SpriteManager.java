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
    
    private ImageManager im;
    
    {
        
        HashMap<String, Image> imageMap = new HashMap<>();
        BufferedImage spriteSheet = (BufferedImage) ResourceTools.loadImageFromResource("spaceinvaders/spritesheet.png");
        imageMap.put(SHIP, spriteSheet.getSubimage(0, 112, 16, 16));
        imageMap.put(PROJECTILE, spriteSheet.getSubimage(0, 74, 1, 6));

        im = new ImageManager(imageMap);
    }
    
    public BufferedImage getImage(String name){
        return (BufferedImage) im.getImage(name);
    }
    
}
