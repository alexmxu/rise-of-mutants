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
import com.bigboots.BBWorldManager;
import com.bigboots.core.BBSceneManager;
import com.jme3.math.FastMath;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import javax.swing.filechooser.FileFilter;

import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;



/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBModelViewer extends BBCanvasApplication implements ActionListener, ListSelectionListener{
    private final JFileChooser mFileCm;
    private FileFilter modFilter = new BBModelFilter();
    private FileFilter texFilter = new BBTextureFilter();
    private JList listEntity, listGeo;
    private DefaultListModel modelEntity, modelGeo;
    
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

//        final JMenuItem itemLoadModel = new JMenuItem("Load Model");
//        menuAsset.add(itemLoadModel);
//        itemLoadModel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                loadModelFromFile();
//            }
//        });
//        
//        final JMenuItem itemLoadDifTexture = new JMenuItem("Load Diffuse Texture");
//        menuAsset.add(itemLoadDifTexture);
//        itemLoadDifTexture.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                loadDiffuseTexture();
//            }
//        });
        
        
//        final JMenuItem itemLoadNorTexture = new JMenuItem("Load Normal Texture");
//        menuAsset.add(itemLoadNorTexture);
//        itemLoadNorTexture.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                loadNormalTexture();
//            }
//        });
        
        
        final JMenuItem removeSelEntity = new JMenuItem("Remove Selected Model");
        menuAsset.add(removeSelEntity);
        removeSelEntity.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modelEntity.removeElement(((BBSceneGrid)app).selectedEntity);                
                ((BBSceneGrid)app).RemoveSelectedEntity();
            }
        });
        
        final JMenuItem clearScene = new JMenuItem("Clear Scene");
        menuAsset.add(clearScene);
        clearScene.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((BBSceneGrid)app).ClearScene();
                modelGeo.clear();
            }
        });        
        
        // ToolBar
        JToolBar toolBar = new JToolBar("Viewer Options");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        addButtons(toolBar);
        mMainFrame.add(toolBar, BorderLayout.PAGE_START);
        
        // entityList
        buttons();
        entityList();
        geometryList();
        

    }

    
    private void buttons() {
        
        JButton loadModelButton = new JButton("Load Model"); 
        loadModelButton.setSize(200, 20);
        loadModelButton.setPreferredSize(new Dimension(190, 20));
        loadModelButton.setVerticalTextPosition(AbstractButton.CENTER);
        loadModelButton.setHorizontalTextPosition(AbstractButton.LEADING); 
        loadModelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadModelFromFile();
                
            }
        });         
        optionPanel.add(loadModelButton);          
        
        JButton loadDiffuseButton = new JButton("Load Diffuse Texture"); 
        loadDiffuseButton.setSize(200, 20);
        loadDiffuseButton.setPreferredSize(new Dimension(190, 20));
        loadDiffuseButton.setVerticalTextPosition(AbstractButton.CENTER);
        loadDiffuseButton.setHorizontalTextPosition(AbstractButton.LEADING); 
        loadDiffuseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadDiffuseTexture();
                
                
            }
        });         
        optionPanel.add(loadDiffuseButton);          

        JButton loadNormalButton = new JButton("Load Normal Texture"); 
        loadNormalButton.setSize(200, 20);
        loadNormalButton.setPreferredSize(new Dimension(190, 20));
        loadNormalButton.setVerticalTextPosition(AbstractButton.CENTER);
        loadNormalButton.setHorizontalTextPosition(AbstractButton.LEADING); 
        loadNormalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadNormalTexture();
            }
        });         
        optionPanel.add(loadNormalButton);         
        
    }
    
    private void entityList() {

        // Create a list that allows adds and removes
        modelEntity = new DefaultListModel();
                
		// Create a new listbox control
		listEntity = new JList(modelEntity);
                
		// Set the frame characteristics
//		listbox.setTitle( "Simple ListBox Application" );
                listEntity.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                listEntity.setLayoutOrientation(JList.VERTICAL);
		listEntity.setSize( 180, 200 );
                listEntity.setPreferredSize(new Dimension(180, 200));
		listEntity.setBackground( new Color(245,245,245));
                listEntity.addListSelectionListener (this);
                
                JScrollPane listScroller = new JScrollPane(listEntity);
                listScroller.setSize(listEntity.getSize());   
                listScroller.setPreferredSize(listEntity.getPreferredSize());
                optionPanel.add(listScroller, BorderLayout.CENTER);                
        
    }
    

    private void geometryList() {

        
        // Create a list that allows adds and removes
        modelGeo = new DefaultListModel();
                
// Get the index of all the selected items
//int[] selectedIx = listGeo.getSelectedIndices();

		// Create a new listbox control
		listGeo = new JList( modelGeo );
                
		// Set the frame characteristics
//		listGeo.setTitle( "Simple ListBox Application" );
                listGeo.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                listGeo.setLayoutOrientation(JList.VERTICAL);
		listGeo.setSize( 180, 200 );
                listGeo.setPreferredSize(new Dimension(180, 200));
		listGeo.setBackground( new Color(245,245,245));
                listGeo.addListSelectionListener (this);
                
//                listGeo.setSelectedIndices(selectedIx);
//                Object[] selected = listGeo.getSelectedValues();

                // Get number of items in the list
//                int size = list.getModel().getSize();

        // Get all item objects
//        for (int i=0; i<size; i++) {
//        Object item = list.getModel().getElementAt(i);
//        }                
                
                JScrollPane listScroller2 = new JScrollPane(listGeo);
                listScroller2.setSize(listGeo.getSize());
                listScroller2.setPreferredSize(listGeo.getPreferredSize());
                optionPanel.add(listScroller2, BorderLayout.CENTER);                
                
    }
    
    
    private void loadModelFromFile(){
        mFileCm.setFileFilter(modFilter);
        int returnVal = mFileCm.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = mFileCm.getSelectedFile();
            try{
                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
                ((BBSceneGrid)app).loadExternalModel(file.getName(), file.getParent());
                
                //Load Entity list
//                modelEntity.clear();
//                for (int i=0; i<((BBSceneGrid)app).entList.size(); i++) {
//                modelEntity.add(i, ((BBSceneGrid)app).entList.get(i).getObjectName());
                modelEntity.addElement(((BBSceneGrid)app).selectedEntity);
                
                // Load Geometries list
                modelGeo.clear();
                for (int i=0; i<BBWorldManager.getInstance().getEntity(((BBSceneGrid)app).selectedEntity).getAllGeometries().toArray().length; i++) {
                modelGeo.add(i, BBWorldManager.getInstance().getEntity(((BBSceneGrid)app).selectedEntity).getAllGeometries().get(i).getName());
                
                }                
            }catch (IOException ex){}
            
        }
        
        mFileCm.setSelectedFile(null);
    }

    private void loadDiffuseTexture(){
        mFileCm.setFileFilter(texFilter);
        int returnVal = mFileCm.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = mFileCm.getSelectedFile();
//            mFileCt.setCurrentDirectory(file);
            try{
                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
                int[] selIndexes = listGeo.getSelectedIndices();
                if (selIndexes != null && ((BBSceneGrid)app).selectedEntity != null) {
                List<String> strLst = new ArrayList<String>();
                for(int i : selIndexes){
                    strLst.add(modelGeo.get(i).toString());
                }
                ((BBSceneGrid)app).loadDiffuseTexture(file.getName(), file.getParent(), strLst);
                }
            }catch (IOException ex){}
        }
        
        mFileCm.setSelectedFile(null);
    }  
    
    private void loadNormalTexture(){
        mFileCm.setFileFilter(texFilter);
        int returnVal = mFileCm.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = mFileCm.getSelectedFile();
//            mFileCt.setCurrentDirectory(file);
            try{
                mLogArea.append("Loading file : " + file.getCanonicalPath() +"\n");
                int[] selIndexes = listGeo.getSelectedIndices();
                if (selIndexes != null && ((BBSceneGrid)app).selectedEntity != null) {
                List<String> strLst = new ArrayList<String>();
                for(int i : selIndexes){
                    strLst.add(modelGeo.get(i).toString());
                }
                ((BBSceneGrid)app).loadNormalTexture(file.getName(), file.getParent(), strLst);
                }
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

    public void valueChanged(ListSelectionEvent lse) {

      JList lst = (JList) lse.getSource();

      // If Entity is changed in the Entitylist
      if (lst.equals(listEntity)) {
         ((BBSceneGrid)app).selectedEntity = (String) listEntity.getModel().getElementAt(lst.getSelectedIndex());

          // Load Geometries list
          modelGeo.clear();
          for (int i=0; i<BBWorldManager.getInstance().getEntity(((BBSceneGrid)app).selectedEntity).getAllGeometries().toArray().length; i++) {
          modelGeo.add(i, BBWorldManager.getInstance().getEntity(((BBSceneGrid)app).selectedEntity).getAllGeometries().get(i).getName());
                } 
          
      }      
//      if (!lse.getValueIsAdjusting())  System.out.println(lst.getSelectedIndices().length);
    }
}
