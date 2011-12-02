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
package com.bigboots.components;

//import com.jme3.bullet.control.PhysicsControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBControlComponent implements BBComponent{
    
    public enum ControlType {
        NONE,
        CHARACTER
    }
    
    private Control mControl;
    
    public BBControlComponent(){
        
    }
    
    public void attachControl(Control ctrl){
        mControl = ctrl;
    }
    
    public Control getControl(){
        return mControl;
    }
    
    public CompType getType(){
        return CompType.CONTROLLER;
    }
    
    public CompFamily getFamily(){
        return CompFamily.PHYSICS;
    }
}
