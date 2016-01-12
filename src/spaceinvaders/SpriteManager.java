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
import java.util.ArrayList;
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
    
    public static String POWERUP_RAPID_FIRE = "POWERUP_RAPID_FIRE";
    public static String POWERUP_RAPID_FIRE_SMALL = "POWERUP_RAPID_FIRE_SMALL";
    public static String POWERUP_RAPID_FIRE_LARGE = "POWERUP_RAPID_FIRE_LARGE";
    public static String POWERUP_SPEED = "POWERUP_SPEED";
    public static String POWERUP_SPEED_SMALL = "POWERUP_SPEED_SMALL";
    public static String POWERUP_SPEED_LARGE = "POWERUP_SPEED_LARGE";
    public static String POWERUP_SHIELD = "POWERUP_SHIELD";
    public static String POWERUP_SHIELD_SMALL = "POWERUP_SHIELD_SMALL";
    public static String POWERUP_SHIELD_LARGE = "POWERUP_SHIELD_LARGE";
    
    public static String RED_METER = "RED_METER";
    public static String RED_METER_FILL = "RED_METER_FILL";
    public static String RED_METER_FLASH = "RED_METER_FLASH";
    public static String BLUE_METER = "BLUE_METER";
    public static String BLUE_METER_FILL = "BLUE_METER_FILL";
    public static String BLUE_METER_FLASH = "BLUE_METER_FLASH";
    public static String YELLOW_METER = "YELLOW_METER";
    public static String YELLOW_METER_FILL = "YELLOW_METER_FILL";
    public static String YELLOW_METER_FLASH = "YELLOW_METER_FLASH";
    public static String GREEN_METER = "GREEN_METER";
    public static String GREEN_METER_FILL = "GREEN_METER_FILL";
    public static String GREEN_METER_FLASH = "GREEN_METER_FLASH";
    
    public static String YELLOW_ALIEN = "YELLOW_ALIEN";
    public static String GREEN_ALIEN = "GREEN_ALIEN";
    public static String BLUE_ALIEN = "BLUE_ALIEN";
    public static String YELLOW_ALIEN_MOVE = "YELLOW_ALIEN_MOVE";
    public static String GREEN_ALIEN_MOVE = "GREEN_ALIEN_MOVE";
    public static String BLUE_ALIEN_MOVE = "BLUE_ALIEN_MOVE";
    public static String PROJECTILE_SMALL_YELLOW = "PROJECTILE_SMALL_YELLOW";
    public static String PROJECTILE_LARGE_YELLOW = "PROJECTILE_LARGE_YELLOW";
    public static String PROJECTILE_SMALL_GREEN = "PROJECTILE_SMALL_GREEN";
    public static String PROJECTILE_MEDIUM_GREEN = "PROJECTILE_MEDIUM_GREEN";
    public static String PROJECTILE_MEDIUM_BLUE = "PROJECTILE_MEDIUM_BLUE";
    public static String PROJECTILE_LARGE_BLUE = "PROJECTILE_LARGE_BLUE";
    public static String PROJECTILE_SMALL_WHITE = "PROJECTILE_SMALL_WHITE";
    public static String PROJECTILE_MEDIUM_WHITE = "PROJECTILE_MEDIUM_WHITE";
    public static String PROJECTILE_LARGE_WHITE = "PROJECTILE_LARGE_WHITE";
    
    public static String LOGO = "LOGO";
    public static String MOTHERSHIP = "MOTHERSHIP";
    public static String MOTHERSHIP_BEAM = "MOTHERSHIP_BEAM";

    private ImageManager im;
    
    private ArrayList<String> images;
    
    {
        
        HashMap<String, Image> imageMap = new HashMap<>();
        BufferedImage spriteSheet = (BufferedImage) ResourceTools.loadImageFromResource("spaceinvaders/spritesheet.png");
        BufferedImage motherShip = (BufferedImage) ResourceTools.loadImageFromResource("spaceinvaders/mothership.png");
        BufferedImage logo = (BufferedImage) ResourceTools.loadImageFromResource("spaceinvaders/logo.png");
        imageMap.put(LOGO, logo.getSubimage(0, 0, 128, 128));
        imageMap.put(SHIP, spriteSheet.getSubimage(0, 112, 16, 16));
        imageMap.put(PROJECTILE, spriteSheet.getSubimage(0, 58, 1, 6));
        imageMap.put(PROJECTILE_SMALL_YELLOW, spriteSheet.getSubimage(6, 34, 6, 6));
        imageMap.put(PROJECTILE_LARGE_YELLOW, spriteSheet.getSubimage(10, 16, 10, 10));
        imageMap.put(PROJECTILE_SMALL_GREEN, spriteSheet.getSubimage(12, 34, 6, 6));
        imageMap.put(PROJECTILE_MEDIUM_GREEN, spriteSheet.getSubimage(16, 26, 8, 8));
        imageMap.put(PROJECTILE_MEDIUM_BLUE, spriteSheet.getSubimage(24, 26, 8, 8));
        imageMap.put(PROJECTILE_LARGE_BLUE, spriteSheet.getSubimage(30, 16, 10, 10));
        imageMap.put(PROJECTILE_SMALL_WHITE, spriteSheet.getSubimage(0, 34, 6, 6));
        imageMap.put(PROJECTILE_MEDIUM_WHITE, spriteSheet.getSubimage(0, 26, 8, 8));
        imageMap.put(PROJECTILE_LARGE_WHITE, spriteSheet.getSubimage(0, 16, 10, 10));
        
        imageMap.put(POWERUP_RAPID_FIRE, spriteSheet.getSubimage(24, 74, 12, 12));
        imageMap.put(POWERUP_RAPID_FIRE_SMALL, spriteSheet.getSubimage(24, 62, 12, 12));
        imageMap.put(POWERUP_RAPID_FIRE_LARGE, spriteSheet.getSubimage(24, 86, 12, 12));
        imageMap.put(POWERUP_SHIELD, spriteSheet.getSubimage(0, 74, 12, 12));
        imageMap.put(POWERUP_SHIELD_SMALL, spriteSheet.getSubimage(0, 62, 12, 12));
        imageMap.put(POWERUP_SHIELD_LARGE, spriteSheet.getSubimage(0, 86, 12, 12));
        imageMap.put(POWERUP_SPEED, spriteSheet.getSubimage(12, 74, 12, 12));
        imageMap.put(POWERUP_SPEED_SMALL, spriteSheet.getSubimage(12, 62, 12, 12));
        imageMap.put(POWERUP_SPEED_LARGE, spriteSheet.getSubimage(12, 86, 12, 12));
        
        imageMap.put(GREEN_TINT, spriteSheet.getSubimage(40, 110, 18, 18));
        imageMap.put(BLUE_TINT, spriteSheet.getSubimage(58, 110, 18, 18));
        imageMap.put(SHIELD, spriteSheet.getSubimage(16, 104, 24, 24));
        imageMap.put(RED_METER, spriteSheet.getSubimage(0, 98, 4, 6));
        imageMap.put(RED_METER_FILL, spriteSheet.getSubimage(8, 98, 4, 6));
        imageMap.put(RED_METER_FLASH, spriteSheet.getSubimage(4, 98, 4, 6));
        imageMap.put(BLUE_METER, spriteSheet.getSubimage(12, 98, 4, 6));
        imageMap.put(BLUE_METER_FILL, spriteSheet.getSubimage(20, 98, 4, 6));
        imageMap.put(BLUE_METER_FLASH, spriteSheet.getSubimage(16, 98, 4, 6));
        imageMap.put(YELLOW_METER, spriteSheet.getSubimage(24, 98, 4, 6));
        imageMap.put(YELLOW_METER_FILL, spriteSheet.getSubimage(32, 98, 4, 6));
        imageMap.put(YELLOW_METER_FLASH, spriteSheet.getSubimage(28, 98, 4, 6));
        imageMap.put(GREEN_METER, spriteSheet.getSubimage(36, 98, 4, 6));
        imageMap.put(GREEN_METER_FILL, spriteSheet.getSubimage(44, 98, 4, 6));
        imageMap.put(GREEN_METER_FLASH, spriteSheet.getSubimage(40, 98, 4, 6));
        imageMap.put(YELLOW_ALIEN, spriteSheet.getSubimage(0, 0, 16, 16));
        imageMap.put(GREEN_ALIEN, spriteSheet.getSubimage(32, 0, 20, 16));
        imageMap.put(BLUE_ALIEN, spriteSheet.getSubimage(72, 0, 24, 16));
        imageMap.put(YELLOW_ALIEN_MOVE, spriteSheet.getSubimage(16, 0, 16, 16));
        imageMap.put(GREEN_ALIEN_MOVE, spriteSheet.getSubimage(52, 0, 20, 16));
        imageMap.put(BLUE_ALIEN_MOVE, spriteSheet.getSubimage(96, 0, 24, 16));
        
        imageMap.put(MOTHERSHIP, motherShip.getSubimage(0, 0, 122, 47));
        imageMap.put(MOTHERSHIP_BEAM, motherShip.getSubimage(0, 47, 38, 46));

        im = new ImageManager(imageMap);
        images = new ArrayList<>();
    }
    
    @Override
    public BufferedImage getImage(String name){
        return (BufferedImage) im.getImage(name);
    }
}
