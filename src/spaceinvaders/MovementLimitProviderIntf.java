/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

/**
 *
 * @author Kyle
 */
public interface MovementLimitProviderIntf {
    
    public int getMaxX();
    public int getMinX();

    public int getMaxY();
    public int getMinY();
}
