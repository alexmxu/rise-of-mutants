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
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javax.swing.filechooser.FileFilter;

import javax.swing.JToolBar;



/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBModelViewer extends BBCanvasApplication implements ActionListener{
    private final JFileChooser mFileCm;
    private FileFilter modFilter = new BBModelFilter();
    private FileFilter texFilter = new BBTextureFilter();
    
    
    public BBModelViewer(){
        //Create a file chooser for Models
        mFileCm = new JFileChooser();
        //mFileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mFileCm.addChoosableFileFilter(modFilter);
        mFileCm.addChoosableFileFilter(texFilter);
        mFileCm.setAcceptAllFileFilterUsed(false);
        mFileCm.setPreferredSize(new Dimension(800, 600));
     
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
        
        final JMenuItem itemLoadTexture = new JMenuItem("Load Texture");
        menuAsset.add(itemLoadTexture);
        itemLoadTexture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTextureFromFile();
            }
        });
        
        JToolBar toolBar = new JToolBar("Viewer Options");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        addButtons(toolBar);
        mMainFrame.add(toolBar, BorderLayout.PAGE_START);
        

    }

    private void loadModelFromFile(){
        mFileCm.setFileFilter(modFilter);
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
        mFileCm.setFileFilter(texFilter);
        int returnVal = mFileCm.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = mFileCm.getSelectedFile();
//            mFileCt.setCurrentDirectory(file);
            try{
                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
                ((BBSceneGrid)app).loadExternalTexture(file.getName(), file.getParent());
            }catch (IOException ex){}
        }
        
        mFileCm.setSelectedFile(null);
    }    
    
    static final private String PREVIOUS = "previous";
    static final private String UP = "up";
    static final private String NEXT = "next";
    static final private String SOMETHING_ELSE = "other";
    static final private String TEXT_ENTERED = "text";
    protected void addButtons(JToolBar toolBar) {
        JButton button = null;
 
        //first button
        button = makeNavigationButton("Up24", UP,
                                      "Selection tool",
                                      "SELECT");
        toolBar.add(button);
 
        //second button
        button = makeNavigationButton("Back24", PREVIOUS,
                                      "Rotation tool",
                                      "ROTATE");
        toolBar.add(button);
 
        //third button
        button = makeNavigationButton("Forward24", NEXT,
                                      "Scale",
                                      "SCALE");
        toolBar.add(button);
 
        //separator
        toolBar.addSeparator();
 
    }
    protected JButton makeNavigationButton(String imageName,
                                           String actionCommand,
                                           String toolTipText,
                                           String altText) {
        //Look for the image.
        String imgLocation = "images/"
                             + imageName
                             + ".gif";
        URL imageURL = BBModelViewer.class.getResource(imgLocation);

        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
 
        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {                                     //no image found
            button.setText(altText);
            System.err.println("Resource not found: "
                               + imgLocation);
        }
 
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        String description = null;
 
        // Handle each button.
        if (PREVIOUS.equals(cmd)) { //first button clicked
            ((BBSceneGrid)app).mSceneGizmo.setRotateAxisVisible(true);
        } else if (UP.equals(cmd)) { // second button clicked
            ((BBSceneGrid)app).mSceneGizmo.setTranAxisVisible(true);
        } else if (NEXT.equals(cmd)) { // third button clicked
            ((BBSceneGrid)app).mSceneGizmo.setScaleAxisVisible(true);
        }
    }
}
