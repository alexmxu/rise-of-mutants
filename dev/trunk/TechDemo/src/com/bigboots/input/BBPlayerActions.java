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

import com.bigboots.BBGlobals;
import com.bigboots.components.BBAnimComponent;
import com.bigboots.components.BBAudioComponent;
import com.bigboots.components.BBNodeComponent;
import com.bigboots.components.BBPlayerManager;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBPlayerActions implements ActionListener, AnalogListener{
    private float time = 0;
    private int pressed=0;
    private static final Logger logger = Logger.getLogger(BBPlayerActions.class.getName());
    
    private static final class Directions{
        private static final Quaternion rot = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
        private static final Quaternion upDir = Quaternion.DIRECTION_Z.mult(rot);
        private static final Quaternion rightDir = upDir.mult(rot);
        private static final Quaternion downDir = rightDir.mult(rot);
        private static final Quaternion leftDir = downDir.mult(rot);
    }
        
    public void onAction(String binding, boolean keyPressed, float tpf) {
                    
            if(keyPressed == true){
                pressed++;
            }
            else{
                pressed--;                      
            }
            //System.out.println("******* BINDING PRESSED : "+ binding);
            
            if (keyPressed==false && BBPlayerManager.getInstance().isWalking()==true) {
                BBPlayerManager.getInstance().setIsWalking(false);
                if(!BBPlayerManager.getInstance().isJumping()){
                    logger.log(Level.INFO,"Character walking end.");
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("base_stand", 0.50f);          
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAudioComponent.class).stop();
                }
            }
            
            if(!keyPressed && binding.equals("MOUSE_LEFT")){
                BBPlayerManager.getInstance().setIsWalking(false);
                if(!BBPlayerManager.getInstance().isJumping()){
                    logger.log(Level.INFO,"******  Character Attack 1.");
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("shoot", 0.05f);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                }
                
            }
            if(!keyPressed && binding.equals("MOUSE_RIGHT")){
                BBPlayerManager.getInstance().setIsWalking(false);
                if(!BBPlayerManager.getInstance().isJumping()){
                    logger.log(Level.INFO,"******  Character Attack 2.");
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("strike_sword", 0.05f);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                }
                
            }
            if(keyPressed && !binding.equals("Jump") && !binding.equals("MOUSE_LEFT") && !binding.equals("MOUSE_RIGHT") && !BBPlayerManager.getInstance().isWalking()){
                BBPlayerManager.getInstance().setIsWalking(true);
                
                if(!BBPlayerManager.getInstance().isJumping()){
                    logger.log(Level.INFO,"Character walking init.");
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("run_01", 0.50f); // TODO: Must be activated after a certain time after "RunTop"
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.Loop);
              
                    //Trying to repeat the walk sound
                    time += tpf;
                    if (time > 0f) {
                        BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAudioComponent.class).play();
                        time = 0;
                    }
                }
            }
            
            if (binding.equals("Jump") &! BBPlayerManager.getInstance().isJumping() ) {
                if (keyPressed){
                    logger.log(Level.INFO,"Character jumping start.");
                    BBPlayerManager.getInstance().setIsJumping(true);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).jump();
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("jump", 0.50f); // TODO: Must be activated after a certain time after "JumpStart"
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                }
            }
        }//end onAAction
        
              
        public void onAnalog(String binding, float value, float tpf) {
            if(!BBPlayerManager.getInstance().isJumping()){
                if (binding.equals(BBGlobals.INPUT_MAPPING_LEFT)) {
                    Quaternion newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.leftDir, tpf*8);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);
                }
                else if (binding.equals("Right")) {
                    Quaternion newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.rightDir, tpf*8);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);        
                } else if (binding.equals("Up")) {
                    Quaternion newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.upDir, tpf*8);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);
                } else if (binding.equals("Down")) {
                    Quaternion newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.downDir, tpf*8);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);
                }

                if(BBPlayerManager.getInstance().isWalking()){
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setViewDirection(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getWorldRotation().mult(Vector3f.UNIT_Z));                     
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).getViewDirection().multLocal(.2f));                  
                }        
                else{
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                }
            }
        }//end onAnalog
}
