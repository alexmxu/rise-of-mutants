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
package com.bigboots.components;

import com.bigboots.components.BBComponent.CompType;
import com.bigboots.core.BBSceneManager;
import com.jme3.animation.AnimControl;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 * 
 * Composite of our Object component Design
 */
public class BBEntity extends BBObject{
    private BBComponent mNode;
    private BBComponent mAudio;
    private BBComponent mAnimation;
    private BBComponent mLstr;
    private BBComponent mCollision;
    private BBComponent mControl;
    
    protected Geometry mDisplay;
    protected CollisionShape mRigidBody;
    protected Light mLight;
    
    protected boolean mEnable;
    private Spatial tmpSpatial;

    //Collection of child graphics.
    private List<BBObject> mChildComponents = new ArrayList<BBObject>();
    
    public BBEntity(String name){
        super(name);
       
    }
    
    public void loadModel(String mesh){
       tmpSpatial =  BBSceneManager.getInstance().loadSpatial(mesh);
       tmpSpatial.setLocalTranslation(0, -.85f, 0);
       mDisplay = new Geometry();
       this.getComponent(BBNodeComponent.class).attachChild(tmpSpatial);
    }
   
    
    public void setEnabled(boolean enabled) {
        mEnable = enabled;
    }

    public boolean isEnabled() {
        return mEnable;
    }
    //Adds the BBObject to the composition.
    public void addObjectComponent(BBObject obj) {
        mChildComponents.add(obj);
    }
 
    //Removes the BBObject from the composition.
    public void removeObjectComponent(BBObject obj) {
        mChildComponents.remove(obj);
    }
    
    public <T extends BBComponent>T addComponent(CompType type){
        if(type.equals(CompType.NODE)){
            mNode = new BBNodeComponent(name);
            return (T)mNode;
        }
        if(type.equals(CompType.ANIMATION)){
            mAnimation = new BBAnimComponent(tmpSpatial.getControl(AnimControl.class).createChannel());
            return (T)mAnimation;
        }
        if(type.equals(CompType.AUDIO)){
            mAudio = new BBAudioComponent();
            return (T)mAudio;
        }
        if(type.equals(CompType.LISTENER)){
            mLstr = new BBListenerComponent();
            return (T)mLstr;
        }
        if(type.equals(CompType.COLSHAPE)){
            mCollision = new BBCollisionComponent();
            return (T)mCollision;
        }
        if(type.equals(CompType.CONTROLLER)){
            mControl = new BBControlComponent();
            return (T)mControl;
        }
        
        return null;
    }
    
    public <T extends BBComponent>T getComponent(Class<T> name){
        if(name.equals(BBNodeComponent.class)){
            return (T)mNode;
        }
        if(name.equals(BBAudioComponent.class)){
            return (T)mAudio;
        }
        if(name.equals(BBAnimComponent.class)){
            return (T)mAnimation;
        }
        if(name.equals(BBListenerComponent.class)){
            return (T)mLstr;
        }
        if(name.equals(BBCollisionComponent.class)){
            return (T)mCollision;
        }
        if(name.equals(BBControlComponent.class)){
            return (T)mControl;
        }

       return null;

    }
    
    public void getChildComponent(){
        
    }
    public Geometry getGeometry(){
        return mDisplay;
    }
    
    public void destroy(){
        ((BBNodeComponent)mNode).detachAllChildren();
        ((BBNodeComponent)mNode).removeFromParent();
        ((BBNodeComponent)mNode).getWorldLightList().clear();
        ((BBNodeComponent)mNode).getLocalLightList().clear();
        mNode = null;
        
        ((BBAudioComponent)mAudio).destroy();
        mAudio = null;
        
        mAnimation = null;
        mLstr = null;
        mCollision = null;
        mControl = null;
    }
   
}
