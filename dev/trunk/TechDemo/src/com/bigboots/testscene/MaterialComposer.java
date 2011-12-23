/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots.testscene;


import com.jme3.material.*;
import com.jme3.scene.*;


/**
 *
 * @author mifth
 */
public class MaterialComposer {
    
    private Material mat_geo;
    private String mat_name;    
    
    MaterialComposer (Geometry geo) {
    
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
    
   if (mat_name.indexOf("bb") >= 0) {
   
   //Search of a number of a folder "base_xx"   
   int bb1 = Character.getNumericValue(mat_name.charAt(mix_base + 1));
   bb1 *= 10;
   int bb2 = Character.getNumericValue(mat_name.charAt(mix_base + 2));
   int bb_folder = bb1 + bb2;
   
   //Search of a number of a file "base_xx"
   int bb3 = Character.getNumericValue(mat_name.charAt(mix_base + 3));
   bb3 *= 10;
   int bb4 = Character.getNumericValue(mat_name.charAt(mix_base + 4));
   int bb_file = bb3 + bb4;              
   }    
       
   }

   
   
   
}
