/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots.scene;


import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.*;
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
    private Material mat_geo;
    private String mat_name;    
    private File texDir;
    
    MaterialComposer (Geometry geom_mc, File dir, AssetManager assetManager) {
    
    asset = assetManager;    
    texDir = dir;
    geo = geom_mc;
    mat_geo = geo.getMaterial();
    mat_name = geo.getMaterial().getName();
        
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
   
       
       mat_geo.setTexture("DiffuseMap", asset.loadTexture(getTexture(mixFolder(mix_base), mixFile(mix_base))));

   }    
       
   }

   
private int mixFolder(int mix) {
    
//Search of a number of a folder "base_xx"   
   int bb1 = Character.getNumericValue(mat_name.charAt(mix + 1));
   bb1 *= 10;
   int bb2 = Character.getNumericValue(mat_name.charAt(mix + 2));
   int bb_folder = bb1 + bb2;
    
   return bb_folder;
}   
   
private int mixFile(int mix) {
    
   //Search of a number of a file "base_xx"
   int bb3 = Character.getNumericValue(mat_name.charAt(mix + 3));
   bb3 *= 10;
   int bb4 = Character.getNumericValue(mat_name.charAt(mix + 4));
   int bb_file = bb3 + bb4;              
   
   return bb_file;
    
}   

   private TextureKey getTexture(int foldID, int fileID) {

       
       
       
String[] children = texDir.list();
if (children == null) {
    // Either dir does not exist or is not a directory
} else {
    for (int i=0; i<children.length; i++) {
        // Get filename of file or directory
        String filename = children[i];
        
        if (filename.indexOf(foldID) >= 0) {

    File fileTex = new File(texDir.getPath() + filename);

    String[] children2 = fileTex.list();
if (children == null) {
    // Either dir does not exist or is not a directory
} else {
    for (int j=0; j<children2.length; i++) {
        // Get filename of file or directory
        String filename2 = children[j];
        if (filename.indexOf(fileID) >= 0) {
            //HERE I ENDED
            
        }
    }
}
            
            
        }
    }
}       
 
TextureKey tk = new TextureKey(string); //HERE I ENDED

       return tk;  
   }


}
