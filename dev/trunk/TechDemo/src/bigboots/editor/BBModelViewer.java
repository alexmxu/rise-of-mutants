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
    private final JFileChooser mFileC;
    
    
    public BBModelViewer(){
        //Create a file chooser
        mFileC = new JFileChooser();
        //mFileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mFileC.addChoosableFileFilter(new BBModelFilter());
        mFileC.setAcceptAllFileFilterUsed(false);
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
    }

    private void loadModelFromFile(){
        int returnVal = mFileC.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = mFileC.getSelectedFile();
            try{
                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
                ((BBSceneGrid)app).loadExternalModel(file.getName(), file.getParent());
            }catch (IOException ex){}
            
        }
        
        mFileC.setSelectedFile(null);
    }

    
}
