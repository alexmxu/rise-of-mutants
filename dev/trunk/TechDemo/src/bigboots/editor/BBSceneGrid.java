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
package bigboots.editor;

import com.bigboots.BBApplication;
import com.bigboots.BBGlobals;
import com.bigboots.components.BBComponent.CompType;
import com.bigboots.components.BBEntity;
import com.bigboots.components.BBLightComponent;
import com.bigboots.components.BBNodeComponent;
import com.bigboots.components.BBObject.ObjectTag;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.input.BBInputManager;
import com.bigboots.core.BBDebugInfo;
import com.bigboots.components.camera.BBFreeCamera;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light.Type;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import java.io.File;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
public class BBSceneGrid extends BBApplication{
    //Variables
    private MyTestAction actionListener;
    private BBFreeCamera mFreeCamera;
    private Camera cam;
    private Node gridNode, sceneNode;
    private BBSceneGizmo mSceneGizmo;
    
    private int mEntityID = 0;
    
    public BBSceneGrid() {
        super();
    }
    
    @Override
    public void simpleInitialize(){
        //Load the main camera
        cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 0f, 1f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        
        //Set up the main viewPort
        ViewPort vp = engineSystem.getRenderManager().createMainView("CUSTOM_VIEW", cam);
        vp.setClearFlags(true, true, true);
        vp.setBackgroundColor(ColorRGBA.Gray);
        BBSceneManager.getInstance().setViewPort(vp);
        
        mFreeCamera = new BBFreeCamera("FREE_CAM", cam);
        mFreeCamera.setMoveSpeed(30);
        //mFreeCamera.setDragToRotate(false);
        
        //Set up basic light and sky coming with the standard scene manager
        BBSceneManager.getInstance().setupBasicLight();
        BBSceneManager.getInstance().createSky();
        
        //Set up keys and listener to read it
        actionListener = new MyTestAction();
        
        //load keys
        setupKeys();
        
        // Add a light Source
        BBLightComponent compLight = new BBLightComponent();
        compLight.setLightType(Type.Directional);
        compLight.getLight(DirectionalLight.class).setDirection(new Vector3f(0.5432741f, -0.58666015f, -0.6005691f).normalizeLocal());
        compLight.getLight(DirectionalLight.class).setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        BBSceneManager.getInstance().getRootNode().addLight(compLight.getLight(DirectionalLight.class));
        
        //create grid
        createGrid();
        mSceneGizmo = new BBSceneGizmo();
        mSceneGizmo.init();
        mSceneGizmo.setTranAxisVisible(true);
        
        //Attach node to rootNode
        BBSceneManager.getInstance().addChild(gridNode);
        BBSceneManager.getInstance().addChild(sceneNode);
        
        //Relocate the camera
        cam.setLocation(new Vector3f(13f,9f,15f));
        cam.lookAt(new Vector3f(-3f, -3f, -3f), Vector3f.UNIT_Y);
        
        //Set debub info on
        BBDebugInfo.getInstance().setDisplayFps(true);
        BBDebugInfo.getInstance().setDisplayStatView(true);
        
    }
    
    @Override
    public void simpleUpdate(){
        
    }
    
    public void loadExternalModel(String name, String path){       
        // convert to / for windows
        if (File.separatorChar == '\\'){
            path = path.replace('\\', '/');
        }
        if(!path.endsWith("/")){
            path += "/";
        }
        
        BBSceneManager.getInstance().addFileLocator(path);

        Spatial tmpSpatial =  BBSceneManager.getInstance().getAssetManager().loadModel(name);
        
        BBEntity entity;
        
        if(tmpSpatial instanceof Geometry){
            Node tmpNode = new Node(tmpSpatial.getName());
            tmpNode.attachChild(tmpSpatial);
            entity = new BBEntity("ENTITY"+mEntityID, tmpNode);
        }else{
            entity = new BBEntity("ENTITY"+mEntityID, (Node)tmpSpatial);
        }
        
        entity.setObjectTag(ObjectTag.PLAYER);
        BBNodeComponent pnode = entity.addComponent(CompType.NODE);
        pnode.setLocalTranslation(mSceneGizmo.getMarkPosition());
        entity.attachToRoot();
        entity.loadModel("");
        entity.attachToNode(sceneNode);
        
        mSceneGizmo.getTranAxis().setLocalTranslation(mSceneGizmo.getMarkPosition());
        
        BBSceneManager.getInstance().removeFileLocator(path);
        
        mEntityID++;
    }
    
