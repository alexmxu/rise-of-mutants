package com.bigboots.input;

import com.bigboots.components.BBAnimComponent;
import com.bigboots.components.BBAudioComponent;
import com.bigboots.components.BBEntity;
import com.bigboots.components.BBMonsterManager;
import com.bigboots.components.BBPlayerManager;
import com.bigboots.core.BBSceneManager;
import com.bigboots.gui.BBGuiManager;
import com.bigboots.gui.BBProgressbarController;
import com.bigboots.input.BBPlayerActions;
import com.jme3.animation.LoopMode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
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
    private Transform bulletTrans;
    private float timer2;
    private Vector3f frontVec;
    private boolean work = true;
    private BoundingVolume bv;
    private ParticleEmitter effect;
    private BBAudioComponent expSound;    
    
    BBPlayerBulletControl (Geometry arg1, BBPlayerActions arg2){

        geooMove = arg1;
        sbc = arg2;

        prepareEffect();
        expSound = new BBAudioComponent();
        expSound.setSoundName("Sounds/explosionLarge2.ogg", false);
        expSound.setLooping(false);
        expSound.setVolume(10);        
        
        
//Approach 1        
// frontVec = geooMove.getLocalRotation().getRotationColumn(2).normalize();        

//Approach 2        
frontVec = geooMove.getWorldRotation().mult(Vector3f.UNIT_Z).normalize();

    }
    
    
    private void prepareEffect() {
        int COUNT_FACTOR = 1;
        float COUNT_FACTOR_F = 1f;
        effect = new ParticleEmitter("Flame", Type.Triangle, 32 * COUNT_FACTOR);
        effect.setSelectRandomImage(true);
        effect.setStartColor(new ColorRGBA(1f, 0.4f, 0.05f, (float) (1f / COUNT_FACTOR_F)));
        effect.setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
        effect.setStartSize(1.3f);
        effect.setEndSize(2f);
        effect.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        effect.setParticlesPerSec(0);
        effect.setGravity(0, -5f, 0);
        effect.setLowLife(.4f);
        effect.setHighLife(.5f);
        effect.setImagesX(2);
        effect.setImagesY(2);
        Material mat = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", BBSceneManager.getInstance().getAssetManager().loadTexture("Effects/Explosion/flame.png"));
        mat.setReceivesShadows(false);
        effect.setMaterial(mat);
        effect.setEnabled(false);
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
            
            if (entity.equals(BBPlayerManager.getInstance().getMainPlayer().getObjectName()) == false) {

            geooMove.removeFromParent();

            // Search for monster collisions
            BBEntity monster = BBMonsterManager.getInstance().getMonster(entity);
            if (monster != null) {
                int health = (Integer) monster.getSkills("HEALTH");
                BBGuiManager.getInstance().getNifty().getScreen("hud").findControl("enemy_progress", BBProgressbarController.class).setProgress(health / 100.0f);
                health = health - 10;

                if(health <= 0){
                    monster.stopAllAudio();
                    monster.setSkills("HEALTH", 0);
                    monster.setEnabled(false);
                    monster.getComponent(BBAnimComponent.class).getChannel().setAnim("mutant_death", 0.50f);
                    monster.getComponent(BBAnimComponent.class).getChannel().setLoopMode(LoopMode.DontLoop);
                }else{
                    monster.setSkills("HEALTH", health);
                }
              }
            
            effect.setLocalTranslation(geooMove.getLocalTranslation());
            BBSceneManager.getInstance().getRootNode().attachChild(effect);
            effect.setEnabled(true);
            effect.emitAllParticles();
            expSound.play();

            //if destroy control meshes
            if (timer2 > 2.5f) {
            BBSceneManager.getInstance().getRootNode().detachChild(geooMove);
            BBSceneManager.getInstance().getRootNode().detachChild(effect);
            effect.setEnabled(false);
            effect = null;
            expSound.stop();
            expSound.destroy();
            expSound = null;
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
            effect.setEnabled(false);
            effect = null;
            expSound.stop();
            expSound.destroy();
            expSound = null;
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
    

