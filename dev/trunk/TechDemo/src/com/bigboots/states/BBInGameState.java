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
import com.bigboots.components.BBEntity;
import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.gui.BBGuiManager;
import com.bigboots.input.BBInputManager;
import com.bigboots.physics.BBPhysicsManager;
import com.jme3.animation.AnimChannel;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;

//for gui
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
//for player
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.scene.Node;
import com.jme3.asset.BlenderKey;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.audio.AudioNode;
import java.util.HashMap;

/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBInGameState extends BBAbstractState implements ScreenController{

    private Nifty mNifty;
    
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
    protected Node human,humanStalker;
    protected Node ndmd, physicsModels;
    protected Spatial player;
    
    private AnimControl control;
    private AnimChannel playerChannel;
    
    float hasJumped = 0;
    boolean hasBeenOnGround = false;
    private int pressed=0;
    private boolean walk, jump = false;
    // Temp workaround, speed is reset after blending.
    private float smallManSpeed = .6f;
    
    //music
    private AudioNode music;
    private AudioNode step;
    
    private GameActionListener actionListener = new GameActionListener();
    private static final Logger logger = Logger.getLogger(BBInGameState.class.getName());
    
    private HashMap<Long, Node> mapEnemies = new HashMap<Long, Node>();
    
    @Override
    public void initialize(BBEngineSystem eng) {
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
        
        BBInputManager.getInstance().getInputManager().addListener(actionListener, 
                                                                    BBGlobals.INPUT_MAPPING_LEFT,
                                                                    BBGlobals.INPUT_MAPPING_RIGHT, 
                                                                    BBGlobals.INPUT_MAPPING_UP, 
                                                                    BBGlobals.INPUT_MAPPING_DOWN,
                                                                    BBGlobals.INPUT_MAPPING_JUMP,
                                                                    BBGlobals.INPUT_MAPPING_EXIT);
        
        BBInputManager.getInstance().getInputManager().setCursorVisible(false);
        
        mNifty = BBGuiManager.getInstance().getNifty();
        
        //Load first scene and camera
        Camera cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(25f, 10f, 0f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        
        
        ViewPort vp = engineSystem.getRenderManager().createMainView("TEST", cam);
        vp.setClearFlags(true, true, true);
        BBSceneManager.getInstance().setViewPort(vp);
        
        BBSceneManager.getInstance().setupLight();
        BBSceneManager.getInstance().createSky();
        
        
        humanStalker = new Node("HumanStalker");
                
        human = new Node("human");
        //Spatial sinbadModel2 = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        Spatial sinbadSpatial = BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/character.mesh.xml");

        Node sinbadModel = new Node();
        sinbadModel.attachChild(sinbadSpatial);
        sinbadSpatial.setLocalTranslation(0, -.85f, 0);
        sinbadSpatial.setName("human_player");

        human.attachChild(sinbadModel);
        //    human.attachChild(sword);
        human.setLocalTranslation(new Vector3f(0, 10, 0));

        //Set up animation
        control = sinbadSpatial.getControl(AnimControl.class);
        //control.addListener(actionListener);
        // PlayerChannel later refered to by player.getControl(AnimControl.class).getChannel(0);
        playerChannel = control.createChannel();
        playerChannel.setAnim("base_stand");
        //    playerChannel.setAnim("RunBase");
        playerChannel.setLoopMode(LoopMode.Cycle);
        playerChannel.setSpeed(1f);    

        // CapWe also put the player in its starting position.
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(.7f, 2f, 1);
        CharacterControl pControler = new CharacterControl(capsuleShape, .05f);

        pControler.setJumpSpeed(35);
        pControler.setFallSpeed(40);
        pControler.setGravity(35);
        //pControler.setPhysicsLocation(player.getWorldTranslation());//new Vector3f(0, 10, 0));
        pControler.setUseViewDirection(true);   

        // Add phys. controller to player.
        //sinbadModel.addControl(pControler);
        human.addControl(pControler);
        human.scale(2); 
        player = human.getChild("humanplayer");
        //BBPhysicsManager.getInstance().getPhysicsSpace().addAll(human);
        BBSceneManager.getInstance().addChild(human);
        BBSceneManager.getInstance().addChild(humanStalker);
        
        //Physics
        BBPhysicsManager.getInstance().getPhysicsSpace().addAll(human);
        
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(25, 10, 0));
        //Rotate the camNode to look at the target:
        camNode.lookAt(humanStalker.getLocalTranslation(), Vector3f.UNIT_Y);
        //Attach the camNode to the target:
        humanStalker.attachChild(camNode);
        
        //*******************************************
        //TEST AND LOAD ENEMY WITH ENTITY SYSTEM
        BBEntity mEnemy = new BBEntity("ENEMY");
        Spatial tmpSpatial = BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/mutant.j3o");
        //Node tmpModel = new Node();
        //tmpModel.attachChild(tmp);
        tmpSpatial.setLocalTranslation(0, -.85f, 0);
        mEnemy.mTransform.attachChild(tmpSpatial);
        mEnemy.mTransform.scale(5);
        mEnemy.mTransform.setLocalTranslation(-0.44354653f, 5f, -90.836426f);//(3.20598009f, 3.1f, -22.878937f);
        //Set up animation
        AnimControl enControl = tmpSpatial.getControl(AnimControl.class);
        //control.addListener(actionListener);
        // PlayerChannel later refered to by player.getControl(AnimControl.class).getChannel(0);
        AnimChannel enChannel = enControl.createChannel();
        enChannel.setAnim("mutant_idle");
        enChannel.setLoopMode(LoopMode.Cycle);
        enChannel.setSpeed(1f);
        
        // CapWe also put the player in its starting position.
        CapsuleCollisionShape enShape = new CapsuleCollisionShape(.7f, 7f, 1);
        CharacterControl eControler = new CharacterControl(enShape, .05f);
        eControler.setJumpSpeed(20);
        eControler.setFallSpeed(30);
        eControler.setGravity(30);
        eControler.setUseViewDirection(true);       
        //eControler.setPhysicsLocation(new Vector3f(-15.5712421f, 5.2f, 0.255402f));
        mEnemy.mTransform.addControl(eControler);
        
        BBSceneManager.getInstance().addChild(mEnemy.mTransform);
        //BBPhysicsManager.getInstance().getPhysicsSpace().add(mEnemy.mTrasform.getControl(CharacterControl.class));
        BBPhysicsManager.getInstance().getPhysicsSpace().addAll(mEnemy.mTransform);
        //Add it the map of Enemies
        mapEnemies.put(new Long(1), mEnemy.mTransform);
        
        loadScene();
        
        //init music
        //BBAudioManager.getInstance().getListener().setLocation(humanStalker.getWorldTranslation());
        music = BBAudioManager.getInstance().createAudio("Sounds/game.wav", false);
        music.setVolume(3);
        music.setLooping(true);
        music.play();
        
        step = BBAudioManager.getInstance().createAudio("Sounds/step1.wav", false);
        step.setVolume(10);
        
        //TEST enemy sound
        AudioNode eAud = BBAudioManager.getInstance().createAudio("Sounds/growling1.wav", false);
        mEnemy.setSound(eAud);
        BBAudioManager.getInstance().getAudioRenderer().setListener(mEnemy.listener);
        mEnemy.mAudio.setLooping(true);
        mEnemy.mAudio.setVolume(1);
        mEnemy.mAudio.play();
    }
    
    @Override
    public void stateDetached() {
        super.stateDetached();
        
        human.detachAllChildren();
        human.removeFromParent();
        human.getWorldLightList().clear();
        human.getLocalLightList().clear();
        human = null;
                
        humanStalker.detachAllChildren();
        humanStalker.removeFromParent();
        humanStalker.getWorldLightList().clear();
        humanStalker.getLocalLightList().clear();
        humanStalker = null;
        
        ndmd.detachAllChildren();
        ndmd.removeFromParent();
        ndmd.getWorldLightList().clear();
        ndmd.getLocalLightList().clear();
        ndmd = null;
        
        obj01.removeFromParent();
        obj01 = null;
        obj02.removeFromParent();
        obj02 = null;
        obj03.removeFromParent();
        obj03 = null;
        ledder.removeFromParent();
        ledder = null;
        player.removeFromParent();
        player = null;
        
        obj01_l = null;
        obj02_l = null;
        obj03_l = null;
        ledder_l = null;
        
        physicsModels.detachAllChildren();
        physicsModels.removeFromParent();
        physicsModels.getWorldLightList().clear();        
        physicsModels.getLocalLightList().clear();
        physicsModels = null;
        
        BBSceneManager.getInstance().destroy();
        BBSceneManager.getInstance().getViewPort().clearScenes();
        
        this.engineSystem.getRenderManager().clearQueue(BBSceneManager.getInstance().getViewPort());        
        this.engineSystem.getRenderManager().removeMainView("TEST"); 
        
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        //*************************************************
        // Update Enemies
        for(Node a:mapEnemies.values()){
            Vector3f humanPos = human.getLocalTranslation().clone();
            Quaternion newRot = new Quaternion().fromAngleAxis(FastMath.rand.nextFloat()*2-.5f, Vector3f.UNIT_Y);
            humanPos.y = a.getLocalTranslation().y;            
            a.lookAt(humanPos,Vector3f.UNIT_Y);
            a.getLocalRotation().slerp(newRot,tpf);
            //System.out.println("**** POS : "+humanPos.toString());           
            float distSquared = humanPos.distanceSquared(a.getLocalTranslation());
            if(distSquared>9){      
            a.getControl(CharacterControl.class).setViewDirection(a.getLocalRotation().mult(Vector3f.UNIT_Z));            
            a.getControl(CharacterControl.class).setWalkDirection(a.getLocalRotation().mult(Vector3f.UNIT_Z).mult(tpf*1.8f));
                if(!a.getChild(0).getControl(AnimControl.class).getChannel(0).getAnimationName().equals("mutant_base_walk"))
                {
                //a.getChild(0).getControl(AnimControl.class).getChannel(0).setAnim("RunTop", 0.50f); // TODO: Must activate "RunBase" after a certain time.                    
                a.getChild(0).getControl(AnimControl.class).getChannel(0).setAnim("mutant_base_walk", 0.50f);
                a.getChild(0).getControl(AnimControl.class).getChannel(0).setLoopMode(LoopMode.Loop);
                }
                // Workaround
                if(a.getChild(0).getControl(AnimControl.class).getChannel(0).getSpeed()!=smallManSpeed){
                a.getChild(0).getControl(AnimControl.class).getChannel(0).setSpeed(smallManSpeed);
                }            
            }
            else{
            a.getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                if(!a.getChild(0).getControl(AnimControl.class).getChannel(0).getAnimationName().equals("mutant_idle"))
                {
                a.getChild(0).getControl(AnimControl.class).getChannel(0).setAnim("mutant_idle", 0.50f);
                a.getChild(0).getControl(AnimControl.class).getChannel(0).setLoopMode(LoopMode.Loop);
                }
               
            }
        }//end for
        
        //******************************************************
        // Update character
        Vector3f pos = human.getControl(CharacterControl.class).getPhysicsLocation();
        humanStalker.setLocalTranslation(pos);
        
         PhysicsCharacter anv = human.getControl(CharacterControl.class);
        
        if(anv.onGround()){
            if(jump)
            {
                boolean hasBeenOnGroundCopy = hasBeenOnGround;
                if(!hasBeenOnGround)
                    hasBeenOnGround=true;
         
                if(hasBeenOnGroundCopy)
                {
                    hasJumped+=tpf;

                    logger.log(Level.INFO,"Character jumping end.");
                    jump = false;
                    hasJumped = 0;
                    if(walk){
                        
                        logger.log(Level.INFO,"Character jumping end. Start stand.");

                        playerChannel.setAnim("run_01", 0.50f);
                        playerChannel.setLoopMode(LoopMode.Loop);
                    }
                    else{
                        logger.log(Level.INFO,"Character jumping end. Start stand.");

                        playerChannel.setAnim("base_stand", 0.50f);
                        playerChannel.setLoopMode(LoopMode.DontLoop);                           
                        human.getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                    }
                    hasBeenOnGround = false;
                }
            }
        }
        else if(!jump){
            logger.log(Level.INFO,"Character jumping start.");
            jump = true;
            
            playerChannel.setAnim("jump", 0.50f); // TODO: Must be activated after a certain time after "JumpStart"
            playerChannel.setLoopMode(LoopMode.DontLoop);
        }
       
        
        
    }

    public void bind(Nifty nifty, Screen screen){
        mNifty = nifty;
    }
    public void onStartScreen(){
        
    }
    public void onEndScreen(){
        
    }
    public void quitToMain(){
        // switch to another screen
        //mNifty.gotoScreen("null");
        BBStateManager.getInstance().detach(this);
        //reset input
        BBInputManager.getInstance().getInputManager().removeListener(actionListener);
        BBInputManager.getInstance().getInputManager().clearMappings();
        BBInputManager.getInstance().resetInput(); 
        //Change Game state
        //mNifty.gotoScreen("start");
        //BBMainMenuState menu = new BBMainMenuState();
        //BBStateManager.getInstance().attach(menu);
    
    }
   
      
    private static final class Directions{
        private static final Quaternion rot = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
        private static final Quaternion upDir = Quaternion.DIRECTION_Z.mult(rot);
        private static final Quaternion rightDir = upDir.mult(rot);
        private static final Quaternion downDir = rightDir.mult(rot);
        private static final Quaternion leftDir = downDir.mult(rot);
    }
    private float time = 0;
    private class GameActionListener implements AnimEventListener, ActionListener, AnalogListener {

        public void onAction(String binding, boolean value, float tpf) {
            
            if (binding.equals(BBGlobals.INPUT_MAPPING_EXIT)) {
              //System.out.println("ESCAPE");  
              //mNifty.gotoScreen("game");
                engineSystem.stop(false);
            } 
            
            if(value == true){
              pressed++;
            }
            else{
              pressed--;                      
            }
            if(pressed == 1 && value& !binding.equals("Jump") && !walk){
              walk = true;
              if(!jump){
              logger.log(Level.INFO,"Character walking init.");
              //playerChannel.setAnim("RunTop", 0.50f); // TODO: Must activate "RunBase" after a certain time.
              playerChannel.setAnim("run_01", 0.50f); // TODO: Must be activated after a certain time after "RunTop"
              playerChannel.setLoopMode(LoopMode.Loop);
              
              //Trying to repeat the walk sound
              time += tpf;
              if (time > 0f) {
                step.play();
                time = 0;
              }

              }
            }
            else if (pressed==0&!value&!binding.equals("Jump")) {
              walk = false;
              if(!jump){
                  logger.log(Level.INFO,"Character walking end.");
                  //playerChannel.setAnim("IdleTop", 0.50f);
                  playerChannel.setAnim("base_stand", 0.50f);          
                  playerChannel.setLoopMode(LoopMode.DontLoop);
                  step.stop();
              }
            }
            if (binding.equals("Jump") &! jump ) {
                if (value){
                    logger.log(Level.INFO,"Character jumping start.");
                    jump = true;
                    // channel.setAnim("JumpStart", 0.5f); // TODO: Must activate "JumpLoop" after a certain time.
                    human.getControl(CharacterControl.class).jump();
                    playerChannel.setAnim("jump", 0.50f); // TODO: Must be activated after a certain time after "JumpStart"
                    playerChannel.setLoopMode(LoopMode.DontLoop);
                }
            }
        }//end onAAction
        
              
        public void onAnalog(String binding, float value, float tpf) {
            //System.out.println("******** BINDING :"+ binding.toString() +"********");
            //System.out.println("******** JUMP VALUE  :"+ jump +"********");
            if(!jump){
                if (binding.equals(BBGlobals.INPUT_MAPPING_LEFT)) {
                    Quaternion newRot = new Quaternion().slerp(human.getLocalRotation(),Directions.leftDir, tpf*8);
                    human.setLocalRotation(newRot);
                }
                else if (binding.equals("Right")) {
                    Quaternion newRot = new Quaternion().slerp(human.getLocalRotation(),Directions.rightDir, tpf*8);
                    human.setLocalRotation(newRot);        
                } else if (binding.equals("Up")) {
                    Quaternion newRot = new Quaternion().slerp(human.getLocalRotation(),Directions.upDir, tpf*8);
                    human.setLocalRotation(newRot);
                } else if (binding.equals("Down")) {
                    Quaternion newRot = new Quaternion().slerp(human.getLocalRotation(),Directions.downDir, tpf*8);
                    human.setLocalRotation(newRot);
                }

                if(walk){
                    human.getControl(CharacterControl.class).setViewDirection(human.getWorldRotation().mult(Vector3f.UNIT_Z));                     
                    human.getControl(CharacterControl.class).setWalkDirection(human.getControl(CharacterControl.class).getViewDirection().multLocal(.2f));                  
                }        
                else{
                    human.getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                }
            }
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
        
        ndmd = new Node("Models");
        physicsModels = new Node("PhysicsModels");
        
        // Material
        Material mat = BBSceneManager.getInstance().getAssetManager().loadMaterial("Scenes/TestScene/TestSceneMaterial.j3m"); 
         
        // Models
        
        obj01 = BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/obj01.obj"); 
        obj01.setName("obj01");
        obj01.setMaterial(mat);
        ndmd.attachChild(obj01);
      
        obj02 = BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/obj02.obj"); 
        obj02.setName("obj02");
        obj02.setMaterial(mat);
        ndmd.attachChild(obj02);

        obj03 = BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/obj03.obj"); 
        obj03.setName("obj03");
        obj03.setMaterial(mat);
        ndmd.attachChild(obj03);

        ledder = BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/ledder.obj"); 
        ledder.setName("ledder");
        ledder.setMaterial(mat);
        ndmd.attachChild(ledder);

        // Spawn Points of Mutants
        Box box_a = new Box(Vector3f.ZERO, 0.3f, 0.3f, 0.3f);
        geom_a = new Geometry("spawn", box_a);

        mat_box = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("m_Color", ColorRGBA.Blue);
        geom_a.setMaterial(mat_box);
        ndmd.attachChild(geom_a);
               
        //Collision Shapes
        obj01_l =  (Geometry) BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/obj01_l.obj");
        obj01_l.setName("obj01_l");
        physicsModels.attachChild(obj01_l);
        
        obj02_l =  (Geometry) BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/obj02_l.obj"); 
        obj02_l.setName("obj02_l");    
        physicsModels.attachChild(obj02_l);
        
        obj03_l =  (Geometry) BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/obj03_l.obj"); 
        obj03_l.setName("obj03_l");    
        physicsModels.attachChild(obj03_l);
        
        ledder_l =  (Geometry) BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/ledder_l.obj"); 
        ledder_l.setName("ledder_l");
        physicsModels.attachChild(ledder_l);
        
       
        // Load a blender file. 
        DesktopAssetManager dsk = (DesktopAssetManager) BBSceneManager.getInstance().getAssetManager();        
        BlenderKey bk = new BlenderKey("Scenes/TestScene/test_scene_01_1.blend");
        Node nd =  (Node) dsk.loadModel(bk); 
        
        //Create empty Scene Node
        Node ndscene = new Node("Scene");
        
        // Attach boxes with names and transformations of the blend file to a Scene
        for (int j=0; j < ndmd.getChildren().size(); j++){
            Node mainNode = new Node();
            String strmd = ndmd.getChild(j).getName();
            mainNode.setName(strmd);

            Vector3f mainLocation = Vector3f.ZERO;
            boolean foundMainLocation = false;
            for (int i=0; i<nd.getChildren().size(); i++) {            
                String strndscene = nd.getChild(i).getName();
                if (strmd.length() < strndscene.length()){
                    strndscene = strndscene.substring(0, strmd.length());
                }
                if (strndscene.equals(strmd)){
                    if(!foundMainLocation){
                        mainLocation = nd.getChild(i).getWorldTransform().getTranslation();

                        mainNode.setLocalTranslation(mainLocation);
                        foundMainLocation = true;
                    }                   
                    Spatial ndGet =  ndmd.getChild(j).clone(false);
                    //System.out.println("VisTEST"+ndmd.getChild(j).getName());                
                    ndGet.setName(strndscene);
                    Transform a = new Transform();
                    a = nd.getChild(i).getWorldTransform().clone();
                    a.setTranslation(a.getTranslation().subtract(mainLocation));
                    ndGet.setLocalTransform(a);

                    mainNode.attachChild(ndGet);
                }    
            }
            ndscene.attachChild(mainNode);
        }
        BBSceneManager.getInstance().addChild(ndscene);
        
        
        // Physics: retrieval of positions
        Node physicsModelsFinal = new Node();
        for (int j=0; j<physicsModels.getChildren().size();j++){
            Node mainNode = new Node();
            String strmd = ndmd.getChild(j).getName();
            mainNode.setName(strmd);

            Vector3f mainLocation = Vector3f.ZERO;
            boolean foundMainLocation = false;
            for (int i=0; i<nd.getChildren().size(); i++) {            
                String strndscene = nd.getChild(i).getName();

                if (strmd.length() < strndscene.length()){
                    strndscene = strndscene.substring(0, strmd.length());
                }
                if (strmd.length() > strndscene.length()){
                    strmd = strmd.substring(0, strndscene.length());
                }
                if (strndscene.equals(strmd) == true){                
                    if(!foundMainLocation)
                    {
                        mainLocation = nd.getChild(i).getWorldTransform().getTranslation();                
                        mainNode.setLocalTranslation(mainLocation);
                        foundMainLocation = true;
                    }                   
                    Spatial ndGet =  physicsModels.getChild(j).clone(false);
                    //System.out.println("PHYSTEST"+physicsModels.getChild(j).getName());
                    ndGet.setName(strndscene);

                    Transform a = nd.getChild(i).getWorldTransform().clone();
                    a.setTranslation(a.getTranslation().subtract(mainLocation));
                    ndGet.setLocalTransform(a);  
                    mainNode.attachChild(ndGet);
                }
            }
        physicsModelsFinal.attachChild(mainNode);
        }

        // Clear Cache
        nd.detachAllChildren();
        nd.removeFromParent();
        dsk.clearCache();
        
        CollisionShape myComplexShape = CollisionShapeFactory.createMeshShape(physicsModelsFinal);
        physicsModelsFinal.detachAllChildren();
        RigidBodyControl worldPhysics = new RigidBodyControl(myComplexShape,0);  
        worldPhysics.createDebugShape(BBSceneManager.getInstance().getAssetManager());        
        BBPhysicsManager.getInstance().getPhysicsSpace().add(worldPhysics); 
        BBSceneManager.getInstance().getRootNode().attachChild(worldPhysics.debugShape());
        physicsModelsFinal = null;
        
    }
}
