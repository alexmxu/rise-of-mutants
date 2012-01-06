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


import com.bigboots.scene.SceneComposer;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.BlenderKey;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jme3.util.TangentBinormalGenerator;
import java.io.File;


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
        
    String entities = new String("assets/Models");
    entities.replaceAll("/", File.separator);
    String baseTex = new String("assets/Textures/base_textures");
    baseTex.replaceAll("/", File.separator);
    String levelTex = new String("assets/Textures/level_textures");
    levelTex.replaceAll("/", File.separator);
  
    System.out.println(bk.getFolder().toString());
    SceneComposer sc = new SceneComposer(nd, entities, bk.getFolder().toString(), baseTex, levelTex, assetManager);
    TangentBinormalGenerator.generate(nd);
    rootNode.attachChild(nd);
        


        
//        // Clear Cache
//        nd.detachAllChildren();
//        nd.removeFromParent();
//        dsk.clearCache();
 
        
        // Add a light Source
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5432741f, -0.58666015f, -0.6005691f).normalizeLocal());
        dl.setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        rootNode.addLight(dl);
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

}


    }
