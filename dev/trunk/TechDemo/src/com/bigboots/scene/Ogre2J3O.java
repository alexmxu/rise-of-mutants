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

import java.io.FileNotFoundException;
import org.json.simple.*;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mifth
 */
public class Ogre2J3O {

    private AssetManager assett;
    private Node sceneNode;
    private String ScenePath, sceneNAME;
    private ArrayList allNodesOriginals, allCollisionMesh;
    

    public Ogre2J3O(Node scene, String sceneName, String scenePath, AssetManager assetM) throws FileNotFoundException, IOException, ParseException {

        sceneNAME = sceneName;
        ScenePath = scenePath;
        assett = assetM;
        sceneNode = scene;



        allNodesOriginals = new ArrayList();
        allCollisionMesh = new ArrayList();

        

        
        startCompose();


        allNodesOriginals.clear();
        allCollisionMesh.clear();

    }

    private void startCompose() {

        
        // Clear Nodes
        clearNodes(sceneNode);
        
                
        // Search for Original Objects
        for (Spatial originSearch : sceneNode.getChildren()) {
            if (originSearch instanceof Node) {
                
                // Scene Entities
                if (originSearch.getName().indexOf("E") != 0 && originSearch.getName().indexOf("CAPSULE") != 0 && originSearch.getName().indexOf("BOX") != 0
                        && originSearch.getName().indexOf("CYLINDER") != 0 && originSearch.getName().indexOf("HULL") != 0 && originSearch.getName().indexOf("MESH") != 0
                        && originSearch.getName().indexOf("PLANE") != 0 && originSearch.getName().indexOf("SPHERE") != 0 && originSearch.getName().indexOf("CONE") != 0
                        && originSearch.getName().indexOf("COMPLEX") != 0) {

                    boolean sceneMeshExists = false;
                    
                    Node scEntity = (Node) originSearch;
                    String strScEntity = scEntity.getName();
                    
                    if (strScEntity.indexOf(".") > 0) strScEntity = strScEntity.substring(0, strScEntity.indexOf("."));
                    System.out.println("Searched Scene Mesh: " + strScEntity);
                    
                    if (allNodesOriginals != null) {
                     for (Object node : allNodesOriginals.toArray()) {
                        Node origNode = (Node) node;
                        if (strScEntity.equals(origNode.getName())) sceneMeshExists = true;
                     }
                    }
                    
                    if (sceneMeshExists = false) {
                    loadSceneMesh(scEntity); // Load models made in a scene
                    TangentBinormalGenerator.generate(scEntity);
                    allNodesOriginals.add(scEntity);
                  }
                } 
                
                // Entities
//                else if (originSearch.getName().indexOf("E") == 0 && originSearch.getName().indexOf("CAPSULE") != 0 && originSearch.getName().indexOf("BOX") != 0
//                        && originSearch.getName().indexOf("CYLINDER") != 0 && originSearch.getName().indexOf("HULL") != 0 && originSearch.getName().indexOf("MESH") != 0
//                        && originSearch.getName().indexOf("PLANE") != 0 && originSearch.getName().indexOf("SPHERE") != 0 && originSearch.getName().indexOf("CONE") != 0
//                        && originSearch.getName().indexOf("COMPLEX") != 0) {
//                    Node entity = (Node) originSearch;
//                    loadMesh(entity);
//                    TangentBinormalGenerator.generate(entity);
//                    allNodesOriginals.add(entity);
//                } 
                
                // Collision Meshes
                else if (originSearch.getName().indexOf("CAPSULE") == 0 || originSearch.getName().indexOf("BOX") == 0
                        || originSearch.getName().indexOf("CYLINDER") == 0 || originSearch.getName().indexOf("HULL") == 0 || originSearch.getName().indexOf("MESH") == 0
                        || originSearch.getName().indexOf("PLANE") == 0 || originSearch.getName().indexOf("SPHERE") == 0 || originSearch.getName().indexOf("CONE") == 0
                        || originSearch.getName().indexOf("COMPLEX") == 0) {
                    Node collisionNode = new Node(originSearch.getName());
                    loadSceneMesh(collisionNode);
                    allCollisionMesh.add(collisionNode);
                }
            }
        }
        //System.out.println("====================================================");



        // Searching for collision meshes
        for (Object sp : allNodesOriginals.toArray()) {
            Node ndColSearch = (Node) sp;

            for (Object sp2 : allCollisionMesh.toArray()) {
                Node ndCol = (Node) sp2;
                if (ndCol.getName().endsWith(ndColSearch.getName())) {


                    if (ndCol.getName().indexOf("CAPSULE") == 0 || ndCol.getName().indexOf("BOX") == 0
                            || ndCol.getName().indexOf("CYLINDER") == 0 || ndCol.getName().indexOf("HULL") == 0 || ndCol.getName().indexOf("MESH") == 0
                            || ndCol.getName().indexOf("PLANE") == 0 || ndCol.getName().indexOf("SPHERE") == 0 || ndCol.getName().indexOf("CONE") == 0
                            || ndCol.getName().indexOf("COMPLEX") == 0) {
                        ndColSearch.setUserData("PhysicsCollision", ndCol.getName());
                    }
                }
            }
        }

        
        

        // Saving scene with empty Nodes to j3o
        Node sceneSave = new Node(sceneNAME);
        for (Object sp : sceneNode.getChildren()) {
            Node ndGet = (Node) sp;
            if (ndGet.getName().indexOf("CAPSULE") != 0 && ndGet.getName().indexOf("BOX") != 0
                    && ndGet.getName().indexOf("CYLINDER") != 0 && ndGet.getName().indexOf("HULL") != 0 && ndGet.getName().indexOf("MESH") != 0
                    && ndGet.getName().indexOf("PLANE") != 0 && ndGet.getName().indexOf("SPHERE") != 0 && ndGet.getName().indexOf("CONE") != 0
                    && ndGet.getName().indexOf("COMPLEX") != 0) {
                Node ndSave = new Node(ndGet.getName());
                ndSave.setLocalTransform(ndGet.getLocalTransform());
                sceneSave.attachChild(ndSave);
            }
        }
        binaryExport(ScenePath + "/" + sceneSave.getName(), sceneSave);


        // Saving scene Meshes
        for (Object sp : allNodesOriginals) {
            Node ndSave = (Node) sp;
            ndSave.removeFromParent();
            ndSave.setLocalTransform(new Transform());
            binaryExport(ScenePath + "/Models/" + ndSave.getName(), ndSave);
        }

        // Saving collision Meshes
        for (Object sp : allCollisionMesh) {
            Node ndSave = (Node) sp;
            ndSave.setLocalTransform(new Transform());
            binaryExport(ScenePath + "/CollisionMeshes/" + ndSave.getName(), ndSave);
        }
        
    }

    
        // Clear Nodes from unused stuff        
        private void clearNodes(Node nodeClear){

        for (Spatial nodeSearch : nodeClear.getChildren()) {
            if (nodeSearch instanceof Node) {
                Node node = (Node) nodeSearch;
                node.detachAllChildren();
            }
        }
            
        }
        
        
    
