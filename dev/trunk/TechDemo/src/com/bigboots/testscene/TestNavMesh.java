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

import com.bigboots.BBWorldManager;
import com.bigboots.ai.navmesh.NavMesh;
import com.bigboots.core.BBSceneManager;
import com.bigboots.physics.BBPhysicsManager;
import com.bigboots.ai.util.NavMeshGenerator;

import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import java.util.LinkedList;
import java.util.List;
import jme3tools.optimize.GeometryBatchFactory;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class TestNavMesh extends BBSimpleApplication{
    //The main function call to init the app
    public static void main(String[] args) {
        TestNavMesh app = new TestNavMesh();
        app.run();
    }   
    
    protected TerrainQuad terrain;
    
    @Override
    public void simpleInitialize(){
        super.simpleInitialize();
        
        //Swithing physic on
        BBPhysicsManager.getInstance().init(engineSystem);
        
        setupTerrain();
        
        Node world = new Node("world");
        world.attachChild(terrain); 
        //Attach to scene root node
        BBSceneManager.getInstance().addChild(world); 
        /*
        NavMeshGenerator generator = new NavMeshGenerator();
        NavMesh navMesh = new NavMesh();
        
        Mesh mesh = new Mesh();
        List<Geometry> geomList = BBWorldManager.getInstance().findGeometries(world, new LinkedList<Geometry>());
        GeometryBatchFactory.mergeGeometries(geomList, mesh);
        Mesh optiMesh = generator.optimize(mesh);
        */
        //BBWorldManager.getInstance().loadLevel("Scenes/TestScene/newScene.j3o"); 
        BBWorldManager.getInstance().createNavMesh((Terrain) terrain);
        BBWorldManager.getInstance().attachLevel();
        
    }
    
    @Override
    public void simpleUpdate(){
       super.simpleUpdate();

       
    } 
    
    public void setupTerrain(){
    //Load terrain
    
   AssetManager assetManager = BBSceneManager.getInstance().getAssetManager();     
        
   /** 1. Create terrain material and load four textures into it. */
    Material mat_terrain = new Material(assetManager, 
            "Common/MatDefs/Terrain/Terrain.j3md");
 
    /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("Alpha", assetManager.loadTexture(
            "Textures/Terrain/splat/alphamap.png"));
 
    /** 1.2) Add GRASS texture into the red layer (Tex1). */
    Texture grass = assetManager.loadTexture(
            "Textures/Terrain/splat/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex1", grass);
    mat_terrain.setFloat("Tex1Scale", 64f);
 
    /** 1.3) Add DIRT texture into the green layer (Tex2) */
    Texture dirt = assetManager.loadTexture(
            "Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex2", dirt);
    mat_terrain.setFloat("Tex2Scale", 32f);
 
    /** 1.4) Add ROAD texture into the blue layer (Tex3) */
    Texture rock = assetManager.loadTexture(
            "Textures/Terrain/splat/road.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex3", rock);
    mat_terrain.setFloat("Tex3Scale", 128f);
 
    /** 2. Create the height map */
    AbstractHeightMap heightmap = null;
    Texture heightMapImage = assetManager.loadTexture(
            "Textures/Terrain/splat/mountains512.png");
    heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.5f);
    heightmap.load();
 
    /** 3. We have prepared material and heightmap. 
     * Now we create the actual terrain:
     * 3.1) Create a TerrainQuad and name it "my terrain".
     * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
     * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
     * 3.4) As LOD step scale we supply Vector3f(1,1,1).
     * 3.5) We supply the prepared heightmap itself.
     */
    int patchSize = 65;
    terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());
 
    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, 0, 0);
    terrain.setLocalScale(2f, 1f, 2f);
    // We set up collision detection for the scene by creating a
    // compound collision shape and a static RigidBodyControl with mass zero.
    //TerrainQuad tQ = (TerrainQuad) world.getChild(1);
    //terrain.addControl(new RigidBodyControl(new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale()),0));
    // Add terrain to phys. space.
    //bulletAppState.getPhysicsSpace().addAll(terrain);
    }
    
    
}
