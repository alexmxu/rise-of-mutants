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


import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.material.*;
import com.jme3.scene.*;
import com.jme3.texture.Texture;
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
    private ArrayList alMaterials, alNodesBase;
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
        alNodesBase = new ArrayList();

        startCompose();
    }

  
  
    private void startCompose() {
        
        // Search for Clones and Original Objects
        for (Spatial origin : sceneNode.getChildren()) {        
            if (origin instanceof Node && origin.getName().indexOf(".") < 0){
                if (origin.getName().indexOf("E") != 0){
                    Node alNd = (Node) origin;  
                    replaceMeshWithOgre(alNd, levelFold);
                    composeMaterial(alNd, null);
                    alNodesBase.add(alNd);
                    isBlenderOrOgre = true;
                } else if (origin.getName().indexOf("E") == 0){
                    Node entNd = (Node) origin;
                    loadEntity(entFld, entNd);
                    alNodesBase.add(entNd);
                    isBlenderOrOgre = true;
                }   
            }
        }
        System.out.println("====================================================");
        
        // Cloning of Objects (shared meshes and Materials)
       for (Spatial spatNode : sceneNode.getChildren()) {
           
           if (spatNode instanceof Node && spatNode.getName().indexOf(".") > 0) {
               boolean cloneFound = false; // Check for existing Original Object
               Node ndNode = (Node) spatNode;
               String strCompare = ndNode.getName().toString();
               strCompare = strCompare.substring(0, ndNode.getName().indexOf("."));
               for (Object nodeTemp : alNodesBase.toArray()) {
                  Node nodeSearch = (Node) nodeTemp;
                  if (nodeSearch.getName().equals(strCompare)) {
                      cloneFound = true;
                      ndNode.detachAllChildren();
                      for (Spatial sp : nodeSearch.getChildren()) {
                          if (sp instanceof Geometry) {
                              Geometry geo = (Geometry) sp.clone(false);
                              ndNode.attachChild(geo);
                              System.out.println("++++++++++ Attached " + geo + " to " + ndNode);
                          }
                          else if (sp instanceof Node) {
                              Node nd = (Node) sp.clone(false);
                              ndNode.attachChild(nd);
                          }
                      }
                  }
               }
             
              if (cloneFound == false) {
                  ndNode.setName(strCompare);
                    if (ndNode.getName().indexOf("E") != 0){
                    replaceMeshWithOgre(ndNode, levelFold);
                    composeMaterial(ndNode, null);
                    alNodesBase.add(ndNode);
                    isBlenderOrOgre = true;
                } else if (ndNode.getName().indexOf("E") == 0){
                    loadEntity(entFld, ndNode);
                    alNodesBase.add(ndNode);
                    isBlenderOrOgre = true;
                }
              }      
           }         
        }
        System.out.println(alMaterials.size() + " - QUANTITY OF BASE MATERIALS");   
    }
  

    // Replace a mesh of blender with a mesh of ogre, because blender does not support texCoord2
    // texCoord2 is neede for Lightmaps  
    // I hope Core Devs will add texCoord2 support for blender soon.
    private void replaceMeshWithOgre(Node nd, String path) {

        Node nodeOrigin = nd;   
        System.out.println(nodeOrigin.getName() + " scene node");

        if (nodeOrigin.getChildren().size() > 0) {
            String strPath = path + "/" + "ogre" + "/" +  nodeOrigin.getName() + ".mesh.xml";
            //strPath.replaceAll("/".toString(), "/");
            File fileOgreCheck = new File("assets/" + strPath);
            
            if (fileOgreCheck.exists() == true) {
            isBlenderOrOgre = false;   
            ModelKey mkOgre = new ModelKey(strPath);           
            //System.out.println(mkOgre + " "  + "yyyyyyyyyyyyyyy");
            Node nodeOgre = (Node) assett.loadModel(mkOgre);
            List<Spatial> listOgre = nodeOgre.getChildren();
            System.out.println(nodeOgre.getName() + " ogre node");

            int index = 0;
            for (int i=listOgre.size()-1; i>=0; i--) {
               Geometry geoTemp = (Geometry) nodeOrigin.getChild(index);
               Geometry geoTempOgre = (Geometry) listOgre.get(i);
               Material matTemp = geoTemp.getMaterial();
               System.out.println("REPLACE MESH Blender " + geoTemp.getName() + " AND Ogre " + geoTempOgre.getName());
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
        System.out.println("ooooooooo LOAD entity Dir : "+dirEntity+" with Node "+emptyNode.getName());
        File dir = new File(dirEntity);
        File[] a = dir.listFiles();

        for (File f : a) {
            if (f.isDirectory()) {
                // Recursive search
                System.out.println("****** CHECKing Dir : "+f.getName());
                String recursDir = dirEntity + "/" + f.getName();
                loadEntity(recursDir, emptyNode);
            }else if (f.getName().indexOf(emptyNode.getName()) >= 0 && f.getName().endsWith(".blend")) {
                //String strF = f.toString();
                //strF = strF.replaceAll("/", Matcher.quoteReplacement(strF));
                String strF = dirEntity + "/" + f.getName();
                System.out.println("========>>FOUND ENTITY :: " + strF);

                Node nodeEnt = (Node) assett.loadModel(strF.substring(7));
                for (Spatial sp : nodeEnt.getChildren()) {
                    Node ndThis = (Node) sp;
                    fullNode.attachChild(ndThis);
                }
                //Search for Ogre Meshes and Path for Material Composer
                File[] flOgre = f.getParentFile().listFiles();
                for (File fPath : flOgre) {
                    if (fPath.isDirectory() && fPath.toString().endsWith("/" + "ogre")) {
                        replaceMeshWithOgre(fullNode, strF);
                    }
                }
                System.out.println("****** GET PArent File : "+f.getParentFile().toString());
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
            System.out.println(spatial + " Visited Spatial");
            if (spatial instanceof Geometry) {
                Geometry geom_sc = (Geometry) spatial;
                if (alMaterials.isEmpty() == true){
                    setGeneration(geom_sc, entPath);
                }
                else{
                    //Generate Material
                    for (Object matTemp : alMaterials.toArray()) {
                        Material matSearch = (Material) matTemp;
                        if (geom_sc.getMaterial().getName().equals(matSearch.getName())){
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
            System.out.println("Composing Material: " + geomGen.getMaterial().getName() + " for Geometry " + geomGen.getName());
            matComp.generateMaterial(entPath3);
            alMaterials.add(geomGen.getMaterial());

            // Test lighting. It will be removed soon.        
            TextureKey tkk = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_low.png", false);
            tkk.setGenerateMips(true);
            Texture ibl = assett.loadTexture(tkk);
            geomGen.getMaterial().setTexture("IblMap_Simple", ibl); 
        }
    };
 
    ndMat.depthFirstTraversal(sgv);  
    //  sc.breadthFirstTraversal(sgv);     
    }        
        
}
