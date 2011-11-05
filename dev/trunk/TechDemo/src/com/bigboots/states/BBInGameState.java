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
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBInGameState extends BBAbstractState {
    
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
        
        Spatial sinbadModel = BBSceneManager.getInstance().loadSpatial("Models/Sinbad/Sinbad.mesh.j3o");
        sinbadModel.setName("PLAYER");
        BBSceneManager.getInstance().addChild(sinbadModel);
        
        BBSceneManager.getInstance().setupLight();
        BBSceneManager.getInstance().createSky();
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);

        
    }

}
