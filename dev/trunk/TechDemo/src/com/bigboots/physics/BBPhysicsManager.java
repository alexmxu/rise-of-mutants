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

import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBUpdateListener;
import com.bigboots.core.BBUpdateManager;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;

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
    @Override
    public void initialize(){
        
    }  
    @Override
    public void destroy(){
        stateManager.cleanup();
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
}
