/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots.scene;

import com.jme3.asset.AssetManager;
import com.jme3.asset.BlenderKey;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.scene.Node;

/**
 *
 * @author mifth
 */
public class SceneLoading {
    
    
    public SceneLoading(AssetManager asm) {

        // TestLevel
        convertNow("level_01", "Scenes/levels/level_01/level_01.blend", asm);
        
    }

    
    private void convertNow(String sceneName, String scenePath, AssetManager assett) {
        
        // Load a blender file Scene. 
        DesktopAssetManager dsk = (DesktopAssetManager) assett;  
        
        // Register file locator for the AssetManager
        assett.registerLocator("blsets", FileLocator.class);

        BlenderKey bk = new BlenderKey(scenePath);
        bk.setLoadObjectProperties(false);
        Node nd =  (Node) dsk.loadModel(bk);               

        // Clear blend file
        dsk.clearCache();       
        assett.unregisterLocator("blsets", FileLocator.class);
        
        String models = "blsets/Models";
        String baseTex = "assets/Textures/base_textures";
        String levelTex = "assets/Textures/level_textures";
        String sceneFilePath = bk.getFolder().substring(0, bk.getFolder().length() - 1); //BlenderKey sets "File.separator" in the end of String

        // Creating j3o from the Blend Scene
        Blender2J3O sc = new Blender2J3O(nd, sceneName, models, sceneFilePath, baseTex, levelTex, assett);

        //Clear nd Node
        nd.detachAllChildren();
        nd.removeFromParent();
        nd = null;        
    }
    
    
}
