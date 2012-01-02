/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots.scene;


import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.*;
import com.jme3.scene.*;
import com.jme3.scene.plugins.blender.materials.MaterialHelper.DiffuseShader;
import com.jme3.texture.Texture;
import java.io.File;


/**
 *
 * @author mifth
 */
public class MaterialComposer {
    
    private AssetManager asset;
    private Geometry geo;
    private String mat_name;    
    private File texDir;
    
    
   public MaterialComposer (Geometry geom_mc, File dir, AssetManager assetManager) {
    
    asset = assetManager;    
    texDir = dir;
    geo = geom_mc;
    mat_name = geo.getMaterial().getName().toString();
        
    }
    
   public void  generateMaterial () {
    
    int length = mat_name.length();

    
    //Search for mixes of a material name
    int mix_base = mat_name.indexOf("bb"); //search for base mix (diffuse map)
    int mix_emissive = mat_name.indexOf("be"); //search for mix with emissive map (diffuse map + emissive map)
    int mix_specular = mat_name.indexOf("bs"); //search for mix with specular map (diffuse map + specular map)
    int mix_alpha = mat_name.indexOf("ba"); //search for mix with specular map (diffuse map + alpha map)
    int mix_alpha_specular = mat_name.indexOf("as"); //search for mix with specular map (diffuse map + specular map + alpha map)
    int mix_emissive_specular = mat_name.indexOf("es"); //search for mix with specular map (diffuse map + emissive map + specular map)

    int mix_compound = mat_name.indexOf("cc"); //search for base mix (two diffuse maps)    
    int mix_compound_specular = mat_name.indexOf("cc"); //search for base mix (two diffuse maps + specular)        

    int mix_mask = mat_name.indexOf("m"); //search for base mix (diffuse map)
    int mix_occlusion = mat_name.indexOf("o"); //search for base mix (diffuse map)    
    
    
   //search for mix base 
   if (mat_name.indexOf("bb") >= 0) {
   
     //  mat_geo = new Material(asset, "MatDefs/LightBlow/LightBlow.j3md");
       System.out.println("Found mix_base");

       String folderStr = mat_name.substring(mat_name.indexOf("bb") + 2, mat_name.indexOf("bb") + 4);
       String fileStr = mat_name.substring(mat_name.indexOf("bb") + 4, mat_name.indexOf("bb") + 6);
       
       System.out.println(folderStr);
       System.out.println(fileStr);
       
       Material matd = new Material(asset, "MatDefs/LightBlow/LightBlow.j3md");
       matd.setName(mat_name);
       Texture diffuseTex = asset.loadTexture(getTexture(folderStr, fileStr));
       matd.setTexture("DiffuseMap", diffuseTex);
       
       geo.setMaterial(matd);
       
   }    
       
   }



   public String getTexture(String foldID, String fileID) {

  String texPath = texDir.toString();
  String texPath3 = new String();
  String texPath2 = new String();
       
  //Searching foldID
  String[] children = texDir.list();
if (children == null) {
    // Either dir does not exist or is not a directory
} 
else {
    for (int i=0; i<children.length; i++) {
        // Get filename of file or directory
        String filename = children[i];
        
        if (filename.indexOf(foldID) >= 0) {
       
    //Searching fileID        
    
    texPath2 = texPath + "/" + filename;
    File fileTex = new File(texPath2);
    System.out.println("folder " + texPath2);

    String[] children2 = fileTex.list();
if (children2 == null) {
    // Either dir does not exist or is not a directory
} else {
    
    for (int j=0; j<children2.length; j++) {
        // Get filename of file or directory
        String filename2 = children2[j];
        System.out.println("folder " + texPath2);
        if (filename2.substring(filename.indexOf(foldID) + 2).indexOf(fileID) >= 0 && filename2.indexOf("_nor") < 0) {
            texPath3 = texPath2 + "/" + filename2; 
            System.out.println(texPath3 + " file");
            

            
        }
    }
}
            
            
        }
    }
}       
 
if (texPath3.indexOf("assets/") >= 0) return texPath3.substring(7); 
System.out.println("aaa" + texPath3);


       return texPath3;  
   }


}
