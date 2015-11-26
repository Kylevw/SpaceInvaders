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
public class ShipMovementLimitProvider implements MovementLimitProviderIntf {
    
    public ShipMovementLimitProvider(int minX, int maxX, int minY, int maxY){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    
    int minX, maxX, minY, maxY;
    
    @Override
    public int getMaxX() {
        return maxX;
    }

    @Override
    public int getMinX() {
        return minX;
    }

    @Override
    public int getMaxY() {
        return maxY;
    }

    @Override
    public int getMinY() {
        return minY;
    }
    
}