    private void createGrid(){
        gridNode = new Node("gridNode");
    	sceneNode = new Node("sceneNode");
        
        //Create a grid plane
        Geometry g = new Geometry("GRID", new Grid(40, 40, 0.5f) );
        Material floor_mat = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        floor_mat.getAdditionalRenderState().setWireframe(true);
        floor_mat.setColor("Color", new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        floor_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        g.setShadowMode(ShadowMode.Off);
        g.setQueueBucket(Bucket.Transparent);
        g.setMaterial(floor_mat);
        g.center().move(new Vector3f(0f,0f,0f));
        gridNode.attachChild(g);

        // Red line for X axis
        final Line xAxis = new Line(new Vector3f(-10f, 0f, 0f), new Vector3f(10f, 0f, 0f));
        xAxis.setLineWidth(2f);
        Geometry gxAxis = new Geometry("XAxis", xAxis);
        gxAxis.setModelBound(new BoundingBox());
        Material mat = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1.0f, 0.2f, 0.2f, 0.5f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        g.setQueueBucket(Bucket.Transparent);
        gxAxis.setShadowMode(ShadowMode.Off);
        gxAxis.setMaterial(mat);
        gridNode.attachChild(gxAxis);

        // Bleu line for Z axis
        final Line zAxis = new Line(new Vector3f(0f, 0f, -10f), new Vector3f(0f, 0f, 10f));
        zAxis.setLineWidth(2f);
        Geometry gzAxis = new Geometry("ZAxis", zAxis);
        gzAxis.setModelBound(new BoundingBox());
        mat = new Material(BBSceneManager.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.2f, 0.2f, 1.0f, 0.5f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        g.setQueueBucket(Bucket.Transparent);        
        gzAxis.setShadowMode(ShadowMode.Off);
        gzAxis.setMaterial(mat);
        gridNode.attachChild(gzAxis);

    }
    
    
    
    private void setupKeys(){
                //Set up keys and listener to read it
        actionListener = new MyTestAction();
        String[] mappings = new String[]{
            "FLYCAM_Left",
            "FLYCAM_Right",
            "FLYCAM_Up",
            "FLYCAM_Down",

            "FLYCAM_StrafeLeft",
            "FLYCAM_StrafeRight",
            "FLYCAM_Forward",
            "FLYCAM_Backward",

            "FLYCAM_ZoomIn",
            "FLYCAM_ZoomOut",
            "MOUSE_CLICK_LEFT",

            "FLYCAM_Rise",
            "FLYCAM_Lower",
            "MOUSE_MOVE_LEFT",
            "MOUSE_MOVE_RIGHT"
        };
        //mouse click
        BBInputManager.getInstance().mapKey("MOUSE_MOVE_LEFT", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        BBInputManager.getInstance().mapKey("MOUSE_MOVE_RIGHT", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        
        // button - rotation of cam
        BBInputManager.getInstance().mapKey("FLYCAM_Left", new KeyTrigger(KeyInput.KEY_LEFT));
        BBInputManager.getInstance().mapKey("FLYCAM_Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        BBInputManager.getInstance().mapKey("FLYCAM_Up", new KeyTrigger(KeyInput.KEY_UP));
        BBInputManager.getInstance().mapKey("FLYCAM_Down", new KeyTrigger(KeyInput.KEY_DOWN));

        // mouse only - zoom in/out with wheel, and rotate drag
        BBInputManager.getInstance().mapKey("FLYCAM_ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        BBInputManager.getInstance().mapKey("FLYCAM_ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        BBInputManager.getInstance().mapKey("MOUSE_CLICK_LEFT", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // keyboard only WASD for movement and WZ for rise/lower height
        BBInputManager.getInstance().mapKey("FLYCAM_StrafeLeft", new KeyTrigger(KeyInput.KEY_A));
        BBInputManager.getInstance().mapKey("FLYCAM_StrafeRight", new KeyTrigger(KeyInput.KEY_D));
        BBInputManager.getInstance().mapKey("FLYCAM_Forward", new KeyTrigger(KeyInput.KEY_W));
        BBInputManager.getInstance().mapKey("FLYCAM_Backward", new KeyTrigger(KeyInput.KEY_S));
        BBInputManager.getInstance().mapKey("FLYCAM_Rise", new KeyTrigger(KeyInput.KEY_Q));
        BBInputManager.getInstance().mapKey("FLYCAM_Lower", new KeyTrigger(KeyInput.KEY_Z));
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_CAMERA_POS, new KeyTrigger(KeyInput.KEY_F1));
        
        BBInputManager.getInstance().getInputManager().addListener(actionListener, mappings);
        BBInputManager.getInstance().getInputManager().addListener(actionListener, BBGlobals.INPUT_MAPPING_CAMERA_POS);
        
        BBInputManager.getInstance().getInputManager().setCursorVisible(false);
    }
    
    class MyTestAction implements AnalogListener, ActionListener{
     

        public void onAction(String binding, boolean keyPressed, float tpf) {
           /* if (binding.equals("MOUSE_CLICK_LEFT") && mFreeCamera.isDragToRotate() && !mFreeCamera.isEnabled()){
                mFreeCamera.setDragToRotate(keyPressed);
                BBInputManager.getInstance().getInputManager().setCursorVisible(!keyPressed);
            }*/
            
            if (binding.equals(BBGlobals.INPUT_MAPPING_CAMERA_POS) && !keyPressed) {
                if (cam != null) {
                    Vector3f loc = cam.getLocation();
                    Quaternion rot = cam.getRotation();
                    System.out.println("Camera Position: ("
                            + loc.x + ", " + loc.y + ", " + loc.z + ")");
                    System.out.println("Camera Rotation: " + rot);
                    System.out.println("Camera Direction: " + cam.getDirection());
                }
            }
            
            if(binding.equals("MOUSE_CLICK_LEFT")){
                //update the mark location
               mSceneGizmo.updateMarkGizmo();
            }
        }

        public void onAnalog(String name, float value, float tpf) {

            if (name.equals("FLYCAM_Left")){
                mFreeCamera.rotateCamera(value, mFreeCamera.getUpVector());
            }else if (name.equals("FLYCAM_Right")){
                mFreeCamera.rotateCamera(-value, mFreeCamera.getUpVector());
            }else if (name.equals("FLYCAM_Up")){
                mFreeCamera.rotateCamera(-value, mFreeCamera.getEngineCamera().getLeft());
            }else if (name.equals("FLYCAM_Down")){
                mFreeCamera.rotateCamera(value, mFreeCamera.getEngineCamera().getLeft());
            }else if (name.equals("FLYCAM_Forward")){
                mFreeCamera.moveCamera(value, false);
            }else if (name.equals("FLYCAM_Backward")){
                mFreeCamera.moveCamera(-value, false);
            }else if (name.equals("FLYCAM_StrafeLeft")){
                mFreeCamera.moveCamera(value, true);
            }else if (name.equals("FLYCAM_StrafeRight")){
                mFreeCamera.moveCamera(-value, true);
            }else if (name.equals("FLYCAM_Rise")){
                mFreeCamera.riseCamera(value);
            }else if (name.equals("FLYCAM_Lower")){
                mFreeCamera.riseCamera(-value);
            }else if (name.equals("FLYCAM_ZoomIn")){
                mFreeCamera.zoomCamera(value);
            }else if (name.equals("FLYCAM_ZoomOut")){
                mFreeCamera.zoomCamera(-value);
            }
            
            if (name.equals("MOUSE_MOVE_LEFT") || name.equals("MOUSE_MOVE_RIGHT")) {
                // Reset results list.
                CollisionResults results = new CollisionResults();
                // Convert screen click to 3d position
                Vector2f click2d = BBInputManager.getInstance().getInputManager().getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
                // Aim the ray from the clicked spot forwards.
                Ray ray = new Ray(click3d, dir);
                // Collect intersections between ray and all nodes in results list.
                gridNode.collideWith(ray, results);
                // (Print the results so we see what is going on:)
                for (int i = 0; i < results.size(); i++) {
                  // (For each “hit”, we know distance, impact point, geometry.)
                  float dist = results.getCollision(i).getDistance();
                  Vector3f pt = results.getCollision(i).getContactPoint();
                  String target = results.getCollision(i).getGeometry().getName();
                  //System.out.println("Selection #" + i + ": " + target + " at " + pt + ", " + dist + " WU away.");
                }
                // Use the results -- we rotate the selected geometry.
                if (results.size() > 0) {
                  // The closest result is the target that the player picked:
                  Geometry target = results.getClosestCollision().getGeometry();
                  //System.out.println("ooo GEOM FOUND : "+target.getName());
                  // The closest collision point is what was truly hit:
                  CollisionResult closest = results.getClosestCollision();
                  // Here comes the action:
                  if (target.getName().equals("GRID")) {
                    // Let's interact - we mark the hit with a red dot.
                    mSceneGizmo.setMarkPosition(closest.getContactPoint());
                    //System.out.println("xxxxxx FOUND : "+markPosition.toString());
                  }
                }
            } // else if ...

        }
    }//end myClass
    
    
    
}
