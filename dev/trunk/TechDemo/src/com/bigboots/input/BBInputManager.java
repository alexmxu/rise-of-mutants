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
package com.bigboots.input;

import com.bigboots.core.BBEngineSystem;
import com.bigboots.core.BBUpdateListener;
import com.bigboots.core.BBUpdateManager;
import com.jme3.input.JoyInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.TouchInput;
import com.jme3.input.InputManager;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.JmeContext.Type;

/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBInputManager implements BBUpdateListener{
    public static final String INPUT_MAPPING_EXIT = "SIMPLEAPP_Exit";
    public static final String INPUT_MAPPING_CAMERA_POS = "SIMPLEAPP_CameraPos";
    public static final String INPUT_MAPPING_MEMORY = "SIMPLEAPP_Memory";
    public static final String INPUT_MAPPING_HIDE_STATS = "SIMPLEAPP_HideStats";
    
    protected MouseInput mouseInput;
    protected KeyInput keyInput;
    protected JoyInput joyInput;
    protected TouchInput touchInput;
    protected InputManager inputManager;
    
    protected BBEngineSystem myEng;
    
    private static BBInputManager instance = new BBInputManager();

    private BBInputManager() {
    }
    
    public static BBInputManager getInstance() { 
        return instance; 
    }
        
    public void init(BBEngineSystem eng){
        
        myEng = eng;
        //init input
        mouseInput = myEng.getContext().getMouseInput();
        if (mouseInput != null)
            mouseInput.initialize();

        keyInput = myEng.getContext().getKeyInput();
        if (keyInput != null)
            keyInput.initialize();
        
        touchInput = myEng.getContext().getTouchInput();
        if (touchInput != null)
            touchInput.initialize();

        if (!myEng.getSettings().getBoolean("DisableJoysticks")){
            joyInput = myEng.getContext().getJoyInput();
            if (joyInput != null)
                joyInput.initialize();
        }

        inputManager = new InputManager(mouseInput, keyInput, joyInput, touchInput);
        
        if (myEng.getContext().getType() == Type.Display) {
            inputManager.addMapping(INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
        }

        inputManager.addMapping(INPUT_MAPPING_CAMERA_POS, new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping(INPUT_MAPPING_MEMORY, new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping(INPUT_MAPPING_HIDE_STATS, new KeyTrigger(KeyInput.KEY_F5));
        
        BBUpdateManager.getInstance().register(this);
    }
    
    public void update(float tpf){
        
        if (myEng.isInputEnabled()){
            inputManager.update(tpf);
        }
    }

    
    /**
     * @return the {@link InputManager input manager}.
     */
    public InputManager getInputManager(){
        return inputManager;
    }
}
