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
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.material.*;
import com.jme3.math.Vector2f;
import com.jme3.scene.*;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import java.io.File;
import java.util.List;


/**
 *
 * @author mifth
 */
public class SceneComposer {
    
private AssetManager assett;
private Node sc;    
private String dirbase;
private String dirlevel;
private String levelFold;
    
  public  SceneComposer (String levelFolder, Node scene, String dirTexBase, String dirTexLevel, AssetManager assetM) {
    
        levelFold = levelFolder;
        assett = assetM;
        sc = scene;
        dirbase = dirTexBase;    
        dirlevel = dirTexLevel;
        
        startCompose();
        
    }
  

  
  private void startCompose() {
      
      for (Spatial sp : sc.getChildren()) {
          System.out.println(sp.getName() + " scene node");

          if (sp instanceof Node) {
           
           Node nodeOrigin = (Node) sp;   
              
           if (nodeOrigin.getChildren().size() > 0) {
           ModelKey mkOgre = new ModelKey(levelFold + sp.getName() + ".mesh.xml");
           Node nodeOgre = (Node) assett.loadModel(mkOgre);
           List<Spatial> listOgre = nodeOgre.getChildren();
           System.out.println(nodeOgre.getName() + " ogre node");
           
           int index = 0;
           for (int i=listOgre.size()-1; i>=0; i--) {
               
               
               Geometry geoTemp = (Geometry) nodeOrigin.getChild(index);
               Geometry geoTempOgre = (Geometry) listOgre.get(i);
               Material matTemp = geoTemp.getMaterial();
               System.out.println(geoTemp.getName() + " AND " + geoTempOgre.getName());
               geoTemp.setMesh(geoTempOgre.getMesh());
               
               index += 1;
                   
           }
            }
             }
      }
      
      
      
      composeMaterial();
  }  
  
    
  private void composeMaterial() {
    
    //Search for geometries        
 SceneGraphVisitor sgv = new SceneGraphVisitor() {

            public void visit(Spatial spatial) {
                System.out.println(spatial);

                if (spatial instanceof Geometry) {
            
                Geometry geom_sc = (Geometry) spatial;

 //               System.out.println(geom_sc.getMesh().getBuffer(VertexBuffer.Type.TexCoord2).toString() + "UUUVVV");                
                System.out.println("Composing Material: " + geom_sc.getMaterial().getName() + " for Geometry " + geom_sc.getName());
                MaterialComposer matComp = new MaterialComposer(geom_sc, dirbase, dirlevel, assett);
                matComp.generateMaterial();
        TextureKey tkk = new TextureKey("Textures/skyboxes/sky_box_01/skybox_01_low.png", false);
        Texture ibl = assett.loadTexture(tkk);
        geom_sc.getMaterial().setTexture("IblMap_Simple", ibl); 
                
        }
            }
        };
 
  sc.depthFirstTraversal(sgv);  
//  sc.breadthFirstTraversal(sgv);   
    
}
   
   
}
