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
package bigboots.editor;

import com.bigboots.BBCanvasApplication;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBModelViewer extends BBCanvasApplication{
    private final JFileChooser mFileCm;
    private final JFileChooser mFileCt;
    
    
    public BBModelViewer(){
        //Create a file chooser for Models
        mFileCm = new JFileChooser();
        //mFileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mFileCm.addChoosableFileFilter(new BBModelFilter());
        mFileCm.setAcceptAllFileFilterUsed(false);
        
        //Create a file chooser for Textures
        mFileCt = new JFileChooser();
        //mFileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mFileCt.addChoosableFileFilter(new BBTextureFilter());
        mFileCt.setAcceptAllFileFilterUsed(false);
        
    }

    public static void main(String[] args){  
        BBModelViewer myEditor = new BBModelViewer();
        myEditor.start();
    }

    @Override
    public void createCustomMenu() {
        JMenu menuAsset = new JMenu("Asset");
        menuBar.add(menuAsset);

        final JMenuItem itemLoadModel = new JMenuItem("Load Model");
        menuAsset.add(itemLoadModel);
        itemLoadModel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadModelFromFile();
            }
        });
        
        final JMenuItem itemLoadTexture = new JMenuItem("Load Diffuse Texture");
        menuAsset.add(itemLoadTexture);
        itemLoadTexture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTextureFromFile();
            }
        });
    }

    private void loadModelFromFile(){
        int returnVal = mFileCm.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = mFileCm.getSelectedFile();
            try{
                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
                ((BBSceneGrid)app).loadExternalModel(file.getName(), file.getParent());
            }catch (IOException ex){}
            
        }
        
        mFileCm.setSelectedFile(null);
    }

    private void loadTextureFromFile(){
        int returnVal = mFileCt.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = mFileCt.getSelectedFile();
            try{
                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
                ((BBSceneGrid)app).loadExternalTexture(file.getName(), file.getParent());
            }catch (IOException ex){}
            
        }
        
        mFileCt.setSelectedFile(null);
    }    
    
        

}
