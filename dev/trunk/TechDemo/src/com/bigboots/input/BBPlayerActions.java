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
import com.bigboots.components.BBNodeComponent;
import com.bigboots.components.BBPlayerManager;
import com.bigboots.core.BBSceneManager;
import com.bigboots.physics.BBBulletPhysic;
import com.bigboots.physics.BBPhysicsManager;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBPlayerActions implements  ActionListener, AnalogListener{
    private Material matBullet;
    //bullet
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    //explosion
    //private ParticleEmitter effect;
    private Quaternion newRot;
    private float time = 0;
    private int pressed=0;
    private static final Logger logger = Logger.getLogger(BBPlayerActions.class.getName());
    private boolean shootBullets = false;
    int timeBullet = 0;
    BBPlayerBulletControl bulletMove;
    
    
    
    public BBPlayerActions(){
        prepareBullet();
    }

    
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
            
            //if(keyPressed && !binding.equals("Jump") && !binding.equals("MOUSE_LEFT") && !binding.equals("MOUSE_RIGHT") && !BBPlayerManager.getInstance().isWalking()){
            if(pressed==1 && keyPressed &! binding.equals("Jump")  && !BBPlayerManager.getInstance().isWalking()){
                BBPlayerManager.getInstance().setIsWalking(true);
                
                if(!BBPlayerManager.getInstance().isJumping()){
                    BBPlayerManager.getInstance().setIsWalking(true);
                    logger.log(Level.INFO,"Character walking init.");
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("run_01", 0.50f); // TODO: Must be activated after a certain time after "RunTop"
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.Loop);
              
                    //Trying to repeat the walk sound
                    time += tpf;
                    if (time > 0f) {
                        BBPlayerManager.getInstance().getMainPlayer().getAudio("STEP").play();
                        time = 0;
                    }
                }
            //} else if (keyPressed==false && BBPlayerManager.getInstance().isJumping()==false) {
            } else if (pressed==0 &! keyPressed &! binding.equals("Jump")) {
                BBPlayerManager.getInstance().setIsWalking(false);
                if(!BBPlayerManager.getInstance().isJumping()){
                    logger.log(Level.INFO,"Character walking end.");
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("base_stand", 0.50f);          
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                    BBPlayerManager.getInstance().getMainPlayer().getAudio("STEP").stop();
                }
            }
         /*   
            if(binding.equals("MOUSE_LEFT")){
                BBPlayerManager.getInstance().setIsWalking(false);
                if(!BBPlayerManager.getInstance().isJumping()){
                    logger.log(Level.INFO,"******  Character Attack 1.");
                    BBPlayerManager.getInstance().getMainPlayer().getAudio("FIRE").play();
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("shoot", 0.05f);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                    shootBullets = true;
                    bulletControl();
                    Geometry bulletx = bulletg.clone();
                    bulletx.addControl(bulletMove = new BBPlayerBulletControl(bulletx, this));
                    BBSceneManager.getInstance().getRootNode().attachChild(bulletx);
                }
                
            } else {
//                shootBullets = false;
            }
            if(keyPressed==false && binding.equals("MOUSE_RIGHT")){
                BBPlayerManager.getInstance().setIsWalking(false);
                if(!BBPlayerManager.getInstance().isJumping()){
                    logger.log(Level.INFO,"******  Character Attack 2.");
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setAnim("strike_sword", 0.05f);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                }
                
            }
          */
            
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
            //BBPlayerManager.getInstance().setIsJumping(false);
            
//            if (binding.equals("MOUSE_LEFT")) {
//                timeBullet += tpf;
//                    if (shootBullets = true) {
//                
//                System.out.println(timeBullet + "  ******* Bullet cteated");
//                if (timeBullet == 0.001) bulletControl();   
            
