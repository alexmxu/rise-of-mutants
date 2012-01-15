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

import com.bigboots.components.BBCollisionComponent.ShapeType;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.animation.BBAnimManager;
import com.bigboots.audio.BBAudioManager;
import com.bigboots.core.BBSceneManager;
import com.bigboots.physics.BBPhysicsManager;
import com.bulletphysics.dynamics.RigidBody;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.HashMap;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBMonsterManager {
    private static BBMonsterManager instance = new BBMonsterManager();

    private BBMonsterManager() {
    }
    
    public static BBMonsterManager getInstance() { 
        return instance; 
    }
    
    private HashMap<String, BBEntity> mapEnemies = new HashMap<String, BBEntity>();
    //private String mMonsterID = new Long(0);
        // Temp workaround, speed is reset after blending.
    private float smallManSpeed = .6f;
    
    
    public void addMonster(BBEntity ent){
        //Add it the map of Enemies
        mapEnemies.put(ent.getObjectName(), ent);
    }
    
    public BBEntity getMonster(String name){
        
        if(mapEnemies.containsKey(name)){
            BBEntity tmpEnt = mapEnemies.get(name);
            return tmpEnt;
        }else{
            throw new IllegalStateException("Try retreiving an unexisting monster.\n"
                    + "Problem spatial name: " + name);
        }
        
    }
    
    public void createMonter(String name, String file, Vector3f pos){
        //*******************************************
        //TEST AND LOAD ENEMY WITH ENTITY SYSTEM
        //set up out enemy object entity and put it in scene
        BBEntity mEnemy = new BBEntity(name);
        mEnemy.mTag = BBObject.ObjectTag.MONSTER;
        BBNodeComponent node = mEnemy.addComponent(CompType.NODE);
        mEnemy.loadModel(file);
        node.scale(4);
        node.setLocalTranslation(pos);
        BBSceneManager.getInstance().addChild(mEnemy.getComponent(BBNodeComponent.class));
        
        //Set up animation component      
        //mEnemy.createAnimation();
        BBAnimComponent anim = mEnemy.addComponent(CompType.ANIMATION);
        anim.getChannel().setAnim("mutant_idle");
        anim.getChannel().setLoopMode(LoopMode.Loop);
        mEnemy.getComponent(BBAnimComponent.class).getChannel().setSpeed(1f);
        
        //Set up physic controler component
        CollisionShape shape = BBPhysicsManager.getInstance().createPhysicShape(ShapeType.CAPSULE, mEnemy);
        BBCollisionComponent colCp = mEnemy.addComponent(CompType.COLSHAPE);
        colCp.attachShape(shape);
               
        CharacterControl eControler = (CharacterControl) BBAnimManager.getInstance().createControl(BBControlComponent.ControlType.CHARACTER, mEnemy); 
        eControler.setJumpSpeed(20);
        eControler.setFallSpeed(30);
        eControler.setGravity(30);
        eControler.setPhysicsLocation(pos);
        eControler.setCcdMotionThreshold(0.5f);
        eControler.setUseViewDirection(true);
        BBControlComponent ctrlCp = mEnemy.addComponent(CompType.CONTROLLER);
        ctrlCp.setControlType(BBControlComponent.ControlType.CHARACTER);
        ctrlCp.attachControl(eControler);
        mEnemy.getComponent(BBNodeComponent.class).addControl(eControler);
        
        BBPhysicsManager.getInstance().getPhysicsSpace().addAll(mEnemy.getComponent(BBNodeComponent.class));
                
        //Set up enemy's sound component
        //Define the listener
        BBListenerComponent lst = mEnemy.addComponent(CompType.LISTENER);
        lst.setLocation(mEnemy.getComponent(BBNodeComponent.class).getWorldTranslation());
        BBAudioManager.getInstance().getAudioRenderer().setListener(lst);
        //Create associated audio
        BBAudioComponent audnde = new BBAudioComponent();
        audnde.setSoundName("Sounds/growling1.wav", false);
        audnde.setLooping(true);
        audnde.setVolume(1);
        mEnemy.addAudio("GROWLING", audnde);
        
        this.addMonster(mEnemy);
    }
    
    public void update(float tpf){
        //*************************************************
        // Update Enemies
        for(BBEntity object:mapEnemies.values()){
            if(object.isEnabled()){
                Vector3f humanPos = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalTranslation().clone();
                Quaternion newRot = new Quaternion().fromAngleAxis(FastMath.rand.nextFloat()*2-.5f, Vector3f.UNIT_Y);
                humanPos.y = object.getComponent(BBNodeComponent.class).getLocalTranslation().y;            
                object.getComponent(BBNodeComponent.class).lookAt(humanPos,Vector3f.UNIT_Y);
                object.getComponent(BBNodeComponent.class).getLocalRotation().slerp(newRot,tpf);
                //System.out.println("**** POS : "+humanPos.toString());           
                float dist = humanPos.distance(object.getComponent(BBNodeComponent.class).getLocalTranslation());
                if(dist > 4 && dist < 20){      
                    object.getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setViewDirection(object.getComponent(BBNodeComponent.class).getLocalRotation().mult(Vector3f.UNIT_Z));            
                    object.getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(object.getComponent(BBNodeComponent.class).
                            getLocalRotation().mult(Vector3f.UNIT_Z).mult(tpf * 12));
                    if(!object.getComponent(BBAnimComponent.class).getChannel().getAnimationName().equals("mutant_base_walk"))
                    {
                        object.getAudio("GROWLING").stop();
                        //a.getChild(0).getControl(AnimControl.class).getChannel(0).setAnim("RunTop", 0.50f); // TODO: Must activate "RunBase" after a certain time.                    
                        object.getComponent(BBAnimComponent.class).getChannel().setAnim("mutant_base_walk", 0.50f);
                        object.getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.Loop);
                    }
                    // Workaround
                    if(object.getComponent(BBAnimComponent.class).getChannel().getSpeed()!=smallManSpeed){
                        object.getComponent(BBAnimComponent.class).getChannel().setSpeed(smallManSpeed);
                    }            
                }  else if(dist > 20){      
                    if(!object.getComponent(BBAnimComponent.class).getChannel().getAnimationName().equals("mutant_idle"))
                    {
                    object.getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                    object.getComponent(BBAnimComponent.class).getChannel().setAnim("mutant_idle", 0.50f);
                    object.getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.Loop);
                    }
                }
                else if (dist < 4)
                {
                    object.getComponent(BBNodeComponent.class).getControl(CharacterControl.class).setWalkDirection(Vector3f.ZERO);
                    if(!object.getComponent(BBAnimComponent.class).getChannel().getAnimationName().equals("mutant_strike"))
                    {
                        object.getComponent(BBAnimComponent.class).getChannel().setAnim("mutant_strike", 0.05f);
                        object.getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.Loop);
                        object.getAudio("GROWLING").play();
                    }
                }//end if dist
            }//is enable
        }//end for
    }
}
