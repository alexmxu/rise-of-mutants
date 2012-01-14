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
package com.bigboots.physics;

import com.bigboots.components.BBCollisionComponent.ShapeType;
import com.bigboots.components.BBEntity;
import com.bigboots.components.BBNodeComponent;
import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBUpdateListener;
import com.bigboots.core.BBUpdateManager;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.*;


/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBPhysicsManager extends Application implements BBUpdateListener{
    private static BBPhysicsManager instance = new BBPhysicsManager();

    private BBPhysicsManager() {
    }
    
    public static BBPhysicsManager getInstance() { 
        return instance; 
    }
    
    private BulletAppState bulletAppState;
    private BBEngineSystem engineSystem;
    private boolean mShowPhysicDebug = false;
    
    public void update(float tpf) {
        //throw new UnsupportedOperationException("Not supported yet.");
               
        // update states need by Bullet
        stateManager.update(tpf);
        // render states
        stateManager.render(engineSystem.getRenderManager());
        stateManager.postRender();
    }
    
    public void init(BBEngineSystem eng){
        engineSystem = eng;
        stateManager = new AppStateManager(this);
        // Set up Physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        BBUpdateManager.getInstance().register(this);
    }
   
    public CollisionShape createPhysicShape(ShapeType type, BBEntity ent){
        
        BoundingBox vol = (BoundingBox)ent.getComponent(BBNodeComponent.class).getWorldBound();
        
        if(type.equals(ShapeType.CAPSULE)){
            CapsuleCollisionShape enShape = new CapsuleCollisionShape(vol.getZExtent()*0.8f, vol.getYExtent(), 1);
            return enShape;
        }
        if(type.equals(ShapeType.MESH)){
            MeshCollisionShape mshShape = new MeshCollisionShape(ent.getMesh());
            return mshShape;
        }
        
        return null;
    }
    
    public void setDebugInfo(boolean value){
        mShowPhysicDebug = value;
        if(value){
            bulletAppState.getPhysicsSpace().enableDebug(BBSceneManager.getInstance().getAssetManager());
        }else {
            bulletAppState.getPhysicsSpace().disableDebug();
        }
    }
    
    @Override
    public void initialize(){
        
    }  
    @Override
    public void destroy(){
        
        stateManager.detach(bulletAppState);
        bulletAppState.getPhysicsSpace().destroy();
        super.destroy();
    }
    @Override
    public void handleError(String errMsg, Throwable t){        
    }
    @Override
    public void gainFocus(){        
    }
    @Override
    public void loseFocus(){       
    }
    @Override
    public void requestClose(boolean esc){    
    }
    @Override
    public void update(){      
    }
    
    public BulletAppState getBulletApp(){
        return bulletAppState;
    }
    
    public PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    public boolean isShowDebug(){
        return mShowPhysicDebug;
    }
    
}
