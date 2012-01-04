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
import com.jme3.asset.TextureKey;
import com.jme3.material.*;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.*;
import com.jme3.texture.Texture;
import java.io.File;


/**
 *
 * @author mifth
 */
public class MaterialComposer {
    
    private AssetManager asset;
    private Geometry geo;
    private String matName;    
    private String texMasks;
    private String texLightMaps;    
    private File texDir;
    
    
   public MaterialComposer (Geometry geom_mc, String dirBase, String dirLevel, AssetManager assetManager) {
    
       
    asset = assetManager;    
    geo = geom_mc;
    
    if (geo.getMaterial().getName().indexOf("l") == 0) {
        matName = geo.getMaterial().getName().toString().substring(1);
        texDir = new File(dirLevel);
    }
    else {
        matName = geo.getMaterial().getName().toString();
        texDir = new File(dirBase);
    }
   
    //Texture Path of Ambient Occlusion and Composing Masks Textures
    texMasks = dirLevel + "/" + "masks";
    texLightMaps = dirLevel + "/" + "lightmaps";
    
    }
   
   
   
   public void  generateMaterial () {
    
       System.out.println("Generating Material");

       String folderStr = matName.substring(2, 4);
       String fileStr = matName.substring(4, 6);
       
       System.out.println("Get Folder " + folderStr);
       System.out.println("Get File " + fileStr);
       
       Material matNew = new Material(asset, "MatDefs/LightBlow/LightBlow.j3md");
       matNew.setName(matName);
       setYourTexture(matNew, folderStr, fileStr);
       
       geo.setMaterial(matNew);       
   }

   
   private void setYourTexture(Material mat, String foldID, String fileID) {

  Material matThis = mat;     
  String texPath = texDir.toString();
  String texPath2 = new String();
  String texPath3 = new String();
  String texPath3_nor = new String();
       
  //Searching folderID
  String[] children = texDir.list();
if (children == null) {
    // Either dir does not exist or is not a directory
} 
else {
    for (int i=0; i<children.length; i++) {
        // Get filename of directory
        String filename = children[i];
                
        if (filename.indexOf(foldID) >= 0) {
       
    //Searching file        
    texPath2 = texPath + "/" + filename;
    File fileTex = new File(texPath2);
    System.out.println("folder " + texPath2);

    String[] children2 = fileTex.list();
if (children2 == null) {
    // Either dir does not exist or is not a directory
   } else {
    
    for (int j=0; j<children2.length; j++) {
        // Get filename of file
        String filename2 = children2[j];

        // Get Diffuse Map
        if (filename2.substring(filename.indexOf(foldID) + 2).indexOf(fileID) >= 0 && filename2.indexOf("_nor") < 0 && filename2.indexOf(".blend") < 0 && filename2.indexOf(".psd") < 0) {
        texPath3 = texPath2 + "/" + filename2; 
        System.out.println("file " + texPath3);
        }
        // Get Normal Map
        else if (filename2.substring(filename.indexOf(foldID) + 2).indexOf(fileID) >= 0 && filename2.indexOf("_nor") > 0 && filename2.indexOf(".blend") < 0 && filename2.indexOf(".psd") < 0) {
            texPath3_nor = texPath2 + "/" + filename2;
            System.out.println("file NormalMap " + texPath3_nor);
        }
    }
}
             
        }
    }
}       
 

       if (texPath3.indexOf("assets/") == 0) texPath3 = texPath3.substring(7); 
       
       // Set Diffuse Map
       TextureKey tkDif = new TextureKey(texPath3, false);
       tkDif.setGenerateMips(true);
       Texture diffuseTex = asset.loadTexture(tkDif);
       diffuseTex.setWrap(Texture.WrapMode.Repeat);
       matThis.setTexture("DiffuseMap", diffuseTex);


       // Set Normal Map if you have a "texPath3_nor.png" 
        if (texPath3_nor.length() > 3) {
            if (texPath3_nor.indexOf("assets/") == 0) texPath3_nor = texPath3_nor.substring(7);
          TextureKey tkNor = new TextureKey(texPath3_nor, false);
          tkNor.setGenerateMips(true);
          Texture normalTex = asset.loadTexture(tkNor);
          normalTex.setWrap(Texture.WrapMode.Repeat);
          matThis.setTexture("NormalMap", normalTex);
        }       
        
        // Set Specular Lighting
        if (matName.indexOf("s") == 0) {
            matThis.setBoolean("Specular_Lighting", true);
            matThis.setColor("Specular", ColorRGBA.White);
            matThis.setBoolean("Spec_A_Dif", true);
            matThis.setFloat("Shininess", 3.0f);
        } else if (matName.indexOf("s") == 1) {
            matThis.setBoolean("Specular_Lighting", true);
            matThis.setColor("Specular", ColorRGBA.White);
            matThis.setBoolean("Spec_A_Nor", true);
            matThis.setFloat("Shininess", 3.0f);
        }
       
        
        // Set Transparency
        if (matName.indexOf("a") == 0) {
            matThis.setBoolean("Alpha_A_Dif", true);
            matThis.setFloat("AlphaDiscardThreshold", 0.01f);
            matThis.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
           // geo.setQueueBucket(Bucket.Transparent);            
        } else if (matName.indexOf("a") == 1) {
            matThis.setBoolean("Alpha_A_Nor", true);
            matThis.setFloat("AlphaDiscardThreshold", 0.01f);
            matThis.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
           // geo.setQueueBucket(Bucket.Transparent);            
        }
        
        // Set Emission (Illumination)
        if (matName.indexOf("e") == 0) matThis.setBoolean("EmissiveMap", true);
        
        
        //Set Composite Material
        if (matName.indexOf("c") == 0) {
            compoundMat(matThis, matName.substring(6, 8), matName.substring(8, 10));
            
        }
        
       // Set Lightmap(Ambient Occlusion) Texture
   if (matName.indexOf("o") >= 0) {     
       File aoDir = new File(texLightMaps);
       Texture textureAO;
       
       String[] childrenAO = aoDir.list();
        if (childrenAO == null) {
          // Either dir does not exist or is not a directory
         } else {
         for (int i=0; i<childrenAO.length; i++) {
            // Get filename of file or directory
         String fileAO = childrenAO[i];
         
            
         if (fileAO.indexOf(matName.substring(matName.indexOf("o") + 1, matName.indexOf("o") + 3)) >= 0 
             && fileAO.indexOf("lightmap") >= 0 && fileAO.indexOf(".blend") < 0 && fileAO.indexOf(".psd") < 0) {
         String strAO = aoDir + "/" + fileAO;
             System.out.println(strAO + "IIIIIIII");
            if (strAO.indexOf("assets/") == 0) {
                TextureKey tkAO = new TextureKey(strAO.substring(7), false);
                tkAO.setGenerateMips(true);
                textureAO = asset.loadTexture(tkAO);
                textureAO.setWrap(Texture.WrapMode.Repeat);                
            }
            else {
                TextureKey tkAO = new TextureKey(strAO, false);
                tkAO.setGenerateMips(true);
                textureAO = asset.loadTexture(tkAO);
                textureAO.setWrap(Texture.WrapMode.Repeat);
            }
            
            matThis.setTexture("LightMap", textureAO);
            matThis.setBoolean("LightMap_R", true);
            matThis.setBoolean("SeperateTexCoord", true);
                    
            }
          }
        }
       }        
        
    
   }

   
   