    private void binaryExport(String name, Node saveNode) {

        String str = new String("assets/J3O/" + name + ".j3o");

        // convert to / for windows
        if (File.separatorChar == '\\') {
            str = str.replace('\\', '/');
        }
        if (!str.endsWith("/")) {
            str += "/";
        }

        File MaFile = new File(str);
        MaFile.setWritable(true);
        MaFile.canWrite();
        MaFile.canRead();


        try {
            BinaryExporter exporter = BinaryExporter.getInstance();
            exporter.save(saveNode, MaFile);
//            BinaryExporter.getInstance().save(saveNode, MaFile);
        } catch (IOException ex) {
            System.out.println("Baddddd Saveee");

        }

    }

    
    // Load Entity
    private void loadSceneMesh(Node entNode) throws FileNotFoundException, IOException, ParseException {

        // Load a Mesh. 
        DesktopAssetManager dskMesh = (DesktopAssetManager) assett;  
        ModelKey mKey = new ModelKey(ScenePath + "/ogre/" + entNode.getName() + ".mesh.xml");
        Node ndMesh =  (Node) dskMesh.loadModel(mKey); 
        
        
        BBMaterialComposer composer = new BBMaterialComposer(ndMesh, assett, sceneNAME);
        
        Node thisNode = entNode;
        
    }


}