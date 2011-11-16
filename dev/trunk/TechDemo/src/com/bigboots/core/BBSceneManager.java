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

import com.jme3.scene.Node;
import com.jme3.system.JmeSystem;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Manage our specific SceneGraph
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBSceneManager {
    private static BBSceneManager instance = new BBSceneManager();

    private BBSceneManager() {
    }
    
    public static BBSceneManager getInstance() { 
        return instance; 
    }
    
    
    protected ViewPort viewPort;
    protected Node rootNode = new Node("Root Node");
    protected AssetManager assetManager;
    private  AmbientLight al;
    
    public void init(){   
        //BBUpdateManager.getInstance().register(this);
        if (assetManager == null){
            initAssetManager();
        }
    }
    
    public void setViewPort(ViewPort vp){
        viewPort = vp;
        viewPort.setEnabled(true);
        viewPort.attachScene(rootNode);
    }
    
    public Spatial loadSpatial(String name){
        Spatial model = assetManager.loadModel(name);
        return model;
    }
    // TODO : Change it next time
    public void createSky() {
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/sky/skysphere.jpg", true);
        this.addChild(sky);
    }
    
    
    public void addChild(Spatial sp){
        rootNode.attachChild(sp);
    }
    
    public void addChild(Node node){
        rootNode.attachChild(node);
    }
    
    public void update(float tpf) {
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    //TODO : To be changed
    public void setupLight(){
        // We add light so we see the scene
        al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al); 
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);   
    }  
    
    private void initAssetManager(){
        if (BBSettings.getInstance().getSettings() != null){
            String assetCfg = BBSettings.getInstance().getSettings().getString("AssetConfigURL");
            if (assetCfg != null){
                URL url = null;
                try {
                    url = new URL(assetCfg);
                } catch (MalformedURLException ex) {
                }
                if (url == null) {
                    url = BBEngineSystem.class.getClassLoader().getResource(assetCfg);
                    if (url == null) {
                        //logger.log(Level.SEVERE, "Unable to access AssetConfigURL in asset config:{0}", assetCfg);
                        return;
                    }
                }
                assetManager = JmeSystem.newAssetManager(url);
            }
        }
        if (assetManager == null){
            assetManager = JmeSystem.newAssetManager(
                    Thread.currentThread().getContextClassLoader()
                    .getResource("com/jme3/asset/Desktop.cfg"));
        }
    }
    
    public void destroy(){
        rootNode.getWorldLightList().clear();
        rootNode.getLocalLightList().clear();
        rootNode.detachAllChildren();
        rootNode.removeFromParent();
        al = null;
        rootNode = null;
    }
               
    /**
     * Retrieves rootNode
     * @return rootNode Node object
     *
     */
    public Node getRootNode() {
        return rootNode;
    }
    
    public ViewPort getViewPort() {
        return viewPort;
    }
    /**
     * @return The {@link AssetManager asset manager} for this application.
     */
    public AssetManager getAssetManager(){
        return assetManager;
    }
    
}
