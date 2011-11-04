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
package com.bigboots.core;

import com.jme3.app.AppTask;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;

import com.jme3.system.Timer;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
//


import java.util.concurrent.Future;
/*
import com.jme3.math.Vector3f;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
*/
import com.jme3.system.SystemListener;
import java.net.MalformedURLException;
import java.net.URL;
/**
 *
 * 
 */
public class BBEngineSystem {
    
    private static final Logger logger = Logger.getLogger(BBEngineSystem.class.getName());
    
    protected Renderer renderer;
    protected RenderManager renderManager;
    protected JmeContext context;
    protected Timer timer;
    protected boolean pauseOnFocus = true;
    protected float speed = 1f;
    protected boolean paused = false;
    protected float secondCounter = 0.0f;
   
    
    //protected Node rootNode = new Node("Root Node");
    protected AssetManager assetManager;
    
    private final ConcurrentLinkedQueue<AppTask<?>> taskQueue = new ConcurrentLinkedQueue<AppTask<?>>();
    
    
    /**
     * Create a new instance of <code>BBEngineSystem</code>.
     */
    public BBEngineSystem(){
        
    }
    
    private void initAssetManager(){
        if (BBSettings.getInstance().getSettings() != null){
            String assetCfg = BBSettings.getInstance().getSettings().getString("AssetConfigURL");
            if (assetCfg != null){
                URL url = null;
                try {
                    url = new URL(assetCfg);
                } catch (MalformedURLException ex) {
                }
                if (url == null) {
                    url = BBEngineSystem.class.getClassLoader().getResource(assetCfg);
                    if (url == null) {
                        logger.log(Level.SEVERE, "Unable to access AssetConfigURL in asset config:{0}", assetCfg);
                        return;
                    }
                }
                assetManager = JmeSystem.newAssetManager(url);
            }
        }
        if (assetManager == null){
            assetManager = JmeSystem.newAssetManager(
                    Thread.currentThread().getContextClassLoader()
                    .getResource("com/jme3/asset/Desktop.cfg"));
        }
    }
    
    public void create(){
                
        if (context != null && context.isCreated()){
            logger.warning("start() called when application already created!");
            return;
        }
        
        
        logger.log(Level.INFO, "Starting application: {0}", getClass().getName());
        context = JmeSystem.newContext(BBSettings.getInstance().getSettings(), JmeContext.Type.Display);
        
        context.create(false);
        
    }
   
    
    /**
     * Do not call manually.
     * Callback from ContextListener.
     *
     * Initializes the <code>BBEngineSystem</code>, by creating a display and
     * default camera. If display settings are not specified, a default
     * 640x480 display is created. Default values are used for the camera;
     * perspective projection with 45Â° field of view, with near
     * and far values 1 and 1000 units respectively.
     */
    public void initialize(){
        if (assetManager == null){
            initAssetManager();
        }
        
        timer = context.getTimer();
       
        renderer = context.getRenderer();
              
        renderManager = new RenderManager(renderer);
        //Remy - 09/14/2010 setted the timer in the renderManager
        renderManager.setTimer(timer);
 
        // update timer so that the next delta is not too large
        timer.reset(); 

    }
      
    /**
     * Do not call manually.
     * Callback from ContextListener.
     */
    public void update(){
        //execute AppTasks
        AppTask<?> task = taskQueue.poll();
        toploop: do {
            if (task == null) break;
            while (task.isCancelled()) {
                task = taskQueue.poll();
                if (task == null) break toploop;
            }
            task.invoke();
        } while (((task = taskQueue.poll()) != null));
        
        if (speed == 0 || paused)
            return;
        
        if(timer != null){
            timer.update();
        
                
        float tpf = timer.getTimePerFrame() * speed;

        secondCounter += timer.getTimePerFrame();
        int fps = (int) timer.getFrameRate();
        if (secondCounter >= 1.0f) {
            secondCounter = 0.0f;
        }
        
        // update states
        //stateManager.update(tpf);
        
        // render states
        //stateManager.render(renderManager);     
        renderManager.render(tpf, context.isRenderable());
        
        //stateManager.postRender();
        }
    }
    
        
    public void setContextListener(SystemListener lstr){
        context.setSystemListener(lstr);
    }
        
    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<V>(callable);
        taskQueue.add(task);
        return task;
    }
    
     public void reshape(int w, int h){
        renderManager.notifyReshape(w, h);
    }
    
    public void requestClose(boolean esc){
        context.destroy(false);
    }
    
    public void gainFocus(){
        if (pauseOnFocus){
            paused = false;
            context.setAutoFlushFrames(true);
        }
    }
    
    public void loseFocus(){
        if (pauseOnFocus){
            paused = true;
            context.setAutoFlushFrames(false);
        }
    }
    
    public void handleError(String errMsg, Throwable t){
        logger.log(Level.SEVERE, errMsg, t);
    }

    /**
     * Requests the context to close, shutting down the main loop
     * and making necessary cleanup operations. 
     * After the application has stopped, it cannot be used anymore.
     */
    public void stop(boolean waitFor){
        logger.log(Level.FINE, "Closing application: {0}", getClass().getName());
        context.destroy(waitFor);
    }
    
    /**
     * Do not call manually.
     * Callback from ContextListener.
     */
    public void destroy(){       
        timer.reset();
    }
    
    public boolean isPauseOnLostFocus() {
        return pauseOnFocus;
    }

    public void setPauseOnLostFocus(boolean pauseOnLostFocus) {
        this.pauseOnFocus = pauseOnLostFocus;
    }
    
        
    /**
     * @return The display context for the application, or null if was not
     * started yet.
     */
    public JmeContext getContext(){
        return context;
    }
    /**
     * @return the render manager
     */
    public RenderManager getRenderManager() {
        return renderManager;
    }

    /**
     * @return The renderer for the application, or null if was not started yet.
     */
    public Renderer getRenderer(){
        return renderer;
    }
    
    public Timer getTimer(){
        return timer;
    }
    
    
    /**
     * @return The {@link AssetManager asset manager} for this application.
     */
    public AssetManager getAssetManager(){
        return assetManager;
    }
}
