package com.bigboots.testscene;


import com.jme3.app.SimpleApplication;
import com.jme3.asset.BlenderKey;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.Node;
import com.jme3.scene.plugins.blender.BlenderModelLoader;
import com.jme3.scene.shape.*;


public class TestScene extends SimpleApplication {

    Geometry geom_a;
    Material mat_box;
    Node ndmd;
    
    // models
     Spatial obj01;
     Spatial obj02;
     Spatial obj03;
     Spatial ledder;
    
     // collision shapes
     Geometry obj01_l;
     Geometry obj02_l;
     Geometry obj03_l;
     Geometry ledder_l;
     
      
    public static void main(String[] args) {
        TestScene app = new TestScene();
        app.start();
    }

    
    
     public void Models () {
        
         //Create an empty node for models 
         ndmd = new Node("Models");
         
         
        // Material
        Material mat = assetManager.loadMaterial("Scenes/TestScene/TestSceneMaterial.j3m"); 
         
        // Models
        obj01 = assetManager.loadModel("Scenes/TestScene/obj01.obj"); 
        obj01.setName("obj01");
        obj01.setMaterial(mat);
        ndmd.attachChild(obj01);
        
        obj02 = assetManager.loadModel("Scenes/TestScene/obj02.obj"); 
        obj02.setName("obj02");
        obj02.setMaterial(mat);
        ndmd.attachChild(obj02);

        obj03 = assetManager.loadModel("Scenes/TestScene/obj03.obj"); 
        obj03.setName("obj03");
        obj03.setMaterial(mat);
        ndmd.attachChild(obj03);

        ledder = assetManager.loadModel("Scenes/TestScene/ledder.obj"); 
        ledder.setName("ledder");
        ledder.setMaterial(mat);
        ndmd.attachChild(ledder);

        
        //Collision Shapes
        obj01_l =  (Geometry) assetManager.loadModel("Scenes/TestScene/obj01_l.obj"); 
        obj02_l =  (Geometry) assetManager.loadModel("Scenes/TestScene/obj02_l.obj"); 
        obj03_l =  (Geometry) assetManager.loadModel("Scenes/TestScene/obj03_l.obj"); 
        ledder_l =  (Geometry) assetManager.loadModel("Scenes/TestScene/ledder_l.obj"); 

          // Collision Shapes to be used
//        CompoundCollisionShape myComplexShape = CollisionShapeFactory.createMeshShape((Node) myComplexGeometry );

        
        
        
        // Spawn Points of Mutants
        Box box_a = new Box(Vector3f.ZERO, 0.3f, 0.3f, 0.3f);
        geom_a = new Geometry("spawn", box_a);
        geom_a.updateModelBound();
        
        mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("m_Color", ColorRGBA.Blue);
        geom_a.setMaterial(mat_box);
        ndmd.attachChild(geom_a);
        
        
        
        }
     
     
    
    @Override
    public void simpleInitApp() {
        
        Models();
        
        
        // Load a blender file. 
        DesktopAssetManager dsk = (DesktopAssetManager) assetManager;        
        ModelKey bk = new ModelKey("Scenes/TestScene/test_scene_01_1.blend");
      //  bk.setFixUpAxis(false);
        Node nd =  (Node) dsk.loadModel(bk); 
        
        //Create empty Scene Node
        Node ndscene = new Node("Scene");
        
        
        // Attach boxes with names and transformations of the blend file to a Scene
         for (int j=0; j<ndmd.getChildren().size();j++){
            String strmd = ndmd.getChild(j).getName();
                
            for (int i=0; i<nd.getChildren().size(); i++) {
                      
               String strndscene = nd.getChild(i).getName();
             if (strmd.length() < strndscene.length())  strndscene = strndscene.substring(0, strmd.length());
               
         
            if (strndscene.equals(strmd) == true){
                Spatial ndGet =  ndmd.getChild(j).clone(false);
                ndGet.setName(strndscene);
                ndGet.setLocalTransform(nd.getChild(i).getWorldTransform());
                ndscene.attachChild(ndGet);   
                
         }    
         }
         }
           
        rootNode.attachChild(ndscene);

        // Clear Cache
        nd.detachAllChildren();
        nd.removeFromParent();
        dsk.clearCache();
  
        
        /*
        assetManager.registerLoader(BlenderModelLoader.class, "blend");
        // Load a blender file. 
        //DesktopAssetManager dsk = (DesktopAssetManager) assetManager;        
        ModelKey bk = new ModelKey("Scenes/TestScene/test_scene_01_1.blend");
      //  bk.setFixUpAxis(false);
        Node nd =  (Node) assetManager.loadModel(bk);
        rootNode.attachChild(nd);
        
        // Material
        Material woodMat = assetManager.loadMaterial("Scenes/TestScene/TestSceneMaterial.j3m");
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.setColor("m_Color", ColorRGBA.Blue);
        
        for (int i=0; i<nd.getChildren().size(); i++) {                     
            String strndscene = nd.getChild(i).getName();
            System.out.println("***** VISIT NODE : "+strndscene);
            
            if (strndscene.startsWith("spawn") == true){
                nd.getChild(i).setMaterial(boxMat);
            }else{
                nd.getChild(i).setMaterial(woodMat);
            }
         } 
        */
        // Add a light Source
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.7f,0.7f,0.7f,1));
        rootNode.addLight(dl);
        
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(1.9f,1.9f,3.0f,1.0f));
        rootNode.addLight(al);
        
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

}


    }




