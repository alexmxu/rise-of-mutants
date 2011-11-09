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
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.core.BBUpdateManager;
import com.bigboots.input.BBInputManager;
import com.bigboots.states.BBInGameState;
import com.bigboots.states.BBMainMenuState;
import com.bigboots.states.BBStateManager;
import com.jme3.system.SystemListener;

import com.jme3.input.controls.ActionListener;



/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBApplication implements SystemListener {
        
    private BBEngineSystem engineSystem;
    
    private AppActionListener actionListener = new AppActionListener();
    private class AppActionListener implements ActionListener {
      public void onAction(String name, boolean value, float tpf) {
          if (!value) {
              return;
          }

          if (name.equals(BBGlobals.INPUT_MAPPING_EXIT)) {
              stop();
          } 
      }
    }
    
    
    /*
     * Main game loop.
     */
    public void run(){
        BBSettings.getInstance();
        
        engineSystem = new BBEngineSystem();
        engineSystem.create();
        engineSystem.setContextListener(this);
    }
     
    public void stop(){
        engineSystem.stop(false);
    }
    /**
     * Callback to indicate the application to initialize. This method
     * is called in the GL/Rendering thread so any GL-dependent resources
     * can be initialized.
     */
    public void initialize(){
        //set up scene by initializing rootnode
        BBSceneManager.getInstance().init();
        BBUpdateManager.getInstance();      
        //State
        BBStateManager.getInstance().init(engineSystem);
        
        // aquire settings config from the context
        BBSettings.getInstance().loadFromContext(engineSystem.getContext());
        
        //init timer and render
        engineSystem.initialize();
        
        //init input and listener
        BBInputManager.getInstance().init(engineSystem);
           
        BBInputManager.getInstance().getInputManager().addListener(actionListener, BBGlobals.INPUT_MAPPING_EXIT,BBGlobals.INPUT_MAPPING_CAMERA_POS, BBGlobals.INPUT_MAPPING_MEMORY, BBGlobals.INPUT_MAPPING_HIDE_STATS);
        
        
        BBMainMenuState menu = new BBMainMenuState();
        BBStateManager.getInstance().attach(menu);
  
    }

    /**
     * Callback to update the application state, and render the scene
     * to the back buffer.
     */
    public void update(){
        float tpf = engineSystem.getTimer().getTimePerFrame();
        
        BBStateManager.getInstance().update(tpf);
        
        //update all updater : rootnode, input, etc
        BBUpdateManager.getInstance().update(tpf);
        
        //Update RootNode
        BBSceneManager.getInstance().update(tpf);
        
        BBStateManager.getInstance().render(engineSystem.getRenderManager());
        
        //update state of the scene graph after rootNode.updateGeometricState() call
        engineSystem.update();
        
        BBStateManager.getInstance().postRender();
    }
    
    /**
     * Called to notify the application that the resolution has changed.
     * @param width
     * @param height
     */
    public void reshape(int width, int height){
        engineSystem.reshape(width, height);
    }


    /**
     * Called when the user requests to close the application. This
     * could happen when he clicks the X button on the window, presses
     * the Alt-F4 combination, attempts to shutdown the process from 
     * the task manager, or presses ESC. 
     * @param esc If true, the user pressed ESC to close the application.
     */
    public void requestClose(boolean esc){
        engineSystem.requestClose(esc);
    }

    /**
     * Called when the application gained focus. The display
     * implementation is not allowed to call this method before
     * initialize() has been called or after destroy() has been called.
     */
    public void gainFocus(){
        //autoflush the frame
        engineSystem.gainFocus();
        //Reset Input
        BBInputManager.getInstance().resetInput();
    }

    /**
     * Called when the application lost focus. The display
     * implementation is not allowed to call this method before
     * initialize() has been called or after destroy() has been called.
     */
    public void loseFocus(){
        engineSystem.loseFocus();
    }

    /**
     * Called when an error has occured. This is typically
     * invoked when an uncought exception is thrown in the render thread.
     * @param errorMsg The error message, if any, or null.
     * @param t Throwable object, or null.
     */
    public void handleError(String errorMsg, Throwable t){
        engineSystem.handleError(errorMsg, t);
    }

    /**
     * Callback to indicate that the context has been destroyed (either
     * by the user or requested by the application itself). Typically
     * cleanup of native resources should happen here. This method is called
     * in the GL/Rendering thread.
     */
    public void destroy(){
        BBInputManager.getInstance().destroyInput();
        engineSystem.destroy();
    }
}
