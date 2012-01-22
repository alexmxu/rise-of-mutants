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
import com.jme3.scene.Spatial.CullHint;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBFirstPersonCamera extends BBCameraComponent{
    private Node mTargetNode;
    private boolean mTargetVisible;
    
    public BBFirstPersonCamera(String name, Camera cam){
        super(name, cam);
        mCameraMode = CamMode.FPS;
        mTargetVisible = false;
    }
    
    public void setTarget(Node node){
        mTargetNode = node;
        mJm3Camera.setLocation( mTargetNode.getLocalTranslation().clone() );

    }
    
    public void setTargetVisible(boolean value)
    { 
        mTargetVisible = value;
        if(value)
        {
            mTargetNode.setCullHint(CullHint.Inherit);
        }else{
            mTargetNode.setCullHint(CullHint.Always);
        }
    }
    
    public boolean isTargeVisible(){
        return mTargetVisible;
    }
    
    @Override
    public void setEnable(boolean value){
        super.setEnable(value);
        this.setTargetVisible(true);        
    }
    
    public void update(){
        Vector3f camPos = mTargetNode.getLocalTranslation().clone().addLocal( 0, 6, 0 );
        mJm3Camera.setLocation( camPos );
        mJm3Camera.update();
    }
 
}
