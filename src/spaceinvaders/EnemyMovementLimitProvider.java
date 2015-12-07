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
public class EnemyMovementLimitProvider implements EnemyMovementPaternIntf {
    
    public EnemyMovementLimitProvider(int minStartY, int minY, int maxY){
        this.minStartY = minStartY;
        this.minY = minY;
        this.maxY = maxY;
    }
    
    int minStartY, minY, maxY;

    @Override
    public int getMaxY() {
        return maxY;
    }

    @Override
    public int getMinY() {
        return minY;
    }

    @Override
    public int getMinStartY() {
        return minStartY;
    }
    
}
