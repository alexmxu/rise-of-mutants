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

import com.jme3.animation.AnimControl;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
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
    public Node mTransform;
    protected Geometry mDisplay;
    protected CollisionShape mRigidBody;
    protected Light mLight;
    public AudioNode mAudio;
    public Listener listener;
    protected AnimControl mAnimation;
    protected boolean mEnable;
    
    //Collection of child graphics.
    private List<BBObject> mChildComponents = new ArrayList<BBObject>();
    
    public BBEntity(String name){
        super(name);
        mTransform = new Node(name);
        listener = new Listener();
    }
  
    //Adds the BBObject to the composition.
    public void addComponent(BBObject obj) {
        mChildComponents.add(obj);
    }
 
    //Removes the BBObject from the composition.
    public void removeComponent(BBObject obj) {
        mChildComponents.remove(obj);
    }
    
    public void attachModel(Spatial sp){
        mTransform.attachChild(sp);
    }
    
    public void getComponent(String name){
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
    
    public void setSound(AudioNode aud) {
        mAudio = aud;
        if(mTransform != null){
            listener.setLocation(mTransform.getWorldTranslation());
        }
    }
    
    public void setEnabled(boolean enabled) {
        mEnable = enabled;
    }

    public boolean isEnabled() {
        return mEnable;
    }
}
