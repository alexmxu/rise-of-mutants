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

import com.bigboots.audio.BBAudioManager;
import com.bigboots.core.BBSceneManager;
import com.jme3.animation.AnimControl;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import com.jme3.audio.Listener;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 * 
 * Composite of our Object component Design
 */
public class BBEntity extends BBObject{
    private BBNodeComponent mNode;
    private BBAudioComponent mAudio;
    private BBAnimComponent mAnimation;
    
    protected Geometry mDisplay;
    protected CollisionShape mRigidBody;
    protected Light mLight;
    public Listener listener;
    protected boolean mEnable;
    private Spatial tmpSpatial;

    //Collection of child graphics.
    private List<BBObject> mChildComponents = new ArrayList<BBObject>();
    
    public BBEntity(String name){
        super(name);
        //mTransform = new Node(name);
        mNode = new BBNodeComponent(name);
        listener = new Listener();
        listener.setLocation(mNode.getWorldTranslation());
    }
    
    public void createEntity(String mesh){
       tmpSpatial =  BBSceneManager.getInstance().loadSpatial(mesh);
       tmpSpatial.setLocalTranslation(0, -.85f, 0);
       mNode.attachChild(tmpSpatial);
    }
    
    public void createAnimation(){
        mAnimation = new BBAnimComponent(tmpSpatial.getControl(AnimControl.class).createChannel());
    }
    
    public void createAudio(String name){
        mAudio = BBAudioManager.getInstance().createAudio(name, false);
    }
    
    public void createPhysic(){
        
    }
    
    private void attachModel(Spatial sp){
        mNode.attachChild(sp);
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
       return null;
        /*
        for (BBObject obj : mChildComponents) {
            if(obj.getType().equals("ENTITY")){
                ((BBEntity)obj).display();
            }
            else{
                obj.print(strBuff);
            }
        }
        */
    }
    
    public void getChildComponent(){
        
    }
    public Geometry getMesh(){
        return (Geometry)tmpSpatial;
    }
   
}
