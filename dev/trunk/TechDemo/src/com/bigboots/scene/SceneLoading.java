/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots.scene;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Node;

/**
 *
 * @author mifth
 */
public class SceneLoading {
    
    
    public SceneLoading(AssetManager asm) {

            // Load a blender file Scene. 
        DesktopAssetManager dsk = (DesktopAssetManager) asm;        
        ModelKey bk = new ModelKey("Scenes/levels/level_01/level_01.blend");
        Node nd =  (Node) dsk.loadModel(bk);               

        String entities = "assets/Models";
        String baseTex = "assets/Textures/base_textures";
        String levelTex = "assets/Textures/level_textures";
        String scenePath = bk.getFolder().substring(0, bk.getFolder().length() - 1); //BlenderKey sets "File.separator" in the end of String

        // Creating Entities from the Blend Scene
        Blender2J3O sc = new Blender2J3O(nd, "level_01", entities, scenePath, baseTex, levelTex, asm);

        //Clear Blend File
        nd.detachAllChildren();
        nd.removeFromParent();
        nd = null;
        dsk.clearCache();       
        
        
    }
    
    
}
