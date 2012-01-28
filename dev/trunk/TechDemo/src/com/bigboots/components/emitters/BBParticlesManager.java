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
package com.bigboots.components.emitters;

import java.util.HashMap;
import java.util.List;
/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBParticlesManager {
    private static BBParticlesManager instance = new BBParticlesManager();

    private BBParticlesManager() {
    }
    
    public static BBParticlesManager getInstance() { 
        return instance; 
    }
    
    private HashMap<String, BBParticleEmitter> mapParticlesFx = new HashMap<String, BBParticleEmitter>();
    
    public void init(){
        //TODO : add  all standard particles to be use in game
        
        //create explosion ParticleFx
        BBExplosionFx expFx = new BBExplosionFx("FLAME");
        expFx.init();
        mapParticlesFx.put("FLAME", expFx);
    }
    
    public BBParticleEmitter getParticlesFx(String name){
        return mapParticlesFx.get(name);
    }
    
    public void destroy(){
        mapParticlesFx.clear();
    }
    
}
