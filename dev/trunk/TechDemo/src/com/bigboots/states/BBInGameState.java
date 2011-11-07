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

import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.physics.BBPhysicsManager;
import com.jme3.animation.AnimChannel;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;

//for player
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.scene.Node;
import com.jme3.asset.BlenderKey;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBInGameState extends BBAbstractState {
    
        
    @Override
    public void initialize(BBEngineSystem engineSystem) {
        super.initialize(engineSystem);

        BBPhysicsManager.getInstance().init();
        
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
        
        sinbadOut = new Node("PLAYER");
        Spatial humanStalker = BBSceneManager.getInstance().loadSpatial("Scenes/TestScene/character.mesh.xml");
        humanStalker.setName("PLAYER");
        humanStalker.setLocalTranslation(0, -.85f, 0);
        sinbadOut.attachChild(humanStalker);
        sinbadOut.setLocalTranslation(new Vector3f(0, 30, 0));
        sinbadOut.scale(2);     
        
        //Physics
        BBPhysicsManager.getInstance().getPhysicsSpace().addAll(humanStalker);
        
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(25, 10, 0));
        //Rotate the camNode to look at the target:
        camNode.lookAt(humanStalker.getLocalTranslation(), Vector3f.UNIT_Y);
        //Attach the camNode to the target:
        sinbadOut.attachChild(camNode);
        
        
        //Set up animation
        AnimControl control = humanStalker.getControl(AnimControl.class);
        // PlayerChannel later refered to by player.getControl(AnimControl.class).getChannel(0);
        AnimChannel playerChannel = control.createChannel();
        playerChannel.setAnim("run_01");
        //    playerChannel.setAnim("RunBase");
        playerChannel.setLoopMode(LoopMode.Cycle);
        playerChannel.setSpeed(1);    

        // CapWe also put the player in its starting position.
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(.7f, 2f, 1);
        CharacterControl pControler = new CharacterControl(capsuleShape, .05f);

        pControler.setJumpSpeed(35);
        pControler.setFallSpeed(40);
        pControler.setGravity(35);
        //pControler.setPhysicsLocation(player.getWorldTranslation());//new Vector3f(0, 10, 0));
        pControler.setUseViewDirection(true);   
        /*
        Spatial debugColl = pControler.createDebugShape(assetManager);
        rootNode.attachChild(debugColl);    
        debugColl.setLocalTranslation(0, 2, 0);
        */
        // Add phys. controller to player.
        //sinbadModel.addControl(pControler);
        sinbadOut.addControl(pControler);
        
        loadScene();
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        sinbadOut.updateLogicalState(tpf);
        sinbadOut.updateGeometricState();
    }
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
     Node sinbadOut;
    public void loadScene(){
        
        Node ndmd = new Node("Models");
        Node physicsModels = new Node("PhysicsModels");
        
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
                    System.out.println("VisTEST"+ndmd.getChild(j).getName());                
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
                    System.out.println("PHYSTEST"+physicsModels.getChild(j).getName());
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
        
    }

}
