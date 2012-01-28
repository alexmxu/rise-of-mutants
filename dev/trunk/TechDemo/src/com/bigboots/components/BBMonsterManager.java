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

import com.bigboots.BBWorldManager;
import com.bigboots.components.BBCollisionComponent.ShapeType;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.animation.BBAnimManager;
import com.bigboots.audio.BBAudioManager;
import com.bigboots.gui.BBGuiManager;
import com.bigboots.gui.BBProgressbarController;
import com.bigboots.physics.BBPhysicsManager;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.LoopMode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.HashMap;
import java.util.List;

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

        BBEntity tmpEnt = mapEnemies.get(name);
        return tmpEnt;
        
//        if(mapEnemies.containsKey(name)){
//            BBEntity tmpEnt = mapEnemies.get(name);
//            return tmpEnt;
//        }else{
//            throw new IllegalStateException("Try retreiving an unexisting monster.\n"
//                    + "Problem spatial name: " + name);
//        }
        
    }
    
    public void removeMonster(String name){
        BBEntity mon = mapEnemies.get(name);
        mon.getComponent(BBControlComponent.class).setEnable(false);
        BBPhysicsManager.getInstance().getPhysicsSpace().remove(mon.getComponent(BBNodeComponent.class));        
        mapEnemies.remove(mon);
        BBWorldManager.getInstance().removeEntity(name);
    }
    
    public void removeAllMonster(){
        
    }
    
    public void createMonter(String name, String file, Vector3f position,Vector3f posOffset, float scaleMonster){
        //*******************************************
        //TEST AND LOAD ENEMY WITH ENTITY SYSTEM
        //set up out enemy object entity and put it in scene
        BBEntity mEnemy = new BBEntity(name);
        mEnemy.setObjectTag(BBObject.ObjectTag.MONSTER);
        BBNodeComponent node = mEnemy.addComponent(CompType.NODE);
        mEnemy.loadModel(file);
        node.scale(scaleMonster);
        node.setLocalTranslation(position);
        node.getChild(0).setLocalTranslation(posOffset);
        mEnemy.attachToRoot();
        
        //Set up animation component      
        //mEnemy.createAnimation();
        BBAnimComponent anim = mEnemy.addComponent(CompType.ANIMATION);
        anim.getChannel().setAnim("mutant_idle");
        anim.getChannel().setLoopMode(LoopMode.Loop);
        mEnemy.getComponent(BBAnimComponent.class).getChannel().setSpeed(1f);
        
        //Set up physic controler component
        CollisionShape shape = BBPhysicsManager.getInstance().createPhysicShape(ShapeType.CAPSULE, mEnemy.getComponent(BBNodeComponent.class), 0.6f, 1.0f);
        BBCollisionComponent colCp = mEnemy.addComponent(CompType.COLSHAPE);
        shape.setMargin(0.1f);
        colCp.attachShape(shape);
        
               
        CharacterControl eControler = (CharacterControl) BBAnimManager.getInstance().createControl(BBControlComponent.ControlType.CHARACTER, mEnemy); 
        eControler.setJumpSpeed(20);
        eControler.setFallSpeed(50);
        eControler.setGravity(45);
        eControler.setPhysicsLocation(position);
        eControler.setUseViewDirection(true);
        BBControlComponent ctrlCp = mEnemy.addComponent(CompType.CONTROLLER);
        ctrlCp.setControlType(BBControlComponent.ControlType.CHARACTER);
        ctrlCp.attachControl(eControler);
        mEnemy.getComponent(BBNodeComponent.class).addControl(eControler);
        
        BBPhysicsManager.getInstance().getPhysicsSpace().add(mEnemy.getComponent(BBNodeComponent.class));
                
        //Set up enemy's sound component
        //Define the listener
        BBListenerComponent lst = mEnemy.addComponent(CompType.LISTENER);
        lst.setLocation(mEnemy.getComponent(BBNodeComponent.class).getWorldTranslation());
        BBAudioManager.getInstance().getAudioRenderer().setListener(lst);
        //Create associated audio
        BBAudioComponent audnde = new BBAudioComponent();
        audnde.setSoundName("Sounds/growling1.wav", false);
        audnde.setLooping(true);
        audnde.setVolume(0.05f);
        mEnemy.addAudio("GROWLING", audnde);
        
        this.addMonster(mEnemy);
        //Also add the entity to the world
        BBWorldManager.getInstance().addEntity(mEnemy);
    }
    
    public void update(float tpf){
        //*************************************************
        
        Vector3f humanPos = BBPlayerManager.getInstance().getMainPlayer().getComponent(BBNodeComponent.class).getLocalTranslation().clone();
        Quaternion newRot = new Quaternion().fromAngleAxis(FastMath.rand.nextFloat()*2-.5f, Vector3f.UNIT_Y);
        
        // Update Enemies
        for(BBEntity object:mapEnemies.values()){
            if(object.isEnabled()){
                BBNodeComponent mNode = object.getComponent(BBNodeComponent.class);
                AnimChannel mChannel = object.getComponent(BBAnimComponent.class).getChannel();
                CharacterControl mCharCtrl = mNode.getControl(CharacterControl.class);
                        
                humanPos.y = mNode.getLocalTranslation().y;            
                mNode.lookAt(humanPos,Vector3f.UNIT_Y);
                mNode.getLocalRotation().slerp(newRot,tpf);
                //System.out.println("**** POS : "+humanPos.toString());           
                float dist = humanPos.distance(mNode.getLocalTranslation());
                if(dist > 4 && dist < 20){      
                    mCharCtrl.setViewDirection(mNode.getLocalRotation().mult(Vector3f.UNIT_Z));            
                   mCharCtrl.setWalkDirection(mNode.getLocalRotation().mult(Vector3f.UNIT_Z).multLocal(tpf * 4));
                    if(!mChannel.getAnimationName().equals("mutant_base_walk"))
                    {
                        object.getAudio("GROWLING").stop();
                        //a.getChild(0).getControl(AnimControl.class).getChannel(0).setAnim("RunTop", 0.50f); // TODO: Must activate "RunBase" after a certain time.                    
                        mChannel.setAnim("mutant_base_walk", 0.50f);
                        mChannel.setLoopMode(LoopMode.Loop);
                    }
                    // Workaround
                    if(mChannel.getSpeed()!=smallManSpeed){
                        mChannel.setSpeed(smallManSpeed);
                    }            
                }  else if(dist > 20){      
                    if(!mChannel.getAnimationName().equals("mutant_idle"))
                    {
                    mCharCtrl.setWalkDirection(Vector3f.ZERO);
                    mChannel.setAnim("mutant_idle", 0.50f);
                    mChannel.setLoopMode(LoopMode.Loop);
                    }
                }
                else if (dist < 4)
                {
                    mCharCtrl.setWalkDirection(Vector3f.ZERO);
                    if(!mChannel.getAnimationName().equals("mutant_strike"))
                    {
                        mChannel.setAnim("mutant_strike", 0.05f);
                        mChannel.setLoopMode(LoopMode.Loop);
                        object.getAudio("GROWLING").play();
                    }
                }

                // is monster is dead
                int health;
                health = (Integer)object.getSkills("HEALTH");                
                if (health <= 0) {
                    mCharCtrl.setWalkDirection(Vector3f.ZERO);
                    health = 0;
                    object.stopAllAudio();
                    object.setSkills("HEALTH", 0);
                    object.setEnabled(false);
                    object.getComponent(BBAnimComponent.class).getChannel().setAnim("mutant_death", 0.50f);
                    object.getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                    object.getComponent(BBControlComponent.class).setEnable(false);
                    BBPhysicsManager.getInstance().getPhysicsSpace().remove(object.getComponent(BBNodeComponent.class));
                    BBGuiManager.getInstance().getNifty().getScreen("hud").findControl("enemy_progress", BBProgressbarController.class).setProgress(health / 100.0f);
                    
                    // this is just for a while to remove collision detection from dead monsters
                    List <Geometry> geoList = object.getAllGeometries();
                    for (Geometry geo : geoList){
                        Geometry geoChange = geo;   
                        geoChange.setUserData("entityName", "DEAD_" + geoChange.getUserData("entityName"));  
                        BBPhysicsManager.getInstance().removePhysic(object.getComponent(BBNodeComponent.class));
                        //BBControlComponent objControl = object.getComponent(BBControlComponent.class).getControl();
                    }
                    
                    object.setEnabled(false);    
                }  
            }//is enable
        }//end for
    }
}
