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
package com.bigboots.core;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;


import com.jme3.scene.Node;
/**
 * Manage our specific SceneGraph
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBSceneManager implements BBUpdateListener{
    private static BBSceneManager instance = new BBSceneManager();

    private BBSceneManager() {
    }
    
    public static BBSceneManager getInstance() { 
        return instance; 
    }
    
    
    protected ViewPort viewPort;
    protected Node rootNode;
    
    public void init(BBEngineSystem eng, Camera cam){
        rootNode = eng.getRootNode();
        viewPort = eng.createView("TEST", cam);
        viewPort.setClearFlags(true, true, true);
        viewPort.setEnabled(true);
        viewPort.attachScene(rootNode);
        
        BBUpdateManager.getInstance().register(this);
    }
    
    // TODO : Change it next time
    public void createSky(AssetManager asset) {
        Spatial sky = SkyFactory.createSky(asset, "Textures/sky/skysphere.jpg", true);
        this.addChild(sky);
    }
    
    
    public void addChild(Spatial sp){
        rootNode.attachChild(sp);
    }
    
    public void update(float tpf) {
        
    }
    
    //TODO : To be changed
    public void setupLight(){
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al); 
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);   
    }  
    
    
}
