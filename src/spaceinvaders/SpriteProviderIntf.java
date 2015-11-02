/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import java.awt.image.BufferedImage;

/**
 *
 * @author Kyle
 */
public interface SpriteProviderIntf {
    
    public BufferedImage getImage(String name);
    
}
