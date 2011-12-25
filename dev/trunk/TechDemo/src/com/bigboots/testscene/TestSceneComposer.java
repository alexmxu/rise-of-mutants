package com.bigboots.testscene;


import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.Node;


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
           
        rootNode.attachChild(nd);

//Search for geometries        
 SceneGraphVisitor sgv = new SceneGraphVisitor() {

            public void visit(Spatial spatial) {
                System.out.println(spatial);

                if (spatial instanceof Geometry) {
            
                Geometry geom = (Geometry) spatial;
                System.out.println(geom.getMaterial().getName());
                
        }
            }
        };
 
  rootNode.depthFirstTraversal(sgv);    
//  rootNode.breadthFirstTraversal(sgv); 
        
        
//        // Clear Cache
//        nd.detachAllChildren();
//        nd.removeFromParent();
//        dsk.clearCache();
  
        
        
        
        
        // Add a light Source
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        rootNode.addLight(dl);
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

}


    }




