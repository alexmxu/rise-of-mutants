package com.bigboots.testscene;


import com.bigboots.scene.SceneComposer;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.Node;
import java.io.File;


public class TestSceneComposer extends SimpleApplication {


    Node ndmd;
    

     
      
    public static void main(String[] args) {
        TestSceneComposer app = new TestSceneComposer();
        app.start();
    }

    
    
     public void Models () {
        
         //Create an empty node for models 
         ndmd = new Node("Models");
         
         

        
        
        }
     
     
    
    @Override
    public void simpleInitApp() {
        
        Models();
        
        // Load a blender file. 
        DesktopAssetManager dsk = (DesktopAssetManager) assetManager;        
        ModelKey bk = new ModelKey("Scenes/levels/level_01/level_01.blend");
        Node nd =  (Node) dsk.loadModel(bk); 
        nd.setName("nd");
        
        
//        // Attach boxes with names and transformations of the blend file to a Scene
//         for (int j=0; j<ndmd.getChildren().size();j++){
//            String strmd = ndmd.getChild(j).getName();
//                
//            for (int i=0; i<nd.getChildren().size(); i++) {
//                      
//               String strndscene = nd.getChild(i).getName();
//             if (strmd.length() < strndscene.length())  strndscene = strndscene.substring(0, strmd.length());
//               
//         
//            if (strndscene.equals(strmd) == true){
//                Spatial ndGet =  ndmd.getChild(j).clone(false);
//                ndGet.setName(strndscene);
//                ndGet.setLocalTransform(nd.getChild(i).getWorldTransform());
//                ndscene.attachChild(ndGet);   
//                
//         }    
//         }
//         }
           
        
SceneComposer sc = new SceneComposer(nd, "assets/Textures/base_textures", assetManager);
sc.composeMaterial();

        rootNode.attachChild(nd);


        
//        // Clear Cache
//        nd.detachAllChildren();
//        nd.removeFromParent();
//        dsk.clearCache();
  
        
        File dir = new File("assets/Scenes/levels");
        
        System.out.println(dir.toString());
        String[] children = dir.list();
        if (children == null) {
            System.out.println("null");
    // Either dir does not exist or is not a directory
} else {
    for (int i=0; i<children.length; i++) {
        // Get filename of file or directory
        String filename = children[i];
        System.out.println(filename);
    }
        }  
        
        // Add a light Source
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5432741f, -0.58666015f, -0.6005691f).normalizeLocal());
        dl.setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        rootNode.addLight(dl);
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

}


    }




