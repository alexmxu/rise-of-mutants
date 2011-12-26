/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots.scene;


import com.jme3.asset.AssetManager;
import com.jme3.material.*;
import com.jme3.scene.*;
import java.io.File;


/**
 *
 * @author mifth
 */
public class SceneComposer {
    
private AssetManager assett;
    
    
    SceneComposer (Node scene, String texFolder, AssetManager assetM) {
    
        assett = assetM;

        final File dir = new File(texFolder);

        
//Search for geometries        
 SceneGraphVisitor sgv = new SceneGraphVisitor() {

            public void visit(Spatial spatial) {
                System.out.println(spatial);

                if (spatial instanceof Geometry) {
            
                Geometry geom_sc = (Geometry) spatial;
                System.out.println(geom_sc.getMaterial().getName());
                MaterialComposer matComp = new MaterialComposer(geom_sc, dir, assett);
                matComp.generateMaterial();
                
        }
            }
        };
 
  scene.depthFirstTraversal(sgv);            
        
    }
    

   
   
}
