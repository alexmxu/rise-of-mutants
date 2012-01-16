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
package com.bigboots.testscene;

import com.bigboots.BBApplication;
import com.bigboots.BBGlobals;
import com.bigboots.components.BBAnimComponent;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.input.BBInputManager;

//Entity
import com.bigboots.components.BBEntity;
import com.bigboots.components.BBNodeComponent;
import com.bigboots.components.BBObject;
import com.jme3.animation.LoopMode;


import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;



class MyActionListener implements ActionListener{
    
    public boolean mQuit = false; 
    
    public void onAction(String binding, boolean keyPressed, float tpf) {
        if (binding.equals(BBGlobals.INPUT_MAPPING_EXIT) && !keyPressed) {
            mQuit = true; 
        }
    }
}


/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class TestSimpleApp extends BBApplication{
    
    private MyActionListener actionListener;
    
    @Override
    public void simpleInitialize(){

        //Load the main camera
        Camera cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 5f, 15f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        
        //Set up the main viewPort
        ViewPort vp = engineSystem.getRenderManager().createMainView("CUSTOM_VIEW", cam);
        vp.setClearFlags(true, true, true);
        vp.setBackgroundColor(ColorRGBA.Gray);
        BBSceneManager.getInstance().setViewPort(vp);
        
        //Set up basic light and sky coming with the standard scene manager
        BBSceneManager.getInstance().setupBasicLight();
        BBSceneManager.getInstance().createSky();
        
        //Set up keys and listener to read it
        actionListener = new MyActionListener();
        
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));        
        BBInputManager.getInstance().getInputManager().addListener(actionListener, BBGlobals.INPUT_MAPPING_EXIT);
        
        //Put here you custom init code ...
        //Example
        
        //**********************************************************************
        //Create the main Character as an entity      
        BBEntity mMainPlayer = new BBEntity("PLAYER");
        //Set the entity tag. If it is a player or monster or static object like building etc
        mMainPlayer.setObjectTag(BBObject.ObjectTag.PLAYER);
        //Create first of all the translation component attached to the scene
        BBNodeComponent pnode = mMainPlayer.addComponent(CompType.NODE);
        pnode.setLocalTranslation(new Vector3f(1,1,1));
        //Load the mesh file associated to this entity for visual
        mMainPlayer.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        //Set up an associated component. Here is animation
        BBAnimComponent panim = mMainPlayer.addComponent(CompType.ANIMATION);
        panim.getChannel().setAnim("IdleTop");
        panim.getChannel().setSpeed(1f); 
        panim.getChannel().setLoopMode(LoopMode.Cycle);
        
          
        // Add a light Source
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5432741f, -0.58666015f, -0.6005691f).normalizeLocal());
        dl.setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        BBSceneManager.getInstance().getRootNode().addLight(dl);
        
    }
    
    @Override
    public void simpleUpdate(){
        if(actionListener.mQuit == true){
            engineSystem.stop(false);
        }
        
        //Put here your custom update code ...
        
    }    
    
    //The main function call to init the app
    public static void main(String[] args) {
        TestSimpleApp app = new TestSimpleApp();
        app.run();
    }    
}
