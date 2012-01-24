/*
 * Copyright (C) 2011  BigBoots Team
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See <http://www.gnu.org/licenses/>.
 */
package com.bigboots.components.camera;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBThirdPersonCamera extends BBCameraComponent{
    private Node mTarget;
    protected float minDistance = 1.0f;
    protected float maxDistance = 40.0f;
    
    public BBThirdPersonCamera(String name, Camera cam){
        super(name, cam);
        mCameraMode = CamMode.ORBITAL;
    }
    
    public void initCamera(){
        mJm3Camera.setParallelProjection( false );
        Vector3f loc = new Vector3f( 0.0f, 0.0f, 25.0f );
        Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f );
        Vector3f up = new Vector3f( 0.0f, 1.0f, 0.0f );
        Vector3f dir = new Vector3f( 0.0f, 0f, -1.0f );
        // Move our camera to a correct place and orientation.
        mJm3Camera.setFrame( loc, left, up, dir );
        // Signal that we've changed our camera's data
        mJm3Camera.update();

        Vector3f targetOffset = new Vector3f();
        targetOffset.y = 5;
        //ChaseCamera chaser = new ChaseCamera( cam, target.getCharacterNode() );
        //chaser.setTargetOffset(targetOffset);          
        
    }
    
    public void setTarget(Node node){
        mTarget = node;
        //mJm3Camera.setLocation( mTarget.getLocalTranslation().clone() );
    }

    /**
     * Returns the max zoom distance of the camera (default is 40)
     * @return maxDistance
     */
    public float getMaxDistance() {
        return maxDistance;
    }

    /**
     * Sets the max zoom distance of the camera (default is 40)
     * @param maxDistance
     */
    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    /**
     * Returns the min zoom distance of the camera (default is 1)
     * @return minDistance
     */
    public float getMinDistance() {
        return minDistance;
    }

    /**
     * Sets the min zoom distance of the camera (default is 1)
     * @return minDistance
     */
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }    
    

    @Override
    public void udpate() {
        //float camMinHeight = environment.getHeight( mJm3Camera.getLocation() ) + 1;
        //if (!Float.isInfinite(camMinHeight) && !Float.isNaN(camMinHeight) && mJm3Camera.getLocation().y <= camMinHeight) {
        //    mJm3Camera.getLocation().y = camMinHeight;
        //    mJm3Camera.update();
        //}
    }
    
}
