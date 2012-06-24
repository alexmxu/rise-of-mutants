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
import com.jme3.scene.*;
import com.jme3.texture.Texture;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author mifth
 */
public class BBMaterialComposer {
    
    private AssetManager asset;
    private Node nodeMain;
    private ArrayList geometries;
    private JSONObject jsObj;
    private String sceneName;
    public BBMaterialComposer (Node node, AssetManager assetManager, String sceneNAME) throws FileNotFoundException, IOException, ParseException {

        asset = assetManager;    
        nodeMain = node;
        sceneName = sceneNAME;
        
        getGeometries(nodeMain);

        // Load JSON script
        JSONParser json = new JSONParser();
        FileReader fileRead = new FileReader(new File("assets/Scripts/Scenes/" + sceneName + ".json"));
        jsObj = (JSONObject) json.parse(fileRead);        
        
        setShader();
        
        
        
        jsObj.clear();
        fileRead.close();        
    }
   
    
    // Get all Geometries
    private void getGeometries(Node nodeMat) {

        Node ndMat = nodeMat;

        //Search for geometries        
        SceneGraphVisitor sgv = new SceneGraphVisitor() {

            public void visit(Spatial spatial) {
//                System.out.println(spatial + " Visited Spatial");
                if (spatial instanceof Geometry) {
                    Geometry geom_sc = (Geometry) spatial;
                    geometries.add(geom_sc);
          }
        }
     };

        ndMat.depthFirstTraversal(sgv);
        //  sc.breadthFirstTraversal(sgv);     
    }   
    
    private void setShader(){
    
        JSONObject jsonMat = (JSONObject) jsObj.get("Materials");
    for (Object geo : geometries.toArray()) {
     
        Geometry geom = (Geometry) geo;
        JSONObject jsonMatName = (JSONObject) jsonMat.get(geom.getName());    
        String jsonShaderName =  (String) jsonMatName.get("Shader");
        
        if (jsonShaderName.equals("LightBlow")) setLightBlow(jsonMatName, geom);
        
     }
        
    }
    
