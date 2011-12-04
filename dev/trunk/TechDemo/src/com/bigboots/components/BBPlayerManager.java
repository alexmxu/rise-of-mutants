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

import com.bigboots.animation.BBAnimManager;
import com.bigboots.components.BBCollisionComponent.ShapeType;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.core.BBSceneManager;
import com.bigboots.physics.BBPhysicsManager;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBPlayerManager {
    private static BBPlayerManager instance = new BBPlayerManager();

    private BBPlayerManager() {
    }
    
    public static BBPlayerManager getInstance() { 
        return instance; 
    }
    
    private BBEntity mMainPlayer;
    private Vector3f mMainLocation = new Vector3f(0, 10, 0);
    
    public void createMainPlayer(String name, String file){
        //Create the main Character       
        mMainPlayer = new BBEntity("PLAYER");
        BBNodeComponent pnode = mMainPlayer.addComponent(CompType.NODE);
        pnode.scale(2);
        pnode.setLocalTranslation(this.getMainLocation());
        
        mMainPlayer.loadModel("Scenes/TestScene/character.mesh.xml");
        BBSceneManager.getInstance().addChild(pnode);
                
        BBAnimComponent panim = mMainPlayer.addComponent(CompType.ANIMATION);
        panim.getChannel().setAnim("base_stand");
        panim.getChannel().setSpeed(1f); 
        panim.getChannel().setLoopMode(LoopMode.Cycle);
        
        CollisionShape pShape = BBPhysicsManager.getInstance().createPhysicShape(ShapeType.CAPSULE, mMainPlayer);
        BBCollisionComponent pColCp = mMainPlayer.addComponent(CompType.COLSHAPE);
        pColCp.attachShape(pShape);
        
        CharacterControl pControler = (CharacterControl) BBAnimManager.getInstance().createControl(BBControlComponent.ControlType.CHARACTER, mMainPlayer); 
        pControler.setJumpSpeed(35);
        pControler.setFallSpeed(40);
        pControler.setGravity(35);
        pControler.setUseViewDirection(true);
        BBControlComponent pCtrl = mMainPlayer.addComponent(CompType.CONTROLLER);
        pCtrl.setControlType(BBControlComponent.ControlType.CHARACTER);
        pCtrl.attachControl(pControler);
        mMainPlayer.getComponent(BBNodeComponent.class).addControl(pControler);
        BBPhysicsManager.getInstance().getPhysicsSpace().addAll(mMainPlayer.getComponent(BBNodeComponent.class));
        
    }
    
    public BBEntity getMainPlayer(){
        return mMainPlayer;
    }
    
    public Vector3f getMainLocation(){
        return mMainLocation;
    }
    
    public void destroy(){
        mMainPlayer.destroy();
    }
}
