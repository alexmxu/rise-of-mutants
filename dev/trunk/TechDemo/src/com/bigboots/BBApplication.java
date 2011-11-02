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
import com.bigboots.input.BBInputManager;
import com.jme3.input.controls.ActionListener;


/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBApplication {
    public static final String INPUT_MAPPING_EXIT = "SIMPLEAPP_Exit";
    public static final String INPUT_MAPPING_CAMERA_POS = "SIMPLEAPP_CameraPos";
    public static final String INPUT_MAPPING_MEMORY = "SIMPLEAPP_Memory";
    public static final String INPUT_MAPPING_HIDE_STATS = "SIMPLEAPP_HideStats";
    
    private BBEngineSystem engineSystem;
    
    private AppActionListener actionListener = new AppActionListener();
    private class AppActionListener implements ActionListener {
      public void onAction(String name, boolean value, float tpf) {
          if (!value) {
              return;
          }

          if (name.equals(INPUT_MAPPING_EXIT)) {
              stop();
          } 
      }
    }
    
    public void init(){
        engineSystem = new BBEngineSystem();
        engineSystem.create();
    }
    /*
     * Main game loop.
     */
    public void run(){
        this.init();
        engineSystem.initialize();
        BBInputManager.getInstance().init(engineSystem);
         BBInputManager.getInstance().getInputManager().addListener(actionListener, INPUT_MAPPING_EXIT,INPUT_MAPPING_CAMERA_POS, INPUT_MAPPING_MEMORY, INPUT_MAPPING_HIDE_STATS);
    }
     
    public void stop(){
        engineSystem.stop(false);
    }
}
