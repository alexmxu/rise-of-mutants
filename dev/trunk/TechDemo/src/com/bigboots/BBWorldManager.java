/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigboots;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import com.bigboots.ai.util.NavMeshGenerator;
import com.bigboots.ai.navmesh.NavMesh;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.scene.control.Control;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
/**
 * 
 * @author Ulrich Nzuzi <ulrichnz@code.google.com>
 * @author Vemund Kvam <vekjeft@code.google.com>
 * 
 * A temporary accessor for information needed by many functions in game.
 * Mostly copy paste from MonkeyZone, but Network functions has been removed.
 * Will most like needed a thorough review.
 */
public class BBWorldManager extends AbstractAppState  {
    private HashMap<Long, Spatial> entities = new HashMap<Long, Spatial>();
    private NavMesh navMesh = new NavMesh();
    private Node rootNode;
    private Node worldRoot;
    private Application app;
    private AssetManager assetManager;
    private NavMeshGenerator generator = new NavMeshGenerator();
    private PhysicsSpace space;
    private List<Control> userControls = new LinkedList<Control>();
    
    
    public BBWorldManager(Application app, Node rootNode) {
        this.app = app;
        this.rootNode = rootNode;
        this.assetManager = app.getAssetManager();
        this.space = app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
    }

    /**
     * gets the entity with the specified id
     * @param id
     * @return
     */
    public Spatial getEntity(long id) {
        return entities.get(id);
    }

    /**
     * gets the entity belonging to a PhysicsCollisionObject
     * @param object
     * @return
     */
    public Spatial getEntity(PhysicsCollisionObject object) {
        Object obj = object.getUserObject();
        if (obj instanceof Spatial) {
            Spatial spatial = (Spatial) obj;
            if (entities.containsValue(spatial)) {
                return spatial;
            }
        }
        return null;
    }

    /**
     * finds the entity id of a given spatial if there is one
     * @param entity
     * @return
     */
    public long getEntityId(Spatial entity) {
        for (Iterator<Entry<Long, Spatial>> it = entities.entrySet().iterator(); it.hasNext();) {
            Entry<Long, Spatial> entry = it.next();
            if (entry.getValue() == entity) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * gets the entity belonging to a PhysicsCollisionObject
     * @param object
     * @return
     */
    public long getEntityId(PhysicsCollisionObject object) {
        Object obj = object.getUserObject();
        if (obj instanceof Spatial) {
            Spatial spatial = (Spatial) obj;
            if (spatial != null) {
                return getEntityId(spatial);
            }
        }
        return -1;
    }
    
    public PhysicsSpace getPhysicsSpace(){
    return space;
    }
}
