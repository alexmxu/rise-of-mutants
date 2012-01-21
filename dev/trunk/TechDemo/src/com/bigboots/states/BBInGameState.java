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
package com.bigboots.states;

import com.bigboots.BBGlobals;
import com.bigboots.audio.BBAudioManager;
import com.bigboots.components.camera.BBCameraComponent;
import com.bigboots.components.camera.BBCameraManager;
import com.bigboots.components.camera.BBSideModeCamera;
import com.bigboots.components.BBAudioComponent;
import com.bigboots.components.BBMonsterManager;
import com.bigboots.components.BBNodeComponent;
import com.bigboots.components.BBPlayerManager;
import com.bigboots.core.BBDebugInfo;
import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.gui.BBGuiManager;
import com.bigboots.input.BBInputManager;
import com.bigboots.input.BBPlayerActions;
import com.bigboots.physics.BBBasicCollisionListener;
import com.bigboots.physics.BBPhysicsManager;
import com.bigboots.scene.BBSceneComposer;
import com.bigboots.scene.BBShaderManager;

import com.jme3.animation.AnimChannel;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;

//for player
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.Node;
import com.jme3.asset.ModelKey;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;

import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.plugins.blender.BlenderModelLoader;
import com.jme3.util.BufferUtils;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBInGameState extends BBAbstractState{

    private BBNodeComponent humanStalker;
    //music
    private BBAudioComponent music;   
    private GameActionListener actionListener;
    private BBPlayerActions playerListener = new BBPlayerActions();

    public BBInGameState() {
        
    }
    
    @Override
    public void initialize(BBEngineSystem eng) {       
        super.initialize(eng);
        
        //BBGuiManager.getInstance().getNifty().gotoScreen("progress");
        BBGuiManager.getInstance().getNifty().gotoScreen("hud");
        //BBGuiManager.getInstance().enableProgressBar(true);
        
        actionListener = new GameActionListener(eng);
        
        BBPhysicsManager.getInstance().init(engineSystem);
        
        //Set up keys
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_CAMERA_POS, new KeyTrigger(KeyInput.KEY_F2));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_MEMORY, new KeyTrigger(KeyInput.KEY_F3));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_HIDE_STATS, new KeyTrigger(KeyInput.KEY_F4));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_LEFT, new KeyTrigger(KeyInput.KEY_J));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_RIGHT, new KeyTrigger(KeyInput.KEY_L));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_UP, new KeyTrigger(KeyInput.KEY_I));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_DOWN, new KeyTrigger(KeyInput.KEY_K));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_DEBUG, new KeyTrigger(KeyInput.KEY_F1));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_MLEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_MRIGHT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        BBInputManager.getInstance().getInputManager().addListener(actionListener, 
                                                                    BBGlobals.INPUT_MAPPING_EXIT, 
                                                                    BBGlobals.INPUT_MAPPING_DEBUG,
                                                                    BBGlobals.INPUT_MAPPING_CAMERA_POS,
                                                                    BBGlobals.INPUT_MAPPING_MEMORY);
        BBInputManager.getInstance().getInputManager().addListener(playerListener, 
                                                                    BBGlobals.INPUT_MAPPING_LEFT,
                                                                    BBGlobals.INPUT_MAPPING_RIGHT, 
                                                                    BBGlobals.INPUT_MAPPING_UP, 
                                                                    BBGlobals.INPUT_MAPPING_DOWN,
                                                                    BBGlobals.INPUT_MAPPING_JUMP,
                                                                    BBGlobals.INPUT_MAPPING_MLEFT,
                                                                    BBGlobals.INPUT_MAPPING_MRIGHT);
        
        BBInputManager.getInstance().getInputManager().setCursorVisible(false);
        
        
        //Load first scene and camera
        Camera cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(25f, 10f, 0f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
              
        ViewPort vp = engineSystem.getRenderManager().createMainView("TEST", cam);
        vp.setClearFlags(true, true, true);
        BBSceneManager.getInstance().setViewPort(vp);
                
        //init global music
        music = new BBAudioComponent("Sounds/battle.ogg", false);
        //music = new BBAudioComponent("Sounds/ingame.ogg", false);
        music.setVolume(1);
        music.setLooping(true);
        music.play();

        //*******************************************
        //Create the main Character
        BBPlayerManager.getInstance().createMainPlayer("PLAYER_NAME", "Scenes/TestScene/character.mesh.xml", new Vector3f(0,-0.85f, 0), 2.0f);
                
        // Create a component Node for camera 
        humanStalker = new BBNodeComponent("HumanStalker");
        BBSceneManager.getInstance().addChild(humanStalker);
        
        BBCameraManager.getInstance().registerCamera("SIDE_CAM", BBCameraComponent.CamMode.SIDE, cam, true);
        BBCameraComponent camera = BBCameraManager.getInstance().getCurrentCamera();
        
        if(camera.getCamMode().equals(BBCameraComponent.CamMode.SIDE)){
            BBSideModeCamera sideCam = (BBSideModeCamera)camera;
            sideCam.setPosition(new Vector3f(0, 10, 35));
            sideCam.setTarget(humanStalker);
        }    
        
        //*******************************************
        //Create enemies

        for (int i=0; i<5; i++){
         Vector3f mPos = new Vector3f(100 + i*10, 100, 0f);  
         BBMonsterManager.getInstance().createMonter("ENEMY" + i*2, "Scenes/TestScene/mutant.j3o", mPos, new Vector3f(0,-1.0f, 0), 2+i);
        }

        
        
        //********************************************
        //Set collision listener
        BBBasicCollisionListener basicCol = new BBBasicCollisionListener();
        BBPhysicsManager.getInstance().getPhysicsSpace().addCollisionListener(basicCol);
        
      //  BBSceneManager.getInstance().createFilterProcessor();
        
/*      
        //Create post effect processor
        BBSceneManager.getInstance().createFilter("GAME_BLOOM", BBSceneManager.FilterType.BLOOM);
        BloomFilter tmpFilter = (BloomFilter) BBSceneManager.getInstance().getFilterbyName("GAME_BLOOM");
        tmpFilter.setBloomIntensity(2.0f);
        tmpFilter.setExposurePower(1.3f);
         
        BBSceneManager.getInstance().createFilter("GAME_BLEUR", BBSceneManager.FilterType.DEPHT);
        DepthOfFieldFilter tmpFltrBleur= (DepthOfFieldFilter) BBSceneManager.getInstance().getFilterbyName("GAME_BLEUR");
        tmpFltrBleur.setFocusDistance(0);
        tmpFltrBleur.setFocusRange(150);
        tmpFltrBleur.setBlurScale(1.4f);
        
        //Create sun
        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir = new Vector3f(35.12f, -0.3729129f, 3.74847335f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        BBSceneManager.getInstance().getRootNode().addLight(sun);
        
        BBSceneManager.getInstance().createFilter("GAME_LIGHT", BBSceneManager.FilterType.LIGHT);
        LightScatteringFilter tmpFltrLight = (LightScatteringFilter) BBSceneManager.getInstance().getFilterbyName("GAME_LIGHT");
        Vector3f lightPos = lightDir.multLocal(-5000);
        tmpFltrLight.setLightPosition(lightPos);
     
        BBSceneManager.getInstance().createFilter("GAME_TOON", BBSceneManager.FilterType.CARTOON);
 */  
        
        // Load the main map (here blend loading)
        BBSceneManager.getInstance().setupBasicLight();
        BBSceneManager.getInstance().createSky();
        loadScene();
    }
    
    @Override
    public void stateDetached() {
        super.stateDetached();
        
        //reset input
        BBInputManager.getInstance().getInputManager().removeListener(actionListener);
        BBInputManager.getInstance().getInputManager().removeListener(playerListener);
        
        music.destroy();
        music = null;
        
        BBPlayerManager.getInstance().destroy();
    
        humanStalker.detachAllChildren();
        humanStalker.removeFromParent();
        humanStalker.getWorldLightList().clear();
        humanStalker.getLocalLightList().clear();
        humanStalker = null;

        BBAudioManager.getInstance().destroy();
        BBPhysicsManager.getInstance().destroy();
        
        BBSceneManager.getInstance().destroy();
        
        this.engineSystem.getRenderManager().clearQueue(BBSceneManager.getInstance().getViewPort());        
        this.engineSystem.getRenderManager().removeMainView("TEST"); 
        
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        //******************************************************
        // Update character
        Vector3f pos = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).getPhysicsLocation();
        humanStalker.setLocalTranslation(pos);
        
        BBPlayerManager.getInstance().update(tpf);
        
        BBMonsterManager.getInstance().update(tpf);
   
    }

    private class GameActionListener implements AnimEventListener, ActionListener, AnalogListener {
        private BBEngineSystem engineSystem;
        public GameActionListener(BBEngineSystem eng){
             engineSystem = eng;
        }
        
        public void onAction(String binding, boolean keyPressed, float tpf) {
            
            if (binding.equals(BBGlobals.INPUT_MAPPING_EXIT) && !keyPressed) {
              BBInputManager.getInstance().getInputManager().setCursorVisible(true);  
              BBGuiManager.getInstance().getNifty().gotoScreen("game");
              //TODO : pause the game by disabling render and actions update. Let the input enabled
              //this.engineSystem.setSystemPause(!this.engineSystem.isSystemPause());
              return;
            }
            else if (binding.equals(BBGlobals.INPUT_MAPPING_DEBUG) && !keyPressed) { 
                
              BBPhysicsManager.getInstance().setDebugInfo(!BBPhysicsManager.getInstance().isShowDebug());
              BBDebugInfo.getInstance().setDisplayFps(!BBDebugInfo.getInstance().isShowFPS());
              BBDebugInfo.getInstance().setDisplayStatView(!BBDebugInfo.getInstance().isShowStat());
            }
            else if (binding.equals(BBGlobals.INPUT_MAPPING_CAMERA_POS) && !keyPressed) {
                    Vector3f loc = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).getPhysicsLocation();
                    Quaternion rot = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation();
                    System.out.println("***** Character Position: ("
                            + loc.x + ", " + loc.y + ", " + loc.z + ")");
                    System.out.println("***** Character Rotation: " + rot);
                    System.out.println("***** Character Direction: " + BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).getViewDirection());
            } else if (binding.equals(BBGlobals.INPUT_MAPPING_MEMORY) && !keyPressed) {
                BufferUtils.printCurrentDirectMemory(null);
            }
        }//end onAAction
        
              
        public void onAnalog(String binding, float value, float tpf) {
            
        }//end onAnalog
        
        // Abstract funtion coming with animation
        public void onAnimCycleDone(AnimControl control, AnimChannel chan, String animName) {
            //unused
        }
        // Abstract funtion coming with animation
        public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
            // unused
        }
    }//end GameActionListener

    
     
    public void loadScene(){

        // Load a blender file Scene. 
        DesktopAssetManager dsk = (DesktopAssetManager) BBSceneManager.getInstance().getAssetManager();        
        ModelKey bk = new ModelKey("Scenes/levels/level_01/level_01.blend");
        Node nd =  (Node) dsk.loadModel(bk);                 
        
        String entities = "assets/Models";
        String baseTex = "assets/Textures/base_textures";
        String levelTex = "assets/Textures/level_textures";
        String scenePath = bk.getFolder().substring(0, bk.getFolder().length() - 1); //BlenderKey sets "File.separator" in the end of String

        // Creating Entities from the Blend Scene
        BBSceneComposer sc = new BBSceneComposer(nd, entities, scenePath, baseTex, levelTex, BBSceneManager.getInstance().getAssetManager());

        //Clear Blend File
        nd.detachAllChildren();
        nd = null;
        dsk.clearCache();        
        
        // Added scene effects (fog, ibl)
        BBShaderManager shm = new BBShaderManager(BBSceneManager.getInstance().getRootNode(), BBSceneManager.getInstance().getAssetManager());
        shm.setSimpleIBLParam("Textures/skyboxes/sky_box_01/skybox_01_low.png");   
        shm.setFogParam(new ColorRGBA(0.7f,0.6f,0.2f, 43f), null);      
     
    }
}
