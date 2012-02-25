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
package com.bigboots.scene;


import com.bigboots.BBWorldManager;
import com.bigboots.FixedTangentBinormalGenerator;
import com.bigboots.components.BBCollisionComponent;
import com.bigboots.components.BBCollisionComponent.ShapeType;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.components.BBEntity;
import com.bigboots.components.BBNodeComponent;
import com.bigboots.physics.BBPhysicsManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.*;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.scene.*;
import com.jme3.util.TangentBinormalGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author mifth
 */
public class BBSceneComposer {

    private AssetManager assett;
    private Node sceneNode;    
    private String pathDir;
    private ArrayList alNodesOriginals, alEntitiesOriginals, alEntitiesClones; //alCollisionMesh


    public  BBSceneComposer (Node scene, AssetManager assetM) {
        

        assett = assetM;
        sceneNode = scene;
        pathDir = "J3O/";

        alNodesOriginals = new ArrayList();
        alEntitiesOriginals = new ArrayList();
        alEntitiesClones = new ArrayList();        
        

        startCompose();
        
    }


    private void startCompose() {
                 
        // Search for Original Objects
        for (Spatial originSearch : sceneNode.getChildren()) {
            if (originSearch instanceof Node && originSearch.getName().indexOf(".") < 0){
                    Node alNd = (Node) originSearch;  
                    alNodesOriginals.add(alNd);
            }         
        }

        
       // Search for Original Objects with "." name
       for (Spatial spatNode : sceneNode.getChildren()) {
           
           if (spatNode instanceof Node && spatNode.getName().indexOf(".") > 0) {
               boolean cloneFound = false; // Check for existing Original Object
               Node ndNode = (Node) spatNode;
               String strCompare = ndNode.getName().toString();
               strCompare = strCompare.substring(0, ndNode.getName().indexOf("."));
               for (Object nodeTemp : alNodesOriginals.toArray()) {
                  Node nodeSearch = (Node) nodeTemp;
                  if (nodeSearch.getName().equals(strCompare)) {
                      cloneFound = true;
                  }
               }
             
              if (cloneFound == false) {
                  ndNode.setName(strCompare);
                    if (ndNode.getName().indexOf("E") != 0){
                    alNodesOriginals.add(ndNode);
                } else if (ndNode.getName().indexOf("E") == 0){
                    alNodesOriginals.add(ndNode);
                }
              }      
           }         
        }          
        
        
        // Creating Entities
       for (Object sp : alNodesOriginals.toArray()) {
           Node ndEnt = (Node) sp;
           
           
           // Load j3o Model
           Node loadedNode = loadModelNow(pathDir + "Models/" + ndEnt.getName()  + ".j3o");
           loadedNode.setLocalTransform(ndEnt.getLocalTransform());
           loadedNode.setName(ndEnt.getName());
           
           //Create an Entity from an existing node
           BBEntity mEntity = new BBEntity(loadedNode.getName(), loadedNode);
           
           //Add a transform component to attach it to the scene graph
           BBNodeComponent pnode = mEntity.addComponent(CompType.NODE);

           //Load it in the way to attach Geometry to the entity node
           mEntity.loadModel("");

           // Fixing some coordinates
           mEntity.getComponent(BBNodeComponent.class).setLocalTransform(mEntity.getComponent(BBNodeComponent.class).getChild(0).getWorldTransform());
           mEntity.getComponent(BBNodeComponent.class).getChild(0).setLocalTransform(new Transform());

           
          
           BBWorldManager.getInstance().addEntity(mEntity);
           //System.out.println("Entity Created " + ndColSearch.getName());
           
           // Searching for collision meshes
             if (loadedNode.getUserData("PhysicsCollision") != null) {
                 
               String conName = loadedNode.getUserData("PhysicsCollision").toString();


                   
                   ShapeType shType = null;
                   if (conName.indexOf("CAPSULE") == 0) shType = ShapeType.CAPSULE;
                   else if (conName.indexOf("BOX") == 0) shType = ShapeType.BOX;
                   else if (conName.indexOf("CYLINDER") == 0) shType = ShapeType.CYLINDER;
                   else if (conName.indexOf("HULL") == 0) shType = ShapeType.HULL;
                   else if (conName.indexOf("MESH") == 0) shType = ShapeType.MESH;
                   else if (conName.indexOf("PLANE") == 0) shType = ShapeType.PLANE;
                   else if (conName.indexOf("SPHERE") == 0) shType = ShapeType.SPHERE;
                   else if (conName.indexOf("CONE") == 0) shType = ShapeType.CONE;
                   else if (conName.indexOf("COMPLEX") == 0) shType = ShapeType.COMPLEX;
                   
                   
                    // Creating Collision Mesh
                    Node ndCol = loadModelNow(pathDir + "CollisionMeshes/" + conName + ".j3o");
                    ndCol.setLocalRotation(new Quaternion());
                    CollisionShape colShape = BBPhysicsManager.getInstance().createPhysicShape(shType, ndCol, 1, 1);                   
                    colShape.setScale(mEntity.getComponent(BBNodeComponent.class).getLocalScale());
                    RigidBodyControl worldPhysics = new RigidBodyControl(colShape,0);

                    // Setting ShapeType of the Entity
                    mEntity.addComponent(CompType.COLSHAPE);
                    mEntity.getComponent(BBCollisionComponent.class).setShapeType(shType);
                    //mEntity.getComponent(BBCollisionComponent.class).attachShape(colShape);
                    
                    pnode.addControl(worldPhysics);
                    BBPhysicsManager.getInstance().getPhysicsSpace().add(mEntity.getComponent(BBNodeComponent.class)); 
              }
           
           alEntitiesOriginals.add(mEntity);           
           //Attach it to the RootNode
           mEntity.attachToRoot(); 

           
       }
       


       
       
       //Cloning of Entities (shared meshes and Materials)
       for (Spatial spatNode : sceneNode.getChildren()) {
           
           if (spatNode instanceof Node && spatNode.getName().indexOf(".") > 0) {
               boolean cloneFound = false; // Check for existing Original Object
               Node ndNode = (Node) spatNode;
               String strCompare = ndNode.getName().toString();
               strCompare = strCompare.substring(0, ndNode.getName().indexOf("."));
               for (Object objTemp : alEntitiesOriginals.toArray()) {
                   BBEntity entSearch = (BBEntity) objTemp;
                   if (entSearch.getObjectName().equals(strCompare)) {
                      cloneFound = true; 
                      //Clone node of an existing Entity                     
                      BBEntity mCloneEntity = entSearch.clone(ndNode.getName());
                      //Add a transform component to attach it to the scene graph
                      mCloneEntity.getComponent(BBNodeComponent.class).setLocalTransform(ndNode.getLocalTransform());

                      if (entSearch.getComponent(BBNodeComponent.class).getControl(RigidBodyControl.class) != null 
                              && entSearch.getComponent(BBCollisionComponent.class) != null) {
                          
                        RigidBodyControl rgBody = (RigidBodyControl) entSearch.getComponent(BBNodeComponent.class).
                        getControl(RigidBodyControl.class).cloneForSpatial(mCloneEntity.getComponent(BBNodeComponent.class));
                        
                        entSearch.getComponent(BBCollisionComponent.class).getShape();
                        ShapeType origShape = entSearch.getComponent(BBCollisionComponent.class).getShapeType();

                        
                        // Create collision mesh again if Enitities have different scale
                        if (mCloneEntity.getComponent(BBNodeComponent.class).getLocalScale().
                        equals(entSearch.getComponent(BBNodeComponent.class).getLocalScale()) != true) {
                        
                        boolean foundShape = false; // search for the same shape    
                        
                        // Search for collisions from already cloned Entities 
                        if (alEntitiesClones != null){
                        for(Object sp2 : alEntitiesClones.toArray()) {
                            BBEntity mEntityConed = (BBEntity) sp2;
                            
                            if (mEntityConed.getObjectName().substring(0, mEntityConed.getObjectName().indexOf(".")).
                            equals(mCloneEntity.getObjectName().substring(0, mCloneEntity.getObjectName().indexOf("."))) == true
                                    && mEntityConed.getComponent(BBNodeComponent.class).getLocalScale().
                                       equals(mCloneEntity.getComponent(BBNodeComponent.class).getLocalScale()) == true) {

                               CollisionShape colShapeCloned = mEntityConed.getComponent(BBNodeComponent.class).getControl(RigidBodyControl.class).getCollisionShape();
                                rgBody.setCollisionShape(colShapeCloned);
                                foundShape = true;
                                //System.out.println("Cloned Shape from " + mEntityConed + " to " + mCloneEntity);
                            }                         
                           }
                          }
                         
                         // If Cloned analog is not found... creates a new collision mesh
                         if (foundShape == false) {   
                        Node ndCol = loadModelNow(pathDir + "CollisionMeshes/" + entSearch.getComponent(BBNodeComponent.class).getChild(0).getUserData("PhysicsCollision").toString() + ".j3o");                        
                        if (ndCol.getName().endsWith(strCompare)){
                            
                        CollisionShape colShape = BBPhysicsManager.getInstance().createPhysicShape(origShape, ndCol, 1, 1);     
                        colShape.setScale(ndNode.getLocalScale());
                        rgBody.setCollisionShape(colShape);

                        alEntitiesClones.add(mCloneEntity); // add a clone for collisinShape instancing
                              }
                           }
                        }
                        mCloneEntity.getComponent(BBNodeComponent.class).addControl(rgBody);
                        BBPhysicsManager.getInstance().getPhysicsSpace().add(mCloneEntity.getComponent(BBNodeComponent.class));   

                        // Setting ShapeType for Cloned Entity
                        mCloneEntity.addComponent(CompType.COLSHAPE);
                        mCloneEntity.getComponent(BBCollisionComponent.class).setShapeType(entSearch.getComponent(BBCollisionComponent.class).getShapeType());                        
                        
                      } 
                      //Attach it to the RootNode
                      mCloneEntity.attachToRoot();

                      BBWorldManager.getInstance().addEntity(mCloneEntity);
                      //System.out.println("cccccccccccccccc Cloninig Entity: " + mCloneEntity.getObjectName()+" From entity : "+entSearch.getObjectName());
                  }
               }
           }
       }
           
      
    }
   
    
    
    private Node loadModelNow (String Path){
 
        // Load a blender file Scene. 
//        DesktopAssetManager dsk = (DesktopAssetManager) asm;        
        ModelKey bk = new ModelKey(Path);
        Node nd =  (Node) assett.loadModel(bk);               

//        //Clear Blend File
//        nd.detachAllChildren();
//        nd.removeFromParent();
//        nd = null;
//        dsk.clearCache(); 
    
        return nd;
    }    
    
}