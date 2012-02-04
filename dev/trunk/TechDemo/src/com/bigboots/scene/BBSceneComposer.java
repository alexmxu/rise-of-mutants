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
    private String dirbase, levelFold, dirlevel, entFld, entPath;
    private ArrayList alMaterials, alNodesOriginals, alCollisionMesh, alEntitiesOriginals, alEntitiesClones;
    private boolean isBlenderOrOgre;

    public  BBSceneComposer (Node scene, String entityFolder, String levelFolder, String dirTexBase, String dirTexLevel, AssetManager assetM) {
        
        isBlenderOrOgre = true;
        
        entFld = entityFolder; 
        levelFold = levelFolder;
        assett = assetM;
        sceneNode = scene;
        dirbase = dirTexBase;    
        dirlevel = dirTexLevel;

        alMaterials = new ArrayList();
        alNodesOriginals = new ArrayList();
        alCollisionMesh = new ArrayList();
        alEntitiesOriginals = new ArrayList();
        alEntitiesClones = new ArrayList();

        startCompose();
        
        alMaterials.clear();
        alNodesOriginals.clear();
        alCollisionMesh.clear();
        alEntitiesOriginals.clear();
        alEntitiesClones.clear();

        
    }


    private void startCompose() {
                 
        // Search for Original Objects
        for (Spatial originSearch : sceneNode.getChildren()) {        
            if (originSearch instanceof Node && originSearch.getName().indexOf(".") < 0){
                if (originSearch.getName().indexOf("E") != 0 && originSearch.getName().indexOf("CAPSULE") != 0 && originSearch.getName().indexOf("BOX") != 0  
                && originSearch.getName().indexOf("CYLINDER") != 0 && originSearch.getName().indexOf("HULL") != 0 && originSearch.getName().indexOf("MESH") != 0
                && originSearch.getName().indexOf("PLANE") != 0 && originSearch.getName().indexOf("SPHERE") != 0 && originSearch.getName().indexOf("CONE") != 0 
                && originSearch.getName().indexOf("COMPLEX") != 0 ){
                    Node alNd = (Node) originSearch;  
                    replaceMeshWithOgre(alNd, levelFold);
                    composeMaterial(alNd, null);
                    TangentBinormalGenerator.generate(alNd);
                    alNodesOriginals.add(alNd);
                    isBlenderOrOgre = true;
                } else if (originSearch.getName().indexOf("E") == 0 && originSearch.getName().indexOf("CAPSULE") != 0 && originSearch.getName().indexOf("BOX") != 0  
                && originSearch.getName().indexOf("CYLINDER") != 0 && originSearch.getName().indexOf("HULL") != 0 && originSearch.getName().indexOf("MESH") != 0
                && originSearch.getName().indexOf("PLANE") != 0 && originSearch.getName().indexOf("SPHERE") != 0 && originSearch.getName().indexOf("CONE") != 0 
                && originSearch.getName().indexOf("COMPLEX") != 0 ){
                    Node entNd = (Node) originSearch;
                    loadEntity(entFld, entNd);
                    TangentBinormalGenerator.generate(entNd);
                    alNodesOriginals.add(entNd);
                    isBlenderOrOgre = true;
                }  else if (originSearch.getName().indexOf("CAPSULE") == 0 || originSearch.getName().indexOf("BOX") == 0  
                || originSearch.getName().indexOf("CYLINDER") == 0 || originSearch.getName().indexOf("HULL") == 0 || originSearch.getName().indexOf("MESH") == 0
                || originSearch.getName().indexOf("PLANE") == 0 || originSearch.getName().indexOf("SPHERE") == 0 || originSearch.getName().indexOf("CONE") == 0 
                || originSearch.getName().indexOf("COMPLEX") == 0 ){
                    Node entNd = (Node) originSearch;
                    alCollisionMesh.add(entNd);
                } 
            }
        }
        //System.out.println("====================================================");
        
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
                    replaceMeshWithOgre(ndNode, levelFold);
                    composeMaterial(ndNode, null);
                    TangentBinormalGenerator.generate(ndNode);
                    alNodesOriginals.add(ndNode);
                    isBlenderOrOgre = true;
                } else if (ndNode.getName().indexOf("E") == 0){
                    loadEntity(entFld, ndNode);
                    TangentBinormalGenerator.generate(ndNode);
                    alNodesOriginals.add(ndNode);
                    isBlenderOrOgre = true;
                }
              }      
           }         
        }  
       
       

       // Creating Entities
       for (Object sp : alNodesOriginals.toArray()) {
           Node ndColSearch = (Node) sp;
           //Create an Entity from an existing node
           BBEntity mEntity = new BBEntity(ndColSearch.getName(), ndColSearch);
           
           //Add a transform component to attach it to the scene graph
           BBNodeComponent pnode = mEntity.addComponent(CompType.NODE);

           //Load it in the way to attach Geometry to the entity node
           mEntity.loadModel("");

           // Fixing some coordinates
           mEntity.getComponent(BBNodeComponent.class).setLocalTransform(mEntity.getComponent(BBNodeComponent.class).getChild(0).getWorldTransform());
           mEntity.getComponent(BBNodeComponent.class).getChild(0).setLocalTransform(new Transform());

           
           //Attach it to the RootNode
           mEntity.attachToRoot();           
           BBWorldManager.getInstance().addEntity(mEntity);
           //System.out.println("Entity Created " + ndColSearch.getName());
           
           // Searching for collision meshes
           for (Object sp2 : alCollisionMesh.toArray()) {
               Node ndCol = (Node) sp2;
               if (ndCol.getName().endsWith(ndColSearch.getName())){
                   
                   ShapeType shType = null;
                   if (ndCol.getName().indexOf("CAPSULE") == 0) shType = ShapeType.CAPSULE;
                   else if (ndCol.getName().indexOf("BOX") == 0) shType = ShapeType.BOX;
                   else if (ndCol.getName().indexOf("CYLINDER") == 0) shType = ShapeType.CYLINDER;
                   else if (ndCol.getName().indexOf("HULL") == 0) shType = ShapeType.HULL;
                   else if (ndCol.getName().indexOf("MESH") == 0) shType = ShapeType.MESH;
                   else if (ndCol.getName().indexOf("PLANE") == 0) shType = ShapeType.PLANE;
                   else if (ndCol.getName().indexOf("SPHERE") == 0) shType = ShapeType.SPHERE;
                   else if (ndCol.getName().indexOf("CONE") == 0) shType = ShapeType.CONE;
                   else if (ndCol.getName().indexOf("COMPLEX") == 0) shType = ShapeType.COMPLEX;
                   
                   
                   // Creating Collision Mesh
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
           }
           alEntitiesOriginals.add(mEntity);
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
                        for (Object sp3 : alCollisionMesh.toArray()) {                        
                        Node ndCol = (Node) sp3;
                        if (ndCol.getName().endsWith(strCompare)){
                            
                        CollisionShape colShape = BBPhysicsManager.getInstance().createPhysicShape(origShape, ndCol, 1, 1);     
                        colShape.setScale(ndNode.getLocalScale());
                        rgBody.setCollisionShape(colShape);

                        alEntitiesClones.add(mCloneEntity); // add a clone for collisinShape instancing
                              }
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
           
        //System.out.println(alMaterials.size() + " - QUANTITY OF BASE MATERIALS");   
    }
   

    // Replace a mesh of blender with a mesh of ogre, because blender does not support texCoord2
    // texCoord2 is needed for Lightmaps  
    // I hope Core Devs will add texCoord2 support for blender soon.
    private void replaceMeshWithOgre(Node nd, String path) {

        Node nodeOrigin = nd;   
        //System.out.println(nodeOrigin.getName() + " OGRE REPLACING NODE");

        if (nodeOrigin.getChildren().size() > 0) {
            String strPath = path + "/" + "ogre" + "/" +  nodeOrigin.getName() + ".mesh.xml";
            //strPath.replaceAll("/".toString(), "/");
            File fileOgreCheck = new File("assets/" + strPath);
            
            if (fileOgreCheck.exists() == true) {
            isBlenderOrOgre = false;   
            ModelKey mkOgre = new ModelKey(strPath);           
            Node nodeOgre = (Node) assett.loadModel(mkOgre);
            List<Spatial> listOgre = nodeOgre.getChildren();
            //System.out.println(nodeOgre.getName() + " ogre node");

            int index = 0;
            for (int i=listOgre.size()-1; i>=0; i--) {
               Geometry geoTemp = (Geometry) nodeOrigin.getChild(index);
               Geometry geoTempOgre = (Geometry) listOgre.get(i);
               Material matTemp = geoTemp.getMaterial();
               //System.out.println("REPLACE MESH Blender " + geoTemp.getName() + " AND Ogre " + geoTempOgre.getName());
               geoTemp.setMesh(geoTempOgre.getMesh());

               //  the line below can check for texCoord2
               // System.out.println(geoTempOgre.getMesh().getBuffer(VertexBuffer.Type.TexCoord2).toString() + "UUUVVV");                

               index += 1;
               }
            }
        }
    }  
  
    // Load Entity
    private void loadEntity(String dirEntity, Node emptyNode) {
        Node fullNode = emptyNode;   
        //System.out.println("ooooooooo LOAD entity Dir : "+dirEntity+" with Node "+emptyNode.getName());
        File dir = new File(dirEntity);
        File[] a = dir.listFiles();

        for (File f : a) {
            if (f.isDirectory()) {
                // Recursive search
                //System.out.println("****** CHECKing Dir : "+f.getName());
                String recursDir = dirEntity + "/" + f.getName();
                loadEntity(recursDir, emptyNode);
            }else if (f.getName().indexOf(emptyNode.getName()) >= 0 && f.getName().endsWith(".blend")) {
                String strF = dirEntity + "/" + f.getName();
                //System.out.println("========>>FOUND ENTITY :: " + strF);

                // Load a blender file. 
                DesktopAssetManager dsk = (DesktopAssetManager) assett;        
                ModelKey bk = new ModelKey(strF.substring(7));
                Node nodeEnt =  (Node) dsk.loadModel(bk);                 
                
                for (Spatial sp : nodeEnt.getChildren()) {
                    Node ndThis = (Node) sp;
                    
                // Search for Collision mesh inside of an Entity    
                if (ndThis.getName().indexOf("CAPSULE") == 0 || ndThis.getName().indexOf("BOX") == 0  
                || ndThis.getName().indexOf("CYLINDER") == 0 || ndThis.getName().indexOf("HULL") == 0 || ndThis.getName().indexOf("MESH") == 0
                || ndThis.getName().indexOf("PLANE") == 0 || ndThis.getName().indexOf("SPHERE") == 0 || ndThis.getName().indexOf("CONE") == 0 
                || ndThis.getName().indexOf("COMPLEX") == 0 ){
                    nodeEnt.detachChild(ndThis);
                    alCollisionMesh.add(ndThis);
                } else fullNode.attachChild(ndThis);
                }
                
                //Search for Ogre Meshes and Path for Material Composer
                File[] flOgre = f.getParentFile().listFiles();
                //System.out.println(flOgre + "wwwwwwwww");
                for (File fPath : flOgre) {
                    if (fPath.isDirectory() && fPath.toString().endsWith("ogre")) {
                        for (Spatial sp2 : fullNode.getChildren()) {
                        Node ndToOgre = (Node) sp2;    
                        replaceMeshWithOgre(ndToOgre, f.getParentFile().toString().substring(7));
                    }
                  }      
                }
        
                //Clear Blend File
                nodeEnt.detachAllChildren();
                nodeEnt = null;
                dsk.clearCache();
                
                //System.out.println("****** GET PArent File : "+f.getParentFile().toString());
                composeMaterial(fullNode, dirEntity);  
            }
        }
    }   
        
        
    //Generate a material for every geometry
    private void composeMaterial(Node nd2, String entityPath) {
  
    entPath = entityPath; // Path for Entity Textures
    Node ndMat = nd2; 
  
    //Search for geometries        
    SceneGraphVisitor sgv = new SceneGraphVisitor() {
        public void visit(Spatial spatial) {
            //System.out.println(spatial + " Visited Spatial");
            if (spatial instanceof Geometry) {
                Geometry geom_sc = (Geometry) spatial;
                if (alMaterials.isEmpty() == true || entPath != null){
                    setGeneration(geom_sc, entPath);
                }
                else{
                    //Generate Material
                    for (Object matTemp : alMaterials.toArray()) {
                        Material matSearch = (Material) matTemp;
                        if (geom_sc.getMaterial().getName().equals(matSearch.getName()) && entPath == null){
                            geom_sc.setMaterial(matSearch);
                        }
                        else {
                            setGeneration(geom_sc, entPath); 
                        break;
                        }  
                    }
                }   
            }
        }
        private void setGeneration(Geometry geo, String entityPath2) {
            String entPath3 = entityPath2; // Path for Entity Textures
            Geometry geomGen = geo;

            BBMaterialComposer matComp = new BBMaterialComposer(geomGen, dirbase, dirlevel, assett, isBlenderOrOgre);
            //System.out.println("Composing Material: " + geomGen.getMaterial().getName() + " for Geometry " + geomGen.getName());
            matComp.generateMaterial(entPath3);
            if (entPath3 == null) alMaterials.add(geomGen.getMaterial());

        }
    };
 
    ndMat.depthFirstTraversal(sgv);  
    //  sc.breadthFirstTraversal(sgv);     
    }        
        
}