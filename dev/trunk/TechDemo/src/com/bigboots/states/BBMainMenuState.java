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
package com.bigboots.states;

import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.input.BBInputManager;
import com.jme3.scene.Node;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
//import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBMainMenuState extends BBAbstractState implements ScreenController {
    protected Node guiNode = new Node("Gui Node");
    private Nifty mNifty;
    protected ViewPort guiViewPort;
    private NiftyJmeDisplay niftyDisplay;
    
    @Override
    public void initialize(BBEngineSystem engineSystem) {
        super.initialize(engineSystem);
     
        // Create a new cam for the gui
        Camera guiCam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        guiViewPort = engineSystem.getRenderManager().createPostView("Gui Default", guiCam);
        guiViewPort.setClearFlags(false, false, false);
        
        niftyDisplay = new NiftyJmeDisplay(BBSceneManager.getInstance().getAssetManager(),
                                                          BBInputManager.getInstance().getInputManager(),
                                                          null,
                                                          guiViewPort);
        mNifty = niftyDisplay.getNifty();

        mNifty.fromXml("Interface/mainmenu.xml", "start");

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        guiViewPort.attachScene(guiNode);
        BBInputManager.getInstance().getInputManager().setCursorVisible(true);

        /*
        //Load first scene and camera
        Camera cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        
        ViewPort vp = engineSystem.getRenderManager().createMainView("TEST", cam);
        vp.setClearFlags(true, true, true);
        BBSceneManager.getInstance().setViewPort(vp);
   
        BBSceneManager.getInstance().setupLight();
        BBSceneManager.getInstance().createSky();
         */
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);

        guiNode.updateLogicalState(tpf);
        guiNode.updateGeometricState();
    }
    @Override
    public void stateAttached() {
        super.stateAttached();
        
    }

    @Override
    public void stateDetached() {
        super.stateDetached();
        //Clean all scene
        BBInputManager.getInstance().getInputManager().setCursorVisible(false);
        
        niftyDisplay.cleanup();
        guiNode.detachAllChildren();
        guiViewPort.clearScenes();
               
    }
    
    public void bind(Nifty nifty, Screen screen){
        mNifty = nifty;
    }

    public void onStartScreen(){
        
    }

    public void onEndScreen(){
        
    }
    
       /** custom methods */ 
    public void startGame() {     
        System.out.println("startGame");
        //TODO : Next time use message notification to notify the change
        BBStateManager.getInstance().detach(this);
        // switch to another screen
        mNifty.gotoScreen("null"); 
        //Change Game state
        BBInGameState ingame = new BBInGameState();
        BBStateManager.getInstance().attach(ingame);
        
    }

    public void optionGame() {
        System.out.println("optionGame");

    }
    
    public void quitGame() {
        System.out.println("quitGame");
        
    }
}
