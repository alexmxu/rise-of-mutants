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
import com.jme3.math.Vector3f;
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
    private Nifty nifty;
    protected ViewPort guiViewPort;
    
    @Override
    public void initialize(BBEngineSystem engineSystem) {
        super.initialize(engineSystem);

        //Load first scene and camera
        Camera cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        
        ViewPort vp = engineSystem.getRenderManager().createMainView("TEST", cam);
        vp.setClearFlags(true, true, true);
        BBSceneManager.getInstance().setViewPort(vp);
        
        // Create a new cam for the gui
        Camera guiCam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        guiViewPort = engineSystem.getRenderManager().createPostView("Gui Default", guiCam);
        guiViewPort.setClearFlags(false, false, false);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(BBSceneManager.getInstance().getAssetManager(),
                                                          BBInputManager.getInstance().getInputManager(),
                                                          null,
                                                          guiViewPort);
        nifty = niftyDisplay.getNifty();

        nifty.fromXml("Interface/mainmenu.xml", "start");

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        guiViewPort.attachScene(guiNode);
        BBInputManager.getInstance().getInputManager().setCursorVisible(true);
        
        BBSceneManager.getInstance().setupLight();
        BBSceneManager.getInstance().createSky();

    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);

        guiNode.updateLogicalState(tpf);
        guiNode.updateGeometricState();
    }
    
    public void bind(Nifty nifty, Screen screen){
        
    }

    public void onStartScreen(){
        
    }

    public void onEndScreen(){
        
    }
    
       /** custom methods */ 
    public void startGame() {
        //mNifty.gotoScreen(nextScreen);  // switch to another screen
        System.out.println("startGame");
    }

    public void optionGame() {
        System.out.println("optionGame");
    }
    
    public void quitGame() {
        System.out.println("quitGame");
    }
}
