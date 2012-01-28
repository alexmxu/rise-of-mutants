package com.bigboots.input;

import com.bigboots.components.BBEntity;
import com.bigboots.components.BBMonsterManager;
import com.bigboots.components.BBPlayerManager;
import com.bigboots.components.emitters.BBExplosionFx;
import com.bigboots.components.emitters.BBParticlesManager;
import com.bigboots.core.BBSceneManager;
import com.bigboots.gui.BBGuiManager;
import com.bigboots.gui.BBProgressbarController;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class BBPlayerBulletControl extends AbstractControl implements Savable, Cloneable{
    
    private BBPlayerActions sbc;
    private Geometry geooMove;
    private float timer2;
    private Vector3f frontVec;
    private boolean work = true;
    private BoundingVolume bv;
    private BBExplosionFx explosionFX;  
    
    BBPlayerBulletControl (Geometry arg1, BBPlayerActions arg2){

        geooMove = arg1;
        sbc = arg2;
        
        //Prepare explosion FX Particles
        explosionFX = (BBExplosionFx) BBParticlesManager.getInstance().getParticlesFx("FLAME");
       
       
        //Approach 1        
        // frontVec = geooMove.getLocalRotation().getRotationColumn(2).normalize();        
        //Approach 2        
        frontVec = geooMove.getWorldRotation().mult(Vector3f.UNIT_Z).normalize();

    }
    
    @Override
    protected void controlUpdate(float tpf) {

        if (work == true) {   
            timer2 += tpf;
            // Approach 1        
            //geooMove.setLocalTranslation(geooMove.getLocalTranslation().add(frontVec.multLocal(timer2))); 

            // Approach 2
            geooMove.move(frontVec.mult(30f*tpf));

            // Collision listener
            bv = geooMove.getWorldBound();
            CollisionResults results = new CollisionResults();
            BBSceneManager.getInstance().getRootNode().collideWith(bv, results);

            if (results.size() > 0) {

                Geometry closest = results.getClosestCollision().getGeometry();

                if(closest != null && closest.getUserData("entityName") != null) {

                    String entity = closest.getUserData("entityName");

                    if (entity.equals(BBPlayerManager.getInstance().getMainPlayer().getObjectName()) == false
                    && entity.indexOf("DEAD_") != 0 ) {
                        geooMove.removeFromParent();

                        // Search for monster collisions
                        BBEntity monster = BBMonsterManager.getInstance().getMonster(entity);
                        if (monster != null) {
                            int health = (Integer) monster.getSkills("HEALTH");
                            BBGuiManager.getInstance().getNifty().getScreen("hud").findControl("enemy_progress", BBProgressbarController.class).setProgress(health / 100.0f);
                            health = health - 10;
                            monster.setSkills("HEALTH", health);
                        }

                        explosionFX.setLocalTranslation(geooMove.getLocalTranslation());
                        BBSceneManager.getInstance().getRootNode().attachChild(explosionFX);
                        explosionFX.setEnabled(true);
                        explosionFX.startEmitFX();

                        //if destroy control meshes
                        if (timer2 > 2.5f) {
                            explosionFX.destroy();
                            geooMove.removeControl(this);
                            geooMove = null;
                            work = false;
                        }  
                    }
                }
            }

        // If bullet did not collided to a collidable object it destroys automatically
        if (timer2 > 2.7f) {

            BBSceneManager.getInstance().getRootNode().detachChild(geooMove);            
            explosionFX.destroy();
            geooMove.removeControl(this);
            geooMove = null;
            work = false;
        }
      }
    }

    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
      
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
    

