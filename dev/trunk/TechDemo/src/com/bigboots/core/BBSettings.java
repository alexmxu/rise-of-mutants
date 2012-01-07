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
package com.bigboots.core;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;

/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBSettings {
    private static BBSettings instance = new BBSettings();

    public static BBSettings getInstance() { 
        return instance; 
    }
    
    
    protected boolean showSettings = true;
    protected AppSettings settings;
    protected boolean loadFromRegistry = false;
    
    private BBSettings(){

        settings = new AppSettings(true);
        loadFromRegistry = true;
        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadFromRegistry)) {
                return;
            }
        }
    }
    
    public void showDialog(boolean val){
        showSettings = val;
    }
    
    public void loadFromContext(JmeContext contxt){
        settings = contxt.getSettings();
    }
    
    public AppSettings getSettings(){
        return settings;
    }
}
