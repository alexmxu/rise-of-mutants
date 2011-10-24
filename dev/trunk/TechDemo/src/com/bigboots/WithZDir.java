package com.bigboots;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.jme3.math.Vector3f;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
//scene
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
//Lights
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;

//animation class import
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
//physic
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
//import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
//import com.jme3.bullet.util.CollisionShapeFactory;

//Input
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;

//terrain
import com.jme3.material.Material;
//import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
//import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
//import com.jme3.terrain.heightmap.HillHeightMap; // for exercise 2
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import jme3tools.converters.ImageToAwt;

//Camera
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
//Sky
import com.jme3.util.SkyFactory;
//filters
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
//fog
import com.jme3.post.filters.FogFilter;
//bleur
import com.jme3.post.filters.DepthOfFieldFilter;
//audio


/**
 * test
 * @author normenhansen
 */
public class WithZDir extends SimpleApplication implements AnimEventListener, ActionListener, AnalogListener, PhysicsCollisionListener{ 

    public static void main(String[] args) {
        WithZDir app = new WithZDir();
        app.start();
    }

    protected Node human;
    protected CharacterControl pControler;
    protected Spatial player;
    protected CameraNode camNode;
    protected AnimChannel channel;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false,lastLeft = true;
    private static final Logger logger = Logger.getLogger(SimpleApplication.class.getName());
    
    private static final class Directions{
    private static final Quaternion rot = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
    private static final Quaternion upDir = Quaternion.DIRECTION_Z;
    private static final Quaternion rightDir = upDir.mult(rot);
    private static final Quaternion downDir = rightDir.mult(rot);
    private static final Quaternion leftDir = downDir.mult(rot);
    }
    