//                 }
//            }
            
            if(!BBPlayerManager.getInstance().isJumping()){
                //We inverse the key because the map is align on X axis        
                //left
                if (binding.equals("Down")) {
                    newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.leftDir, tpf*3);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);
                }//right
                else if (binding.equals("Up")) {
                    newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.rightDir, tpf*3);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);        

                }//up 
                else if (binding.equals("Left")) {
                    newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.upDir, tpf*3);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);
                } //down
                else if (binding.equals("Right")) {
                    newRot = new Quaternion().slerp(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalRotation(),Directions.downDir, tpf*3);
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).setLocalRotation(newRot);
                }

                if(BBPlayerManager.getInstance().isWalking()){
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setViewDirection(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getWorldRotation().mult(Vector3f.UNIT_Z));                     
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).getViewDirection().multLocal(tpf*10));                  
                }        
                else{
                    BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                }
            
            }
                            

        }//end onAnalog
        

        
   // private Mesh bullet;
 //   private Material matBullet;
    Geometry bulletg;
    private Transform bulletTrans;
    private Vector3f frontVec;
        private void prepareBullet() {
            bullet = new Sphere(8, 8, 0.2f, true, false);
        //    bullet.setTextureMode(TextureMode.Projected);
            matBullet = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            matBullet.setColor("Color", ColorRGBA.Yellow);
       //     matBullet.setColor("m_GlowColor", ColorRGBA.Orange);
            bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(matBullet);
            bulletg.setShadowMode(ShadowMode.Off);
            //BBPhysicsManager.getInstance().getPhysicsSpace().addCollisionListener(this);
        }
        
        private void bulletControl() {
            
            
        bulletTrans = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getWorldTransform();
        bulletg.setLocalTranslation(bulletTrans.getTranslation());
        bulletg.setLocalRotation(bulletTrans.getRotation());
        frontVec = bulletTrans.getRotation().mult(Vector3f.UNIT_Z).normalize();
      //  frontVec = frontVec.add(new Vector3f(0.5f,0.5f,0.5f)).normalize();
        bulletg.move(frontVec.mult(2f));
        

//        CharacterControl character = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class);
//            
//
//            Vector3f pos = character.getPhysicsLocation().add(character.getViewDirection().mult(8));
//            pos.y = pos.y + 1.0f;
//            pos.z = pos.z + 0.4f;
//            bulletg.setLocalTranslation(pos);
//            
            

        //    BBPhysicsManager.getInstance().getPhysicsSpace().add(bulletControl);
    }        
        
        
        
        // Commented as deprecated
//  private void prepareBullet() {
//            bullet = new Sphere(8, 8, 0.2f, true, false);
//            bullet.setTextureMode(TextureMode.Projected);
//            bulletCollisionShape = new SphereCollisionShape(0.3f);
//            matBullet = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//            matBullet.setColor("Color", ColorRGBA.Yellow);
//            matBullet.setColor("m_GlowColor", ColorRGBA.Orange);
//            
//            //BBPhysicsManager.getInstance().getPhysicsSpace().addCollisionListener(this);
//        }
//        
//        private void bulletControl() {
//            
//            CharacterControl character = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getControl(CharacterControl.class);
//            
//            Geometry bulletg = new Geometry("bullet", bullet);
//            bulletg.setMaterial(matBullet);
//            bulletg.setShadowMode(ShadowMode.Off);
//            Vector3f pos = character.getPhysicsLocation().add(character.getViewDirection().mult(10));
//            pos.y = pos.y + 1.0f;
//            pos.z = pos.z + 0.4f;
//            bulletg.setLocalTranslation(pos);
//            RigidBodyControl bulletControl = new BBBulletPhysic(bulletCollisionShape, 1);
//            bulletControl.setCcdMotionThreshold(0.1f);
//            Vector3f vec = character.getViewDirection().add(new Vector3f(0,0.05f,0)).normalize();
//            bulletControl.setLinearVelocity(vec.mult(20));
//            bulletg.addControl(bulletControl);
//            BBSceneManager.getInstance().addChild(bulletg);
//            BBPhysicsManager.getInstance().getPhysicsSpace().add(bulletControl);
//    } 
}
