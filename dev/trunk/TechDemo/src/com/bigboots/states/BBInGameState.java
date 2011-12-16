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
import com.bigboots.animation.BBAnimManager;
import com.bigboots.audio.BBAudioManager;
import com.bigboots.components.BBAnimComponent;
import com.bigboots.components.BBAudioComponent;
import com.bigboots.components.BBCollisionComponent;
import com.bigboots.components.BBCollisionComponent.ShapeType;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.components.BBControlComponent;
import com.bigboots.components.BBEntity;
import com.bigboots.components.BBListenerComponent;
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
import com.jme3.animation.AnimChannel;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;

//for player
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.asset.DesktopAssetManager;
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
import com.jme3.math.Transform;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.plugins.blender.BlenderModelLoader;
import com.jme3.scene.shape.Box;


import java.util.HashMap;

/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBInGameState extends BBAbstractState{

    //private Nifty mNifty;
    
    Geometry geom_a;
    Material mat_box;
    // models
    Spatial obj01;
    Spatial obj02;
    Spatial obj03;
    Spatial ledder;
    // collision shapes
    Geometry obj01_l;
    Geometry obj02_l;
    Geometry obj03_l;
    Geometry ledder_l;

    protected Node ndmd, physicsModels;
    private BBNodeComponent humanStalker;

    // Temp workaround, speed is reset after blending.
    private float smallManSpeed = .6f;
    
    //music
    private BBAudioComponent music;
    
    private GameActionListener actionListener = new GameActionListener();
    private BBPlayerActions playerListener = new BBPlayerActions();
    
    private HashMap<Long, BBEntity> mapEnemies = new HashMap<Long, BBEntity>();
    
    public BBInGameState() {
        
    }
    
    @Override
    public void initialize(BBEngineSystem eng) {
        BBGuiManager.getInstance().getNifty().gotoScreen("progress");
        BBGuiManager.getInstance().enableProgressBar(true);
        
        super.initialize(eng);
        
        BBPhysicsManager.getInstance().init(engineSystem);
        
        //Set up keys
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_CAMERA_POS, new KeyTrigger(KeyInput.KEY_C));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_MEMORY, new KeyTrigger(KeyInput.KEY_M));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_HIDE_STATS, new KeyTrigger(KeyInput.KEY_F5));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_LEFT, new KeyTrigger(KeyInput.KEY_J));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_RIGHT, new KeyTrigger(KeyInput.KEY_L));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_UP, new KeyTrigger(KeyInput.KEY_I));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_DOWN, new KeyTrigger(KeyInput.KEY_K));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_DEBUG, new KeyTrigger(KeyInput.KEY_F1));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_MLEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_MRIGHT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        BBInputManager.getInstance().getInputManager().addListener(actionListener, BBGlobals.INPUT_MAPPING_EXIT, BBGlobals.INPUT_MAPPING_DEBUG);
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
        music.setVolume(3);
        music.setLooping(true);
        music.play();
        
        
        
        //*******************************************
        //Create the main Character
        BBPlayerManager.getInstance().createMainPlayer("PLAYER_NAME", "Scenes/TestScene/character.mesh.xml");
                
        // Create a component Node for camera 
        humanStalker = new BBNodeComponent("HumanStalker");
        BBSceneManager.getInstance().addChild(humanStalker);
        
        CameraNode camNode = new CameraNode("Camera Node", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(25, 10, 0));
        camNode.lookAt(humanStalker.getLocalTranslation(), Vector3f.UNIT_Y);
        humanStalker.attachChild(camNode);
        //BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).attachChild(camNode);
        
        //*******************************************
        //TEST AND LOAD ENEMY WITH ENTITY SYSTEM
        //set up out enemy object entity and put it in scene
        BBEntity mEnemy = new BBEntity("ENEMY");
        BBNodeComponent node = mEnemy.addComponent(CompType.NODE);
        mEnemy.loadModel("Scenes/TestScene/mutant.j3o");
        node.scale(5);
        node.setLocalTranslation(-0.44354653f, 3f, -90.836426f);
        BBSceneManager.getInstance().addChild(mEnemy.getComponent(BBNodeComponent.class));
        
        //Set up animation component      
        //mEnemy.createAnimation();
        BBAnimComponent anim = mEnemy.addComponent(CompType.ANIMATION);
        anim.getChannel().setAnim("mutant_idle");
        anim.getChannel().setLoopMode(LoopMode.Cycle);
        mEnemy.getComponent(BBAnimComponent.class).getChannel().setSpeed(1f);
        
        //Set up physic controler component
        CollisionShape shape = BBPhysicsManager.getInstance().createPhysicShape(ShapeType.CAPSULE, mEnemy);
        BBCollisionComponent colCp = mEnemy.addComponent(CompType.COLSHAPE);
        colCp.attachShape(shape);
               
        CharacterControl eControler = (CharacterControl) BBAnimManager.getInstance().createControl(BBControlComponent.ControlType.CHARACTER, mEnemy); 
        eControler.setJumpSpeed(20);
        eControler.setFallSpeed(30);
        eControler.setGravity(30);
        eControler.setUseViewDirection(true);
        BBControlComponent ctrlCp = mEnemy.addComponent(CompType.CONTROLLER);
        ctrlCp.setControlType(BBControlComponent.ControlType.CHARACTER);
        ctrlCp.attachControl(eControler);
        mEnemy.getComponent(BBNodeComponent.class).addControl(eControler);
        
        BBPhysicsManager.getInstance().getPhysicsSpace().addAll(mEnemy.getComponent(BBNodeComponent.class));
                
        //Set up enemy's sound component
        //Define the listener
        BBListenerComponent lst = mEnemy.addComponent(CompType.LISTENER);
        lst.setLocation(mEnemy.getComponent(BBNodeComponent.class).getWorldTranslation());
        BBAudioManager.getInstance().getAudioRenderer().setListener(lst);
        //Create associated audio
        BBAudioComponent audnde = mEnemy.addComponent(CompType.AUDIO);
        audnde.setSoundName("Sounds/growling1.wav", false);
        audnde.setLooping(true);
        audnde.setVolume(1);
        audnde.play();
        
        //Add it the map of Enemies
        mapEnemies.put(new Long(1), mEnemy);
        
        //********************************************
        //Set collision listener
        BBBasicCollisionListener basicCol = new BBBasicCollisionListener();
        BBPhysicsManager.getInstance().getPhysicsSpace().addCollisionListener(basicCol);     
      
        //Create post effect processor
        BBSceneManager.getInstance().createFilterProcessor();
        BBSceneManager.getInstance().createFilter("GAME_BLOOM", BBSceneManager.FilterType.BLOOM);
        BloomFilter tmpFilter = (BloomFilter) BBSceneManager.getInstance().getFilterbyName("GAME_BLOOM");
        tmpFilter.setBloomIntensity(2.0f);
        tmpFilter.setExposurePower(1.3f);
 /*         
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
        
        //BBGuiManager.getInstance().getNifty().gotoScreen("null");
        
        // Load the main map (here blend loading)
        BBSceneManager.getInstance().setupLight();
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
        
        //*************************************************
        // Update Enemies
        for(BBEntity object:mapEnemies.values()){
            Vector3f humanPos = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalTranslation().clone();
            Quaternion newRot = new Quaternion().fromAngleAxis(FastMath.rand.nextFloat()*2-.5f, Vector3f.UNIT_Y);
            humanPos.y = object.getComponent(BBNodeComponent.class).getLocalTranslation().y;            
            object.getComponent(BBNodeComponent.class).lookAt(humanPos,Vector3f.UNIT_Y);
            object.getComponent(BBNodeComponent.class).getLocalRotation().slerp(newRot,tpf);
            //System.out.println("**** POS : "+humanPos.toString());           
            float distSquared = humanPos.distanceSquared(object.getComponent(BBNodeComponent.class).getLocalTranslation());
            if(distSquared > 9){      
                object.getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setViewDirection(object.getComponent(BBNodeComponent.class).getLocalRotation().mult(Vector3f.UNIT_Z));            
                object.getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(object.getComponent(BBNodeComponent.class).getLocalRotation().mult(Vector3f.UNIT_Z).mult(tpf*1.8f));
                if(!object.getComponent(BBAnimComponent.class).getChannel().getAnimationName().equals("mutant_base_walk"))
                {
                    //a.getChild(0).getControl(AnimControl.class).getChannel(0).setAnim("RunTop", 0.50f); // TODO: Must activate "RunBase" after a certain time.                    
                    object.getComponent(BBAnimComponent.class).getChannel().setAnim("mutant_base_walk", 0.50f);
                    object.getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.Loop);
                }
                // Workaround
                if(object.getComponent(BBAnimComponent.class).getChannel().getSpeed()!=smallManSpeed){
                    object.getComponent(BBAnimComponent.class).getChannel().setSpeed(smallManSpeed);
                }            
            }
            else
            {
                object.getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                if(!object.getComponent(BBAnimComponent.class).getChannel().getAnimationName().equals("mutant_idle"))
                {
                    object.getComponent(BBAnimComponent.class).getChannel().setAnim("mutant_idle", 0.50f);
                    object.getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.Loop);
                }
            }
        }//end for
        
        //******************************************************
        // Update character
        Vector3f pos = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).getPhysicsLocation();
        humanStalker.setLocalTranslation(pos);
        //BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalTranslation(pos);
        
        BBPlayerManager.getInstance().udpate(tpf);
   
    }

    private class GameActionListener implements AnimEventListener, ActionListener, AnalogListener {

        public void onAction(String binding, boolean keyPressed, float tpf) {
            
            if (binding.equals(BBGlobals.INPUT_MAPPING_EXIT)) {
              BBInputManager.getInstance().getInputManager().setCursorVisible(true);  
              BBGuiManager.getInstance().getNifty().gotoScreen("game");
              return;
              //engineSystem.stop(false);
            }
            if (binding.equals(BBGlobals.INPUT_MAPPING_DEBUG) && !keyPressed) { 
                
              BBPhysicsManager.getInstance().setDebugInfo(!BBPhysicsManager.getInstance().isShowDebug());
              BBDebugInfo.getInstance().setDisplayFps(!BBDebugInfo.getInstance().isShowFPS());
              BBDebugInfo.getInstance().setDisplayStatView(!BBDebugInfo.getInstance().isShowStat());
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
        
        BBSceneManager.getInstance().getAssetManager().registerLoader(BlenderModelLoader.class, "blend");
        // Load a blender file. 
        //DesktopAssetManager dsk = (DesktopAssetManager) assetManager;        
        ModelKey bk = new ModelKey("Scenes/TestScene/test_scene_01_1.blend");
      //  bk.setFixUpAxis(false);
        Node nd =  (Node) BBSceneManager.getInstance().getAssetManager().loadModel(bk);
        BBSceneManager.getInstance().addChild(nd);
        
        // Material
        Material woodMat = BBSceneManager.getInstance().getAssetManager().loadMaterial("Scenes/TestScene/TestSceneMaterial.j3m");
        Material boxMat = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.setColor("m_Color", ColorRGBA.Blue);
        
        for (int i=0; i<nd.getChildren().size(); i++) {                     
            String strndscene = nd.getChild(i).getName();
            //System.out.println("***** VISIT NODE : "+strndscene);
            
            if (strndscene.startsWith("spawn") == true){
                nd.getChild(i).setMaterial(boxMat);
            }else{
                nd.getChild(i).setMaterial(woodMat);
            }
         } 
        
        CollisionShape myComplexShape = CollisionShapeFactory.createMeshShape(nd);
        RigidBodyControl worldPhysics = new RigidBodyControl(myComplexShape,0);  
        worldPhysics.createDebugShape(BBSceneManager.getInstance().getAssetManager());        
        BBPhysicsManager.getInstance().getPhysicsSpace().add(worldPhysics); 
        //BBSceneManager.getInstance().getRootNode().attachChild(worldPhysics.debugShape());
        
    }
}
