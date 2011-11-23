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

import com.jme3.animation.AnimControl;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBComponent extends BBObject{
    protected Node mTrasform;
    protected Geometry mDisplay;
    protected CollisionShape mRigidBody;
    protected Light mLight;
    protected AudioNode mAudio;
    protected AnimControl mAnimation;
    protected boolean mEnable;
    
    public BBComponent(String name){
        super(name);
        mTrasform = new Node(name);
    }
    
    
}
