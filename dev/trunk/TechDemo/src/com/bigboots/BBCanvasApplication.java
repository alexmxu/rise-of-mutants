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
package com.bigboots;

import com.bigboots.core.BBEngineSystem;
import com.bigboots.testscene.BBSimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBCanvasApplication {
    protected BBEngineSystem engineSystem;
    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static BBApplication app;
    private static JFrame frame;
    //private static Container canvasPanel1, canvasPanel2;
    //private static Container currentPanel;
    //private static JTabbedPane tabbedPane;
    private static final String appClass = "bigboots.editor.BBSceneGrid";
    
    private static JPanel canvasPanel;
    private static JPanel optionPanel;
    /*
     * Main game loop.
     */
    public void start(){
        JmeFormatter formatter = new JmeFormatter();

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);

        Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
        Logger.getLogger("").addHandler(consoleHandler);
        
        createCanvas(appClass);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                JPopupMenu.setDefaultLightWeightPopupEnabled(false);

                createFrame();
                
                //currentPanel.add(canvas, BorderLayout.CENTER);
                frame.pack();
                startApp();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        
        
    }
    
    private static void createCanvas(String appClass){
        AppSettings settings = new AppSettings(true);
        settings.setWidth(800);
        settings.setHeight(600);

        try{
            Class<? extends BBApplication> clazz = (Class<? extends BBApplication>) Class.forName(appClass);
            app = clazz.newInstance();
        }catch (ClassNotFoundException ex){
            System.out.println("CLASS NOT FOUND");
            ex.printStackTrace();
        }catch (InstantiationException ex){
            System.out.println("INSTANTIATION EXCEPTION");
            ex.printStackTrace();
        }catch (IllegalAccessException ex){
            System.out.println("ILLEGAL ACCESS EXCEPTION");
            ex.printStackTrace();
        }
        
        app.getEngine().setPauseOnLostFocus(false);
        //app.setNewSettings(settings);
        app.initCanvas();
        //app.getEngine().startCanvas();

        context = (JmeCanvasContext) app.getEngine().getContext();
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());
    }
    
    private static void createFrame(){
        frame = new JFrame("BBModel Viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setFont(new Font("Arial", Font.PLAIN, 12));
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                app.getEngine().stop();
            }
        });

        createTabs();
        createMenu();
    }
    
    public static void startApp(){
        app.getEngine().startCanvas();
        app.getEngine().enqueue(new Callable<Void>(){
            public Void call(){
                if (app instanceof BBSimpleApplication){
                    BBSimpleApplication simpleApp = (BBSimpleApplication) app;
                    //simpleApp.getFreeCamera().setDragToRotate(true);
                }
                return null;
            }
        });
        
    }
    
    private static void createTabs(){
/*        tabbedPane = new JTabbedPane();
        
        canvasPanel1 = new JPanel();
        canvasPanel1.setLayout(new BorderLayout());
        tabbedPane.addTab("jME3 Canvas 1", canvasPanel1);
        
        canvasPanel2 = new JPanel();
        canvasPanel2.setLayout(new BorderLayout());
        tabbedPane.addTab("jME3 Canvas 2", canvasPanel2);
        
        frame.getContentPane().add(tabbedPane);
        
        currentPanel = canvasPanel1;
 */       
        // Panel fuer Glide Fenster
        canvasPanel = new JPanel();
        canvasPanel.setLayout(new BorderLayout());
        canvasPanel.add(canvas, BorderLayout.CENTER);

        // Panel fuer Tabs und Eigenschaften
        optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(2,1));

        // Die Angabe einer Minimumsize bei SplitPanels ist Pflicht!
        optionPanel.setMinimumSize(new Dimension(30,30));	
        optionPanel.setPreferredSize(new Dimension(200,10));
        canvasPanel.setMinimumSize(new Dimension(100,50));

        // Das SplitPanel wird in optionPanel (links) und canvasPanel (rechts) unterteilt
        JSplitPane split = new JSplitPane();
        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(canvasPanel);
        split.setRightComponent(optionPanel);

        frame.add(split, BorderLayout.CENTER);        
    }
    
    private static void createMenu(){
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);
        
        final JMenuItem itemOpen = new JMenuItem("Open");
        menuFile.add(itemOpen);
        itemOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        final JMenuItem itemSave = new JMenuItem("Save");
        menuFile.add(itemSave);
        itemSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        JMenu menuEdit = new JMenu("Edit");
        menuBar.add(menuEdit);
        
        JMenu menuAsset = new JMenu("Asset");
        menuBar.add(menuAsset);

        final JMenuItem itemLoadModel = new JMenuItem("Load Model");
        menuAsset.add(itemLoadModel);
        itemLoadModel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
 /*       
        final JMenuItem itemHideCanvas = new JMenuItem("Hide Canvas");
        menuTortureMethods.add(itemHideCanvas);
        itemHideCanvas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (itemHideCanvas.getText().equals("Hide Canvas")){
                    canvas.setVisible(false);
                    itemHideCanvas.setText("Show Canvas");
                }else if (itemHideCanvas.getText().equals("Show Canvas")){
                    canvas.setVisible(true);
                    itemHideCanvas.setText("Hide Canvas");
                }
            }
        });
        
        final JMenuItem itemSwitchTab = new JMenuItem("Switch to tab #2");
        menuTortureMethods.add(itemSwitchTab);
        itemSwitchTab.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
               if (itemSwitchTab.getText().equals("Switch to tab #2")){
                   canvasPanel1.remove(canvas);
                   canvasPanel2.add(canvas, BorderLayout.CENTER);
                   currentPanel = canvasPanel2;
                   itemSwitchTab.setText("Switch to tab #1");
               }else if (itemSwitchTab.getText().equals("Switch to tab #1")){
                   canvasPanel2.remove(canvas);
                   canvasPanel1.add(canvas, BorderLayout.CENTER);
                   currentPanel = canvasPanel1;
                   itemSwitchTab.setText("Switch to tab #2");
               }
           } 
        });
        
        JMenuItem itemSwitchLaf = new JMenuItem("Switch Look and Feel");
        menuTortureMethods.add(itemSwitchLaf);
        itemSwitchLaf.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Throwable t){
                    t.printStackTrace();
                }
                SwingUtilities.updateComponentTreeUI(frame);
                frame.pack();
            }
        });
        
        JMenuItem itemSmallSize = new JMenuItem("Set size to (0, 0)");
        menuTortureMethods.add(itemSmallSize);
        itemSmallSize.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Dimension preferred = frame.getPreferredSize();
                frame.setPreferredSize(new Dimension(0, 0));
                frame.pack();
                frame.setPreferredSize(preferred);
            }
        });
        
        JMenuItem itemKillCanvas = new JMenuItem("Stop/Start Canvas");
        menuTortureMethods.add(itemKillCanvas);
        itemKillCanvas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentPanel.remove(canvas);
                app.getEngine().stop(true);

                createCanvas(appClass);
                currentPanel.add(canvas, BorderLayout.CENTER);
                frame.pack();
                startApp();
            }
        });
*/
        JMenuItem itemExit = new JMenuItem("Exit");
        menuFile.add(itemExit);
        itemExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                frame.dispose();
                app.getEngine().stop();
            }
        });
    }
    
}
