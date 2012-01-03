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
package com.bigboots.scene;


import com.jme3.asset.AssetManager;
import com.jme3.material.*;
import com.jme3.scene.*;
import com.jme3.util.TangentBinormalGenerator;
import java.io.File;


/**
 *
 * @author mifth
 */
public class SceneComposer {
    
private AssetManager assett;
private Node sc;    
private String dirbase;
private String dirlevel;
    
  public  SceneComposer (Node scene, String dirTexBase, String dirTexLevel, AssetManager assetM) {
    
        assett = assetM;
        sc = scene;
        dirbase = dirTexBase;    
        dirlevel = dirTexLevel;
        
    }
    
    
public void composeMaterial() {
    
    //Search for geometries        
 SceneGraphVisitor sgv = new SceneGraphVisitor() {

            public void visit(Spatial spatial) {
                System.out.println(spatial);

                if (spatial instanceof Geometry) {
            
                Geometry geom_sc = (Geometry) spatial;
                System.out.println("Composing Material: " + geom_sc.getMaterial().getName() + " for Geometry " + geom_sc.getName());
                MaterialComposer matComp = new MaterialComposer(geom_sc, dirbase, dirlevel, assett);
                matComp.generateMaterial();
                
        }
            }
        };
 
  sc.depthFirstTraversal(sgv);  
//  sc.breadthFirstTraversal(sgv);   
    
}
   
   
}
