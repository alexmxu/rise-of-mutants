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

package com.bigboots.testscene;


import com.bigboots.BBApplication;
import com.bigboots.BBGlobals;
import com.bigboots.components.BBLightComponent;
import com.bigboots.components.camera.BBFirstPersonCamera;
import com.bigboots.core.BBSceneManager;
import com.bigboots.core.BBSettings;
import com.bigboots.input.BBInputManager;
import com.bigboots.physics.BBPhysicsManager;
import com.bigboots.scene.BBSceneComposer;
import com.bigboots.scene.BBShaderManager;
import com.jme3.asset.BlenderKey;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light.Type;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;




public class TestSceneComposer extends BBApplication {

    public static void main(String[] args) {
        TestSceneComposer app = new TestSceneComposer();
        app.run();
    }

    //Variables
    private MyTestAction actionListener;
    private BBFirstPersonCamera mFPSCamera;
    
    @Override
    public void simpleInitialize() {

        // Add a light Source
        BBLightComponent compLight = new BBLightComponent();
        compLight.setLightType(Type.Directional);
        compLight.getLight(DirectionalLight.class).setDirection(new Vector3f(0.5432741f, -0.58666015f, -0.6005691f).normalizeLocal());
        compLight.getLight(DirectionalLight.class).setColor(new ColorRGBA(1.1f,1.1f,1.1f,1));
        BBSceneManager.getInstance().getRootNode().addLight(compLight.getLight(DirectionalLight.class));
        
        //Set up the physic engine
        BBPhysicsManager.getInstance().init(engineSystem);
        
        //Load the main camera
        Camera cam = new Camera(BBSettings.getInstance().getSettings().getWidth(), BBSettings.getInstance().getSettings().getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 35f, 55f));
        cam.lookAt(new Vector3f(-5, 0, -5), Vector3f.UNIT_Y);
        
        mFPSCamera = new BBFirstPersonCamera("FSP_CAM", cam);
        mFPSCamera.setMoveSpeed(30);

        //Set up the main viewPort
        ViewPort vp = engineSystem.getRenderManager().createMainView("SCENE_VIEW", cam);
        vp.setClearFlags(true, true, true);
        vp.setBackgroundColor(ColorRGBA.Gray);
        BBSceneManager.getInstance().setViewPort(vp);
        
        // Set Skybox
        BBSceneManager.getInstance().createSky();
        //load keys
        setupKeys();
        
        // Load a blender file. 
        DesktopAssetManager dsk = (DesktopAssetManager) BBSceneManager.getInstance().getAssetManager();        
        BlenderKey bk = new BlenderKey("Scenes/levels/level_01/level_01.blend");
        Node nd =  (Node) dsk.loadModel(bk); 
        nd.setName("nd");

        String entities = "assets/Models";
        String baseTex = "assets/Textures/base_textures";
        String levelTex = "assets/Textures/level_textures";
        String scenePath = bk.getFolder().substring(0, bk.getFolder().length() - 1); //BlenderKey sets "File.separator" in the end of String

        BBSceneComposer sc = new BBSceneComposer(nd, entities, scenePath, baseTex, levelTex, BBSceneManager.getInstance().getAssetManager());

