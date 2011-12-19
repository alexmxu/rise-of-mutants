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
package com.bigboots.gui;

import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.core.BBUpdateListener;
import com.bigboots.core.BBUpdateManager;
import com.bigboots.input.BBInputManager;
import com.jme3.scene.Node;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Spatial.CullHint;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBGuiManager implements BBUpdateListener{
    private static BBGuiManager instance = new BBGuiManager();

    private BBGuiManager() {
        
    }
    
    public static BBGuiManager getInstance() { 
        return instance; 
    }
    
    protected Node guiNode = new Node("Gui Node");
    private Nifty mNifty;
    protected ViewPort guiViewPort;
    private NiftyJmeDisplay niftyDisplay;
    
    private int progress = 0;
    private long start;
    private BBEngineSystem engineSystem;
    private boolean mEnableProgBar = false;
    
    public void init(BBEngineSystem eng){
        engineSystem = eng;
        start = engineSystem.getTimer().getTime();
        
        //Use for displaying Debug info
        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        // Create a new cam for the gui
        Camera guiCam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        guiViewPort = engineSystem.getRenderManager().createPostView("Gui Default", guiCam);
        guiViewPort.setClearFlags(false, false, false);

        niftyDisplay = new NiftyJmeDisplay(BBSceneManager.getInstance().getAssetManager(),
                                              BBInputManager.getInstance().getInputManager(),
                                              null,
                                              guiViewPort);
        mNifty = niftyDisplay.getNifty();

        mNifty.fromXml("Interface/mainmenu.xml", "null");
        //mNifty.setDebugOptionPanelColors(true);
        
        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        guiViewPort.attachScene(guiNode);
        
        BBUpdateManager.getInstance().register(this);
    }
    
    public Nifty getNifty() {
        return mNifty;
    }
    
    public void update(float tpf) {
        guiNode.updateLogicalState(tpf);
        guiNode.updateGeometricState();
        
        if(mEnableProgBar){
          long now = engineSystem.getTimer().getTime();
          if (now - start > 50) { // add one percent every 50 ms
            start = now;
            progress++;
            
            Screen mScreen = mNifty.getScreen("progress");
            mScreen.findControl("my-progress", BBProgressbarController.class).setProgress(progress / 100.0f);

            if (progress >= 100) {
              mEnableProgBar = false;
              mNifty.gotoScreen("null");
            }
          }
        }
    }
    
    public void enableProgressBar(boolean val){
        mEnableProgBar = val;
    }
    
    public void destroyGui(){
        niftyDisplay.cleanup();
        guiNode.detachAllChildren();
        guiViewPort.clearScenes();
    }
    
    public Node getGuiNode(){
        return guiNode;
    }
}