    @Override
    public void simpleInitApp() {
        
    //Set up keys
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_L));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_I));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_K));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
        
        
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    rootNode.addLight(al);
 
    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
    rootNode.addLight(dl);
    
    human = new Node("human");

    player = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
    Spatial sword = assetManager.loadModel("Models/Sinbad/Sword/Sword.mesh.j3o");
    human.attachChild(player);
    human.attachChild(sword);
    human.setLocalTranslation(new Vector3f(-338.13904f, -91.89435f, 13.445859f));
    player.rotate(0, FastMath.HALF_PI, 0);
    
    //Load terrain
            
   /** 1. Create terrain material and load four textures into it. */
    Material mat_terrain = new Material(assetManager, 
            "Common/MatDefs/Terrain/Terrain.j3md");
 
    /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("Alpha", assetManager.loadTexture(
            "Textures/Terrain/splat/alphamap.png"));
 
    /** 1.2) Add GRASS texture into the red layer (Tex1). */
    Texture grass = assetManager.loadTexture(
            "Textures/Terrain/splat/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex1", grass);
    mat_terrain.setFloat("Tex1Scale", 64f);
 
    /** 1.3) Add DIRT texture into the green layer (Tex2) */
    Texture dirt = assetManager.loadTexture(
            "Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex2", dirt);
    mat_terrain.setFloat("Tex2Scale", 32f);
 
    /** 1.4) Add ROAD texture into the blue layer (Tex3) */
    Texture rock = assetManager.loadTexture(
            "Textures/Terrain/splat/road.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex3", rock);
    mat_terrain.setFloat("Tex3Scale", 128f);
 
    /** 2. Create the height map */
    AbstractHeightMap heightmap = null;
    Texture heightMapImage = assetManager.loadTexture(
            "Textures/Terrain/splat/mountains512.png");
    heightmap = new ImageBasedHeightMap(
      ImageToAwt.convert(heightMapImage.getImage(), false, true, 0));
    heightmap.load();
 
    /** 3. We have prepared material and heightmap. 
     * Now we create the actual terrain:
     * 3.1) Create a TerrainQuad and name it "my terrain".
     * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
     * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
     * 3.4) As LOD step scale we supply Vector3f(1,1,1).
     * 3.5) We supply the prepared heightmap itself.
     */
    int patchSize = 65;
    TerrainQuad terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());
 
    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, -100, 0);
    terrain.setLocalScale(2f, 1f, 2f);
        
        
    Node world = new Node("world");
    world.attachChild(terrain); 

    //Load sky
    Spatial sky = SkyFactory.createSky(assetManager, "Textures/sky/skysphere.jpg", true);
    rootNode.attachChild(sky);
    
    //Set up animation
    AnimControl control = player.getControl(AnimControl.class);
    control.addListener(this);
    channel = control.createChannel();
    channel.setAnim("IdleTop");
    channel.setLoopMode(LoopMode.Loop);
    channel.setSpeed(1f);

    //Attach to scene root node
    rootNode.attachChild(human);
    rootNode.attachChild(world);       


    // Set up Physics
    BulletAppState bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);

    // We set up collision detection for the scene by creating a
    // compound collision shape and a static RigidBodyControl with mass zero.
    //TerrainQuad tQ = (TerrainQuad) world.getChild(1);
    terrain.addControl(new RigidBodyControl(0));


    //CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(terrain);
    //RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
    //terrain.addControl(landscape);

    // We also put the player in its starting position.
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    pControler = new CharacterControl(capsuleShape, 0.05f);
    pControler.setJumpSpeed(20);
    pControler.setFallSpeed(30);
    pControler.setGravity(30);
    pControler.setPhysicsLocation(player.getWorldTranslation());//new Vector3f(0, 10, 0));
    pControler.setUseViewDirection(true);
    

    bulletAppState.getPhysicsSpace().addAll(terrain);
    bulletAppState.getPhysicsSpace().add(pControler); 
    
    // Add phys controller to player.
    player.addControl(pControler);
    //Position the camera
    //cam.setLocation(new Vector3f(-341.88324f, -68.31373f, -49.842667f));
    //cam.lookAt(player.getWorldTranslation(), Vector3f.UNIT_Y);
    //flyCam.setMoveSpeed(10f);

    // Disable the default flyby cam
    flyCam.setEnabled(false);
    //create the camera Node
    camNode = new CameraNode("Camera Node", cam);
    //This mode means that camera copies the movements of the target:
    camNode.setControlDir(ControlDirection.SpatialToCamera);
    //Move camNode, e.g. behind and above the target:
    camNode.setLocalTranslation(new Vector3f(0, 10, -40));
    //Rotate the camNode to look at the target:
    camNode.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y);
    //Attach the camNode to the target:
    human.attachChild(camNode);    

    
    //bloom
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    BloomFilter bf = new BloomFilter(BloomFilter.GlowMode.Objects);
    bf.setBloomIntensity(2.0f);
    bf.setExposurePower(1.3f);
    fpp.addFilter(bf);
    

    //Fog
    //FilterPostProcessor fogp =new FilterPostProcessor(assetManager);
    //fpp.setNumSamples(4);
    FogFilter fog=new FogFilter();
    fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
    fog.setFogDistance(510);
    fog.setFogDensity(1.2f);
    fpp.addFilter(fog);
    

    //bleur
    //FilterPostProcessor dofp = new FilterPostProcessor(assetManager);
    DepthOfFieldFilter dofFilter = new DepthOfFieldFilter();
    dofFilter.setFocusDistance(0);
    dofFilter.setFocusRange(150);
    dofFilter.setBlurScale(1.4f);
    fpp.addFilter(dofFilter);
    
    viewPort.addProcessor(fpp);
    //viewPort.addProcessor(dofp);
    //viewPort.addProcessor(fogp);
    
    }

  // Abstract funtion coming with animation
  public void onAnimCycleDone(AnimControl control, AnimChannel chan, String animName) {
    if (animName.equals("RunTop")) {
      chan.setAnim("IdleTop", 0.50f);
      chan.setLoopMode(LoopMode.DontLoop);
      chan.setSpeed(1f);
    }
  }
 // Abstract funtion coming with animation
  public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    // unused
  }
    
  /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  private int pressed=0;
  public void onAction(String binding, boolean value, float tpf) {
      if(value == true){
          pressed++;
      }
      else{
          pressed--;                      
      }
      if(pressed==1&&value){
          pControler.setEnabled(true);
          channel.setAnim("RunTop", 0.50f);
          channel.setAnim("RunBase", 0.50f);
          channel.setLoopMode(LoopMode.Loop);          
      }
      else if (pressed==0) {
          pControler.setEnabled(false);
          channel.setAnim("IdleTop", 0.50f);
          channel.setLoopMode(LoopMode.DontLoop);          
      }
    if (binding.equals("Jump")) {
        channel.setAnim("JumpStart", 0.50f);  
        pControler.jump();
          channel.setAnim("JumpLoop", 0.50f);
          channel.setLoopMode(LoopMode.Loop);
      if (!value) { 
          channel.setAnim("IdleTop", 0.50f);
      }
        
    }

  }

  
    
    @Override
    public void simpleUpdate(float tpf) {
    human.setLocalTranslation(pControler.getPhysicsLocation());    
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void collision(PhysicsCollisionEvent pce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onAnalog(String binding, float value, float tpf) {
    
        if (binding.equals("Left")) {
        Quaternion newRot = new Quaternion().slerp(player.getLocalRotation(),Directions.leftDir, tpf*3);
        player.setLocalRotation(newRot);
        }
        else if (binding.equals("Right")) {
        Quaternion newRot = new Quaternion().slerp(player.getLocalRotation(),Directions.rightDir, tpf*3);
        player.setLocalRotation(newRot);        
        } else if (binding.equals("Up")) {
        Quaternion newRot = new Quaternion().slerp(player.getLocalRotation(),Directions.upDir, tpf*3);
        player.setLocalRotation(newRot);
        } else if (binding.equals("Down")) {
        Quaternion newRot = new Quaternion().slerp(player.getLocalRotation(),Directions.downDir, tpf*3);
        player.setLocalRotation(newRot);
        }
        
        pControler.setViewDirection(player.getWorldRotation().mult(Vector3f.UNIT_Z));
        pControler.setWalkDirection(pControler.getViewDirection().multLocal(tpf*10));
                     
    }
}
