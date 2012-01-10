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


import com.bigboots.scene.BBSceneComposer;
import com.bigboots.scene.BBShaderManager;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.BlenderKey;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;


public class TestSceneComposer extends SimpleApplication {

      
    public static void main(String[] args) {
        TestSceneComposer app = new TestSceneComposer();
        app.start();
    }


    @Override
    public void simpleInitApp() {

        // Load a blender file. 
        DesktopAssetManager dsk = (DesktopAssetManager) assetManager;        
        BlenderKey bk = new BlenderKey("Scenes/levels/level_01/level_01.blend");
        Node nd =  (Node) dsk.loadModel(bk); 
        nd.setName("nd");

        String entities = "assets/Models";
        String baseTex = "assets/Textures/base_textures";
        String levelTex = "assets/Textures/level_textures";
        String scenePath = bk.getFolder().substring(0, bk.getFolder().length() - 1); //BlenderKey sets "File.separator" in the end of String

        BBSceneComposer sc = new BBSceneComposer(nd, entities, scenePath, baseTex, levelTex, assetManager);
        TangentBinormalGenerator.generate(nd);

        
        // ShaderManager test
        BBShaderManager shm = new BBShaderManager(nd, assetManager);
        shm.setSimpleIBLParam("Textures/skyboxes/sky_box_01/skybox_01_low.png");   
        shm.setFogParam(new ColorRGBA(0.67f,0.55f,0.2f, 85f), null);
        
        rootNode.attachChild(nd);

        // Set Skybox
        TextureKey key_west = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_west.png", true);
        key_west.setGenerateMips(true);
        
        Texture sky_west = assetManager.loadTexture(key_west);
        TextureKey key_east = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_east.png", true);
        key_east.setGenerateMips(true);
        Texture sky_east = assetManager.loadTexture(key_east);        
        TextureKey key_north = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_north.png", true);
        key_north.setGenerateMips(true);
        Texture sky_north = assetManager.loadTexture(key_north);        
        TextureKey key_south = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_south.png", true);
        key_south.setGenerateMips(true);
        Texture sky_south = assetManager.loadTexture(key_south);        
        TextureKey key_top = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_top.png", true);
        key_top.setGenerateMips(true);
        Texture sky_top = assetManager.loadTexture(key_top);        
        TextureKey key_bottom = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_bottom.png", true);
        key_bottom.setGenerateMips(true);
        Texture sky_bottom = assetManager.loadTexture(key_bottom);        

        Vector3f normalScale = new Vector3f(1, 1, 1);

        Spatial sky = SkyFactory.createSky(assetManager, sky_west, sky_east, sky_north, sky_south, sky_top, sky_bottom, normalScale);
        rootNode.attachChild(sky);


        
        
        // Add a light Source
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5432741f, -0.58666015f, -0.6005691f).normalizeLocal());
        dl.setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        rootNode.addLight(dl);

        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

    }

}