   //This method is used for compound materials
   private void compoundMat(Material mat, String foldID, String fileID) {
  
  Material matThat = mat;     
  String ctexPath = texDir.toString();
  String ctexPath2 = new String();
  String ctexPath3 = new String();
  String ctexPath3_nor = new String();
       
  //Searching folderID
  String[] childrenC = texDir.list();
if (childrenC == null) {
    // Either dir does not exist or is not a directory
} 
else {
    for (int i=0; i<childrenC.length; i++) {
        // Get filename of directory
        String filename = childrenC[i];
        
        if (filename.indexOf(foldID) >= 0) {
       
    //Searching file        
    ctexPath2 = ctexPath + "/" + filename;
    File fileTexC = new File(ctexPath2);
    System.out.println("compound folder " + ctexPath2);

    String[] childrenC2 = fileTexC.list();
if (childrenC2 == null) {
    // Either dir does not exist or is not a directory
   } else {
    
    for (int j=0; j<childrenC2.length; j++) {
        // Get filename of file
        String filename2 = childrenC2[j];

        // Get Diffuse Map
        if (filename2.substring(filename.indexOf(foldID) + 2).indexOf(fileID) >= 0 && filename2.indexOf("_nor") < 0 && filename2.indexOf(".blend") < 0 && filename2.indexOf(".psd") < 0) {
        ctexPath3 = ctexPath2 + "/" + filename2; 
        System.out.println("compound file " + ctexPath3);
        }
        // Get Normal Map
        else if (filename2.substring(filename.indexOf(foldID) + 2).indexOf(fileID) >= 0 && filename2.indexOf("_nor") > 0 && filename2.indexOf(".blend") < 0 && filename2.indexOf(".psd") < 0) {
            ctexPath3_nor = ctexPath2 + "/" + filename2;
            System.out.println("compound file NormalMap " + ctexPath3_nor);
        }
    }
}
             
        }
    }
}      


       
       if (ctexPath3.indexOf("assets/") == 0) ctexPath3 = ctexPath3.substring(7); 
       
       
        int uvScale = Integer.parseInt(matName.substring(matName.indexOf("m") + 3,matName.indexOf("m") + 5)); 
        System.out.println("UV Scale is: " + uvScale);
        matThat.setFloat("uv_0_scale", (float) uvScale);
        
       // Set Diffuse Map R channel
       if (matName.indexOf("cR") >= 0) {
       TextureKey tkDifR = new TextureKey(ctexPath3, false);   
       tkDifR.setGenerateMips(true);
       Texture diffuseTexR = asset.loadTexture(tkDifR);
       diffuseTexR.setWrap(Texture.WrapMode.Repeat);
       matThat.setTexture("DiffuseMap_1", diffuseTexR);
        matThat.setFloat("uv_1_scale", (float) uvScale);            
       }
       // Set Diffuse Map G channel
       else if (matName.indexOf("cG") >= 0) {
       TextureKey tkDifG = new TextureKey(ctexPath3, false);
       tkDifG.setGenerateMips(true);
       Texture diffuseTexG = asset.loadTexture(tkDifG);
       diffuseTexG.setWrap(Texture.WrapMode.Repeat);
       matThat.setTexture("DiffuseMap_2", diffuseTexG);
       matThat.setFloat("uv_2_scale", (float) uvScale);            
       }
       // Set Diffuse Map B channel
       else if (matName.indexOf("cB") >= 0) {
       TextureKey tkDifB = new TextureKey(ctexPath3, false);
       tkDifB.setGenerateMips(true);
       Texture diffuseTexB = asset.loadTexture(tkDifB);
       diffuseTexB.setWrap(Texture.WrapMode.Repeat);
       matThat.setTexture("DiffuseMap_3", diffuseTexB);
       matThat.setFloat("uv_3_scale", (float) uvScale);
       }


       // Set Normal Map if you have a "texPath3_nor.png" 
       if (ctexPath3_nor.length() > 3) {
        
       if (ctexPath3_nor.indexOf("assets/") == 0) ctexPath3_nor = ctexPath3_nor.substring(7);
            
       // Set Normal Map R channel
       if (matName.indexOf("cR") >= 0) {
       TextureKey tkNorR = new TextureKey(ctexPath3_nor, false);   
       tkNorR.setGenerateMips(true);
       Texture normalTexR = asset.loadTexture(tkNorR);
       normalTexR.setWrap(Texture.WrapMode.Repeat);
       matThat.setTexture("NormalMap_1", normalTexR);
       }
       // Set Normal Map G channel
       else if (matName.indexOf("cG") >= 0) {
       TextureKey tkNorG = new TextureKey(ctexPath3_nor, false);  
       tkNorG.setGenerateMips(true);
       Texture normalTexG = asset.loadTexture(tkNorG);
       normalTexG.setWrap(Texture.WrapMode.Repeat);
       matThat.setTexture("NormalMap_2", normalTexG);
       }
       // Set Normal Map B channel
       else if (matName.indexOf("cB") >= 0) {
       TextureKey tkNorB = new TextureKey(ctexPath3_nor, false);
       tkNorB.setGenerateMips(true);
       Texture normalTexB = asset.loadTexture(tkNorB);
       normalTexB.setWrap(Texture.WrapMode.Repeat);
       matThat.setTexture("NormalMap_3", normalTexB);
       }
           
        }      

       // Set Specular Map 
       if (matName.indexOf("s") > 3) {
       matThat.setBoolean("Specular_Lighting", true);
       matThat.setColor("Specular", ColorRGBA.White);
       matThat.setBoolean("Spec_A_Nor", true);
       matThat.setFloat("Shininess", 3.0f);           
           
       }

       

       // Set Mask Texture
       File maskDir = new File(texMasks);
       Texture textureMask;
       
       String[] childrenMask = maskDir.list();
        if (childrenMask == null) {
          // Either dir does not exist or is not a directory
         } else {
         for (int i=0; i<childrenMask.length; i++) {
            // Get filename of file or directory
         String fileMask = childrenMask[i];
            
         if (fileMask.indexOf(matName.substring(matName.indexOf("m") + 1, matName.indexOf("m") + 3)) >= 0 
             && fileMask.indexOf("mask") >= 0 && fileMask.indexOf(".blend") < 0 && fileMask.indexOf(".psd") < 0) {
                  
            String strMask = maskDir + "/" + fileMask; 
             
            if (strMask.indexOf("assets/") == 0) {
                TextureKey tkMask = new TextureKey(strMask.substring(7), false);
                tkMask.setGenerateMips(true);
                textureMask = asset.loadTexture(tkMask);
                textureMask.setWrap(Texture.WrapMode.Repeat);
            }
            else {
                TextureKey tkMask = new TextureKey(strMask, false);
                tkMask.setGenerateMips(true);
                textureMask = asset.loadTexture(tkMask);
                textureMask.setWrap(Texture.WrapMode.Repeat);
            }
            
            matThat.setTexture("TextureMask", textureMask);       
                    
            }
          }
        }


 

   }

   
   
}
