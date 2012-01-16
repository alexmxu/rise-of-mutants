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
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.*;


/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBControlComponent implements BBComponent{
    
    public enum ControlType {
        NONE,
        CHARACTER
    }
    
    private PhysicsCollisionObject mControl;
    private ControlType mType;
    
    public BBControlComponent(){
        mType = ControlType.NONE;
    }
    
    public void attachControl(PhysicsCollisionObject ctrl){
        mControl = ctrl;
    }
    public void setControlType(ControlType tp){
        mType  = tp;
    }
    
    public PhysicsCollisionObject getControl(){
        if(mType.equals(ControlType.CHARACTER)){
            return (CharacterControl) mControl;
        }
        return null;
    }
    
    public CompType getCompType(){
        return CompType.CONTROLLER;
    }
    
    public CompFamily getCompFamily(){
        return CompFamily.PHYSICS;
    }
}
