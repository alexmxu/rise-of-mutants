/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots.scene;


import com.jme3.material.*;
import com.jme3.scene.*;


/**
 *
 * @author mifth
 */
public class SceneComposer {
    

    
    
    SceneComposer (Node scene, String texFolder) {
    
        
//Search for geometries        
 SceneGraphVisitor sgv = new SceneGraphVisitor() {

            public void visit(Spatial spatial) {
                System.out.println(spatial);

                if (spatial instanceof Geometry) {
            
                Geometry geom = (Geometry) spatial;
                System.out.println(geom.getMaterial().getName());
                MaterialComposer matComp = new MaterialComposer(geom);
                matComp.generateMaterial();
                
        }
            }
        };
 
  scene.depthFirstTraversal(sgv);            
        
    }
    

   
   
}
