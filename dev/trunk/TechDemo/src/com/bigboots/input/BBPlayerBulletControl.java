package com.bigboots.input;

import com.bigboots.core.BBSceneManager;
import com.bigboots.input.BBPlayerActions;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.export.Savable;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class BBPlayerBulletControl extends AbstractControl implements Savable, Cloneable{
    
    BBPlayerActions sbc;
    Geometry geooMove;
    Transform bulletTrans;
    float timer2;
    Vector3f frontVec;
    boolean work = true;
    BoundingVolume bv;
    
    
    BBPlayerBulletControl (Geometry arg1, BBPlayerActions arg2){
        //spaaMove = arg0;
        geooMove = arg1;
        sbc = arg2;
     //   geooMove.setLocalTransform(bulletTrans);

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

//        bv = geooMove.getWorldBound();
//        CollisionResults results = new CollisionResults();
//        sbc.shoBuRu.enemyNode.collideWith(bv, results);
//        
//        if (results.size() > 0) {
//        Spatial closest = results.getCollision(0).getGeometry();
//        
//        if(closest != null) {
//
//            closest.removeFromParent();
//            geooMove.removeControl(this);
//            sbc.shotsGroup.detachChild(geooMove);
//            geooMove = null;
//            work = false;
//        }
//                       
//        }

        
        if (timer2 > 2.5f) {
        
            geooMove.removeControl(this);
            BBSceneManager.getInstance().getRootNode().detachChild(geooMove);

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
    