    private void setLightBlow(JSONObject material, Geometry geo){
    
        Geometry geomLB = geo;
        
        Material matNew = new Material(asset, "MatDefs/LightBlow/LightBlow.j3md");
        matNew.setName(geomLB.getMaterial().getName());
        geomLB.setMaterial(matNew);

        
    // DiffuseMap
    if (material.get("DiffuseMap") != null) {
        // Set Diffuse Map
        TextureKey tkDif = new TextureKey(texPath3, BlenderOgreCheck);
        tkDif.setAnisotropy(2);
        Texture diffuseTex = asset.loadTexture(tkDif);
        diffuseTex.setWrap(Texture.WrapMode.Repeat);
        matThis.setTexture("DiffuseMap", diffuseTex);
    
    }
    
    }

    
    
    
    
    
    public void  generateMaterial (String entPath) {
        //System.out.println("Generating Material");
        String fileStr = new String();
        String folderStr = new String();
        String strEntity = entPath; // Checking is it Entity or not

        // If this is Entity
        if (entPath != null) {
           String entPath2 = entPath.substring(13);  // remove "assets/Models" name
           entPath2 = "assets/Textures" + entPath2;
           File someDir = new File(entPath2);
           texDir = someDir.getParentFile();
           tmpString = someDir.getParent();
           

        // convert to / for windows
        if (File.separatorChar == '\\'){
            tmpString = tmpString.replace('\\', '/');
        }
        if(!tmpString.endsWith("/")){
            tmpString += "/";
        }           
            
            tmpString = tmpString.substring(0, tmpString.length() - 1);
        
            System.out.println(someDir + "uuuuuuuuuuuuu");            
            
//           System.out.println(tmpString + "xxxxxxxxxxxxxxxxxxxxxxx");
           folderStr = someDir.getName();
           fileStr = matName.substring(2, 4);

        } else {
           folderStr = matName.substring(2, 4);
           fileStr = matName.substring(4, 6);

        }

        System.out.println("Get Folder " + folderStr);
        System.out.println("Get File " + fileStr);
        System.out.println("Get Entity " + strEntity);

        Material matNew = new Material(asset, "MatDefs/LightBlow/LightBlow.j3md");
        matNew.setName(matName);
        setYourTexture(matNew, folderStr, fileStr, strEntity);

        geo.setMaterial(matNew);       
    }

   
    private void setYourTexture(Material mat, String foldID, String fileID, String entityPath) {
        Material matThis = mat;     
        String texPath = tmpString; //texDir.toString();
        String texPath2 = new String();
        String texPath3 = new String();
        String texPath3_nor = new String();
        String entiPath = entityPath;
  
        //Searching folderID
        String[] children = texDir.list();
        if (children == null) {
            // Either dir does not exist or is not a directory
        } 
        else {
            for (int i=0; i<children.length; i++) {
                // Get filename of directory
                String filename = children[i];
                System.out.println("********** File name in children : " + filename);        
                if (filename.indexOf(foldID) >= 0) {
                    //Searching file        
                    texPath2 = texPath + "/" + filename;
                    File fileTex = new File(texPath2);
                    System.out.println("folder textPath2 " + texPath2);

                    String[] children2 = fileTex.list();
                    if (children2 == null) {
                        // Either dir does not exist or is not a directory
                    } else {
                        for (int j=0; j<children2.length; j++) {
                            // Get filename of file
                            String filename2 = children2[j];
//                            System.out.println("********** File name 2 in children : " + filename2); 
                            int ent;




                            // Get Diffuse Map
                            if (filename2.indexOf(fileID + ".") >= 0 && filename2.indexOf("_nor") < 0 && filename2.indexOf(".blend") < 0 
                                    && filename2.indexOf(".psd") < 0  && filename2.indexOf(".xcf") < 0 && filename2.indexOf("lightmap_") < 0 && filename2.indexOf("mask_") < 0) {
                                texPath3 = texPath2 + "/" + filename2;
                                //texPath3.replaceAll(File.separator.toString(), "/");
                                System.out.println("file " + texPath3);
                            }
                            // Get Normal Map
                            else if (filename2.indexOf(fileID + "_nor.") >= 0 && filename2.indexOf(".blend") < 0 
                                    && filename2.indexOf(".psd") < 0  && filename2.indexOf(".xcf") < 0 && filename2.indexOf("lightmap_") < 0 && filename2.indexOf("mask_") < 0) {
                                texPath3_nor = texPath2 + "/" + filename2;
                                //texPath3_nor.replaceAll(File.separator.toString(), "/");
                                //System.out.println("file NormalMap " + texPath3_nor);
                            }
                        }
                    }
                }
            }
        }       
 
        if (texPath3.indexOf("assets/") == 0) texPath3 = texPath3.substring(7); 
        // Set Diffuse Map
        TextureKey tkDif = new TextureKey(texPath3, BlenderOgreCheck);
        tkDif.setAnisotropy(2);
        System.out.println("Texpath3 : " + texPath3);
        if (texPath3.indexOf(".dds") < 0) tkDif.setGenerateMips(true);
        Texture diffuseTex = asset.loadTexture(tkDif);
        diffuseTex.setWrap(Texture.WrapMode.Repeat);
        matThis.setTexture("DiffuseMap", diffuseTex);
//        matThis.setBoolean("Nor_Inv_X", true);
        

        // Set Normal Map if you have a "texPath3_nor.png" 
        if (texPath3_nor.length() > 3) {
            if (texPath3_nor.indexOf("assets/") == 0) texPath3_nor = texPath3_nor.substring(7);
            TextureKey tkNor = new TextureKey(texPath3_nor, BlenderOgreCheck);
            tkNor.setAnisotropy(2);
            if (texPath3_nor.indexOf(".dds") < 0) tkNor.setGenerateMips(true);
            Texture normalTex = asset.loadTexture(tkNor);
            normalTex.setWrap(Texture.WrapMode.Repeat);
            matThis.setTexture("NormalMap", normalTex);
            if (BlenderOgreCheck == false) matThis.setBoolean("Nor_Inv_Y", true);            
        }       

        // Set Specular Lighting
        if (matName.indexOf("s") == 0) {
            matThis.setBoolean("Specular_Lighting", true);
//            matThis.setColor("Specular", ColorRGBA.White);
            matThis.setBoolean("Spec_A_Dif", true);
//            matThis.setFloat("Shininess", 3.0f);
        } else if (matName.indexOf("s") == 1) {
            matThis.setBoolean("Specular_Lighting", true);
//            matThis.setColor("Specular", ColorRGBA.White);
            matThis.setBoolean("Spec_A_Nor", true);
//            matThis.setFloat("Shininess", 3.0f);
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
            compoundMat(matThis, matName.substring(6, 8), matName.substring(8, 10), entiPath);

        }
        
        
        // Set Lightmap(Ambient Occlusion) Texture
        if (matName.indexOf("o") >= 0) {     
            File aoDir;
            Texture textureAO;

            if (entiPath != null){
           String entPath3 = entiPath.substring(13);  // remove "assets/Models" name
           entPath3 = "assets/Textures" + entPath3;
           File someDir = new File(entPath3);
           texLightMaps = someDir.getParent();

        // convert to / for windows
        if (File.separatorChar == '\\'){
            texLightMaps = texLightMaps.replace('\\', '/');
        }
        if(!texLightMaps.endsWith("/")){
            texLightMaps += "/";
        }           
            
            texLightMaps = entPath3;
            }

            aoDir = new File(texLightMaps);
            
            String[] childrenAO = aoDir.list();
            if (childrenAO == null) {
                // Either dir does not exist or is not a directory
            } else {
                for (int i=0; i<childrenAO.length; i++) {
                    // Get filename of file or directory
                    String fileAO = childrenAO[i];

                    String matCheck = new String(); 
                    if (matName.indexOf("oR") > 0 || matName.indexOf("oG") > 0 || matName.indexOf("oB") > 0){ 
                        matCheck = matName.substring(matName.indexOf("o") + 2, matName.indexOf("o") + 4);
                    }
                    else if (matName.indexOf("oR") < 0 && matName.indexOf("oG") < 0 && matName.indexOf("oB") < 0){ 
                        matCheck = matName.substring(matName.indexOf("o") + 1, matName.indexOf("o") + 3);
                    }

                    if (fileAO.indexOf(matCheck) >= 0 && fileAO.indexOf("lightmap") >= 0 
                     && fileAO.indexOf(".blend") < 0 && fileAO.indexOf(".psd") < 0 && fileAO.indexOf(".xcf") < 0) {
                        String strAO = texLightMaps + "/" + fileAO;
                        //strAO.replaceAll(File.separator.toString(), "/");
                        //System.out.println(strAO + " LightMap Loading");
                        if (strAO.indexOf("assets/") == 0) {
//            System.out.println(strAO + " UUUUUUUUUUUUUUUUUUUUUU");
                            TextureKey tkAO = new TextureKey(strAO.substring(7), BlenderOgreCheck);
                            tkAO.setAnisotropy(2);
                            if (strAO.indexOf(".dds") < 0) tkAO.setGenerateMips(true);
                            textureAO = asset.loadTexture(tkAO);
                            textureAO.setWrap(Texture.WrapMode.Repeat);                
                        }
                        else {
                            TextureKey tkAO = new TextureKey(strAO, BlenderOgreCheck);
                            tkAO.setAnisotropy(2);
                            if (strAO.indexOf(".dds") < 0) tkAO.setGenerateMips(true);
                            textureAO = asset.loadTexture(tkAO);
                            textureAO.setWrap(Texture.WrapMode.Repeat);
                        }

                        matThis.setTexture("LightMap", textureAO);
                        matThis.setBoolean("SeperateTexCoord", true);

                        if (matName.indexOf("oR") >= 0) matThis.setBoolean("LightMap_R", true);
                        if (matName.indexOf("oG") >= 0) matThis.setBoolean("LightMap_G", true);
                        if (matName.indexOf("oB") >= 0) matThis.setBoolean("LightMap_B", true);
                    }
                }
            }
        }        
   }

   
   //This method is used for compound materials
   private void compoundMat(Material mat, String foldID, String fileID, String entityPath2) {
        Material matThat = mat;     
        String ctexPath = tmpString; //texDir.toString();
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
                    //System.out.println("compound folder " + ctexPath2);

                    String[] childrenC2 = fileTexC.list();
                    if (childrenC2 == null) {
                        // Either dir does not exist or is not a directory
                    } else {
                        for (int j=0; j<childrenC2.length; j++) {
                            // Get filename of file
                            String filename2 = childrenC2[j];
                            int ent;

                            // Get Diffuse Map
                            if (filename2.indexOf(fileID + ".") >= 0 && filename2.indexOf("_nor") < 0 && filename2.indexOf(".blend") < 0 
                            && filename2.indexOf(".psd") < 0  && filename2.indexOf(".xcf") < 0 && filename2.indexOf("lightmap_") < 0 && filename2.indexOf("mask_") < 0) {
                                ctexPath3 = ctexPath2 + "/" + filename2; 
                                //ctexPath3.replaceAll(File.separator.toString(), "/");
                                //System.out.println("compound file " + ctexPath3);
                            }
                            // Get Normal Map
                            else if (filename2.indexOf(fileID + "_nor.") >= 0 && filename2.indexOf(".blend") < 0 
                            && filename2.indexOf(".psd") < 0  && filename2.indexOf(".xcf") < 0 && filename2.indexOf("lightmap_") < 0 && filename2.indexOf("mask_") < 0) {
                                ctexPath3_nor = ctexPath2 + "/" + filename2;
                                //ctexPath3_nor.replaceAll(File.separator.toString(), "/");
                                //System.out.println("compound file NormalMap " + ctexPath3_nor);
                            }
                        }
                    }
                }
            }
        }      

        if (ctexPath3.indexOf("assets" + "/") == 0) ctexPath3 = ctexPath3.substring(7); 

        int uvScale = Integer.parseInt(matName.substring(matName.indexOf("m") + 3,matName.indexOf("m") + 5)); 
        //System.out.println("UV Scale is: " + uvScale);
        matThat.setFloat("uv_0_scale", (float) uvScale);

        // Set Diffuse Map R channel
        if (matName.indexOf("cR") >= 0) {
            TextureKey tkDifR = new TextureKey(ctexPath3, BlenderOgreCheck);
            tkDifR.setAnisotropy(2);
            if (ctexPath3.indexOf(".dds") < 0) tkDifR.setGenerateMips(true);
            Texture diffuseTexR = asset.loadTexture(tkDifR);
            diffuseTexR.setWrap(Texture.WrapMode.Repeat);
            matThat.setTexture("DiffuseMap_1", diffuseTexR);
            matThat.setFloat("uv_1_scale", (float) uvScale);            
        }
        // Set Diffuse Map G channel
        else if (matName.indexOf("cG") >= 0) {
            TextureKey tkDifG = new TextureKey(ctexPath3, BlenderOgreCheck);
            tkDifG.setAnisotropy(2);
            if (ctexPath3.indexOf(".dds") < 0) tkDifG.setGenerateMips(true);
            Texture diffuseTexG = asset.loadTexture(tkDifG);
            diffuseTexG.setWrap(Texture.WrapMode.Repeat);
            matThat.setTexture("DiffuseMap_2", diffuseTexG);
            matThat.setFloat("uv_2_scale", (float) uvScale);            
        }
        // Set Diffuse Map B channel
        else if (matName.indexOf("cB") >= 0) {
            TextureKey tkDifB = new TextureKey(ctexPath3, BlenderOgreCheck);
            tkDifB.setAnisotropy(2);
            if (ctexPath3.indexOf(".dds") < 0) tkDifB.setGenerateMips(true);
            Texture diffuseTexB = asset.loadTexture(tkDifB);
            diffuseTexB.setWrap(Texture.WrapMode.Repeat);
            matThat.setTexture("DiffuseMap_3", diffuseTexB);
            matThat.setFloat("uv_3_scale", (float) uvScale);
        }

        // Set Normal Map if you have a "texPath3_nor.png" 
        if (ctexPath3_nor.length() > 3) {
            if (ctexPath3_nor.indexOf("assets" + "/") == 0) ctexPath3_nor = ctexPath3_nor.substring(7);
            // Set Normal Map R channel
            if (matName.indexOf("cR") >= 0) {
                TextureKey tkNorR = new TextureKey(ctexPath3_nor, BlenderOgreCheck);   
                tkNorR.setAnisotropy(2);
                if (ctexPath3_nor.indexOf(".dds") < 0) tkNorR.setGenerateMips(true);
                Texture normalTexR = asset.loadTexture(tkNorR);
                normalTexR.setWrap(Texture.WrapMode.Repeat);
                matThat.setTexture("NormalMap_1", normalTexR);
                if (BlenderOgreCheck == false) matThat.setBoolean("Nor_Inv_Y", true);
            }
            // Set Normal Map G channel
            else if (matName.indexOf("cG") >= 0) {
                TextureKey tkNorG = new TextureKey(ctexPath3_nor, BlenderOgreCheck);  
                tkNorG.setAnisotropy(2);
                if (ctexPath3_nor.indexOf(".dds") < 0) tkNorG.setGenerateMips(true);
                Texture normalTexG = asset.loadTexture(tkNorG);
                normalTexG.setWrap(Texture.WrapMode.Repeat);
                matThat.setTexture("NormalMap_2", normalTexG);
                if (BlenderOgreCheck == false) matThat.setBoolean("Nor_Inv_Y", true);
            }
            // Set Normal Map B channel
            else if (matName.indexOf("cB") >= 0) {
                TextureKey tkNorB = new TextureKey(ctexPath3_nor, BlenderOgreCheck);
                tkNorB.setAnisotropy(2);
                if (ctexPath3_nor.indexOf(".dds") < 0) tkNorB.setGenerateMips(true);
                Texture normalTexB = asset.loadTexture(tkNorB);
                normalTexB.setWrap(Texture.WrapMode.Repeat);
                matThat.setTexture("NormalMap_3", normalTexB);
                if (BlenderOgreCheck == false) matThat.setBoolean("Nor_Inv_Y", true);
                
            }
        }      

        // Set Specular Map 
        if (matName.indexOf("s") > 3) {
            matThat.setBoolean("Specular_Lighting", true);
//            matThat.setColor("Specular", ColorRGBA.White);
            matThat.setBoolean("Spec_A_Nor", true);
//            matThat.setFloat("Shininess", 3.0f);           
        }

        // Set Mask Texture
        File maskDir;
        Texture textureMask;
        if (entityPath2 == null){ 
            maskDir = new File(texMasks);
        } else {
            maskDir = new File(entityPath2 + "/" + "textures");
        }

        String[] childrenMask = maskDir.list();
        if (childrenMask == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i=0; i<childrenMask.length; i++) {
                // Get filename of file or directory
                String fileMask = childrenMask[i];

                if (fileMask.indexOf(matName.substring(matName.indexOf("m") + 1, matName.indexOf("m") + 3)) >= 0 
                && fileMask.indexOf("mask") >= 0 && fileMask.indexOf(".blend") < 0 && fileMask.indexOf(".psd") < 0  && fileMask.indexOf(".xcf") < 0) {
                    //String strMask = maskDir + "/" + fileMask; 
                    String strMask = texMasks + "/" + fileMask;
                    //strMask.replaceAll(File.separator.toString(), "/");

                    if (strMask.indexOf("assets" + "/") == 0) {
                        TextureKey tkMask = new TextureKey(strMask.substring(7), BlenderOgreCheck);
                        tkMask.setAnisotropy(2);
                        tkMask.setGenerateMips(true);
                        textureMask = asset.loadTexture(tkMask);
                        textureMask.setWrap(Texture.WrapMode.Repeat);
                    }
                    else {
                        TextureKey tkMask = new TextureKey(strMask, BlenderOgreCheck);
                        tkMask.setAnisotropy(2);
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
