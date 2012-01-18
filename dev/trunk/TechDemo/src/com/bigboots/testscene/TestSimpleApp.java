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
import com.bigboots.animation.BBAnimManager;
import com.bigboots.components.BBAnimComponent;
import com.bigboots.components.BBCollisionComponent;
import com.bigboots.components.BBCollisionComponent.ShapeType;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.components.BBControlComponent;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.input.BBInputManager;

//Entity
import com.bigboots.components.BBEntity;
import com.bigboots.components.BBLightComponent;
import com.bigboots.components.BBMeshComponent;
import com.bigboots.components.BBNodeComponent;
import com.bigboots.components.BBObject;
import com.bigboots.physics.BBPhysicsManager;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light.Type;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.shape.Box;



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
        
    //The main function call to init the app
    public static void main(String[] args) {
        TestSimpleApp app = new TestSimpleApp();
        app.run();
    }   
    
    //Variables
    private MyActionListener actionListener;
    
    @Override
    public void simpleInitialize(){

        //Load the main camera
        Camera cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 5f, 25f));
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
                
        //Swithing physic on
        BBPhysicsManager.getInstance().init(engineSystem);
        
        //Create quick mesh and texture
        Box floor = new Box(Vector3f.ZERO, 10f, 0.1f, 5f);
        Material floor_mat = BBSceneManager.getInstance().getAssetManager().loadMaterial("Materials/Scene/Character/CharacterSkin.j3m");
        //Set up a Geometry for our box 
        BBMeshComponent floor_geo = new BBMeshComponent("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, -1f, 0);
        BBSceneManager.getInstance().addChild(floor_geo);
        // Make the floor physical with mass 0.0f
        RigidBodyControl floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        BBPhysicsManager.getInstance().getPhysicsSpace().add(floor_phy);
        
        //Create collision shape for our Entity by calling the PhysicMgr factory 
        CollisionShape pShape = BBPhysicsManager.getInstance().createPhysicShape(ShapeType.CAPSULE, mMainPlayer);
        //Create the collision component to attach the created shape
        BBCollisionComponent pColCp = mMainPlayer.addComponent(CompType.COLSHAPE);
        pColCp.attachShape(pShape);
        //Find and create the control to anime the shape
        CharacterControl pControler = (CharacterControl) BBAnimManager.getInstance().createControl(BBControlComponent.ControlType.CHARACTER, mMainPlayer); 
        pControler.setJumpSpeed(19);
        pControler.setFallSpeed(40);
        pControler.setGravity(35);
        pControler.setUseViewDirection(true);
        //Create the control component for our Entity and attach the specific control
        BBControlComponent pCtrl = mMainPlayer.addComponent(CompType.CONTROLLER);
        pCtrl.setControlType(BBControlComponent.ControlType.CHARACTER);
        pCtrl.attachControl(pControler);
        //Attached all to the Entity's Node and set it up in the physic space
        mMainPlayer.getComponent(BBNodeComponent.class).addControl(pControler);
        BBPhysicsManager.getInstance().getPhysicsSpace().addAll(mMainPlayer.getComponent(BBNodeComponent.class));
        
        //Trying Entity clone system to share texture and material
        BBEntity mCopy = mMainPlayer.clone("MYCOPY");
        mCopy.getComponent(BBNodeComponent.class).setLocalTranslation(new Vector3f(8,1,1));
        
        //Set up material after cloning to see the shared texture
        mMainPlayer.setMaterial("Scenes/TestScene/TestSceneMaterial.j3m");
        mMainPlayer.setMaterialToMesh("Sinbad-geom-7", "Models/Sinbad/SinbadMat.j3m");
        
        // Add a light Source
        BBLightComponent compLight = new BBLightComponent();
        compLight.setLightType(Type.Directional);
        compLight.getLight(DirectionalLight.class).setDirection(new Vector3f(0.5432741f, -0.58666015f, -0.6005691f).normalizeLocal());
        compLight.getLight(DirectionalLight.class).setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        BBSceneManager.getInstance().getRootNode().addLight(compLight.getLight(DirectionalLight.class));
        
    }
    
    @Override
    public void simpleUpdate(){
        if(actionListener.mQuit == true){
            engineSystem.stop(false);
        }
        
        //Put here your custom update code ...
        
    }    
 
}
