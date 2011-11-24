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

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBAudioComponent extends AudioNode implements BBComponent{
    public BBAudioComponent(){
        super();
    }
    
    public BBAudioComponent(AssetManager assetManager, String name, boolean stream){
        super(assetManager, name, stream, false);
    }
    
    public Type getType(){
        return Type.AUDIO;
    }
    
    public Family getFamily(){
        return Family.VISUAL;
    }
}
