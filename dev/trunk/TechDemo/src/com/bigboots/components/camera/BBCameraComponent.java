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
package com.bigboots.components.camera;

import com.bigboots.components.BBComponent;
import com.bigboots.components.BBComponent.CompFamily;
import com.bigboots.components.BBComponent.CompType;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBCameraComponent implements BBComponent{
    public enum CamMode{
        NONE,
        FPS,
        SIDE,
        RTS,
        CHASE,
        FIXED,
        FREE
    }
    
    protected CamMode mCameraMode;    
    protected String mCameraName;
    
    public BBCameraComponent(String name){
        mCameraName = name;
    }
    
    public CamMode getCamMode(){
        return mCameraMode;
    }    
    
    public String getCamName(){
        return mCameraName;
    }
    
    public CompType getType(){
        return CompType.CAMERA;
    }
    
    public CompFamily getFamily(){
        return CompFamily.VISUAL;
    }  
}