        // ShaderManager test
        BBShaderManager shm = new BBShaderManager(nd, BBSceneManager.getInstance().getAssetManager());
        shm.setSimpleIBLParam("Textures/skyboxes/sky_box_01/skybox_01_low.png");   
        shm.setFogParam(new ColorRGBA(0.67f,0.55f,0.2f, 70f), null);
        

    }

    
    @Override
    public void simpleUpdate(){
        if(actionListener.mQuit == true){
            engineSystem.stop(false);
        }
        
        //Put here your custom update code ...
        
    } 
    
    public void setupKeys(){
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
            "FLYCAM_RotateDrag",

            "FLYCAM_Rise",
            "FLYCAM_Lower"
        };

        // both mouse and button - rotation of cam
        BBInputManager.getInstance().mapKey("FLYCAM_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true),
                                               new KeyTrigger(KeyInput.KEY_LEFT));

        BBInputManager.getInstance().mapKey("FLYCAM_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false),
                                                new KeyTrigger(KeyInput.KEY_RIGHT));

        BBInputManager.getInstance().mapKey("FLYCAM_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                                             new KeyTrigger(KeyInput.KEY_UP));

        BBInputManager.getInstance().mapKey("FLYCAM_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                                               new KeyTrigger(KeyInput.KEY_DOWN));

        // mouse only - zoom in/out with wheel, and rotate drag
        BBInputManager.getInstance().mapKey("FLYCAM_ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        BBInputManager.getInstance().mapKey("FLYCAM_ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        BBInputManager.getInstance().mapKey("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // keyboard only WASD for movement and WZ for rise/lower height
        BBInputManager.getInstance().mapKey("FLYCAM_StrafeLeft", new KeyTrigger(KeyInput.KEY_A));
        BBInputManager.getInstance().mapKey("FLYCAM_StrafeRight", new KeyTrigger(KeyInput.KEY_D));
        BBInputManager.getInstance().mapKey("FLYCAM_Forward", new KeyTrigger(KeyInput.KEY_W));
        BBInputManager.getInstance().mapKey("FLYCAM_Backward", new KeyTrigger(KeyInput.KEY_S));
        BBInputManager.getInstance().mapKey("FLYCAM_Rise", new KeyTrigger(KeyInput.KEY_Q));
        BBInputManager.getInstance().mapKey("FLYCAM_Lower", new KeyTrigger(KeyInput.KEY_Z));
        
        BBInputManager.getInstance().mapKey(BBGlobals.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));        
        BBInputManager.getInstance().getInputManager().addListener(actionListener, BBGlobals.INPUT_MAPPING_EXIT);
        BBInputManager.getInstance().getInputManager().addListener(actionListener, mappings);
    }
    
    
    class MyTestAction implements AnalogListener, ActionListener{
    
        public boolean mQuit = false; 

        public void onAction(String binding, boolean keyPressed, float tpf) {
            if (binding.equals(BBGlobals.INPUT_MAPPING_EXIT) && !keyPressed) {
                mQuit = true; 
            }
        }

        public void onAnalog(String name, float value, float tpf) {
            if (!mFPSCamera.isEnabled())
                return;

            if (name.equals("FLYCAM_Left")){
                mFPSCamera.rotateCamera(value, mFPSCamera.getUpVector());
            }else if (name.equals("FLYCAM_Right")){
                mFPSCamera.rotateCamera(-value, mFPSCamera.getUpVector());
            }else if (name.equals("FLYCAM_Up")){
                mFPSCamera.rotateCamera(-value, mFPSCamera.getEngineCamera().getLeft());
            }else if (name.equals("FLYCAM_Down")){
                mFPSCamera.rotateCamera(value, mFPSCamera.getEngineCamera().getLeft());
            }else if (name.equals("FLYCAM_Forward")){
                mFPSCamera.moveCamera(value, false);
            }else if (name.equals("FLYCAM_Backward")){
                mFPSCamera.moveCamera(-value, false);
            }else if (name.equals("FLYCAM_StrafeLeft")){
                mFPSCamera.moveCamera(value, true);
            }else if (name.equals("FLYCAM_StrafeRight")){
                mFPSCamera.moveCamera(-value, true);
            }else if (name.equals("FLYCAM_Rise")){
                mFPSCamera.riseCamera(value);
            }else if (name.equals("FLYCAM_Lower")){
                mFPSCamera.riseCamera(-value);
            }else if (name.equals("FLYCAM_ZoomIn")){
                mFPSCamera.zoomCamera(value);
            }else if (name.equals("FLYCAM_ZoomOut")){
                mFPSCamera.zoomCamera(-value);
            }
        }
    }    
    
}
