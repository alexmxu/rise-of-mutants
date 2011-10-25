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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;
import com.jme3.system.SystemListener;
import com.jme3.system.Timer;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
//
import com.jme3.renderer.ViewPort;
import com.jme3.system.AppSettings;
import com.jme3.renderer.Camera;
import com.jme3.math.Vector3f;
import java.util.concurrent.Future;

/**
 *
 * 
 */
public class BBEngineSystem implements SystemListener {
    
    private static final Logger logger = Logger.getLogger(BBEngineSystem.class.getName());
    
    protected Renderer renderer;
    protected RenderManager renderManager;
    protected JmeContext context;
    protected Timer timer;
    protected boolean pauseOnFocus = true;
    protected float speed = 1f;
    protected boolean paused = false;
    protected float secondCounter = 0.0f;
    
    protected boolean showSettings = true;
    protected ViewPort viewPort;
    protected AppSettings settings;
    protected Camera cam;
    
    private final ConcurrentLinkedQueue<AppTask<?>> taskQueue = new ConcurrentLinkedQueue<AppTask<?>>();
    
    
    /**
     * Create a new instance of <code>BBEngineSystem</code>.
     */
    public BBEngineSystem(){
        
    }
    
    
    public void create(){
        if (context != null && context.isCreated()){
            logger.warning("start() called when application already created!");
            return;
        }
        boolean loadSettings = false;
        if (settings == null){
            settings = new AppSettings(true);
            loadSettings = true;
        }
        
        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }
        
           
        logger.log(Level.FINE, "Starting application: {0}", getClass().getName());
        context = JmeSystem.newContext(settings, JmeContext.Type.Display);
        context.setSystemListener(this);
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
        // aquire important objects from the context
        //settings = context.getSettings();
        timer = context.getTimer();
       
        renderer = context.getRenderer();
        renderManager = new RenderManager(renderer);
        //Remy - 09/14/2010 setted the timer in the renderManager
        renderManager.setTimer(timer);
        
        //init camera
        cam = new Camera(settings.getWidth(), settings.getHeight());
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        viewPort = renderManager.createMainView("Default", cam);
        
        //viewPort.setClearEnabled(true);
        // TODO : Control this
        viewPort.setEnabled(true);

        
        // update timer so that the next delta is not too large
        timer.reset();
    }
    
    public void reshape(int w, int h){
        renderManager.notifyReshape(w, h);
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
        
        timer.update();
        
        float tpf = timer.getTimePerFrame() * speed;

        secondCounter += timer.getTimePerFrame();
        int fps = (int) timer.getFrameRate();
        if (secondCounter >= 1.0f) {
            secondCounter = 0.0f;
        }

        // update states
        //stateManager.update(tpf);
        // update and root node
        //rootNode.updateLogicalState(tpf);
        //rootNode.updateGeometricState();
        
        // render states
        //stateManager.render(renderManager);
        //renderManager.render(tpf);
        // TODO : Control this        
        renderManager.render(tpf,true);
        
        //stateManager.postRender();
    }
    
    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<V>(callable);
        taskQueue.add(task);
        return task;
    }
    
    public void requestClose(boolean esc){
        context.destroy(false);
    }
    
    public void gainFocus(){
        if (pauseOnFocus){
            paused = false;
            context.setAutoFlushFrames(true);
            //if (inputManager != null)
            //    inputManager.reset();
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
     * Do not call manually.
     * Callback from ContextListener.
     */
    public void destroy(){
        //stateManager.cleanup();
        
        //destroyInput();
        //if (audioRenderer != null)
        //    audioRenderer.cleanup();
        
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
     
}
