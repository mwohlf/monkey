package net.wohlfart;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventServiceLocator;

import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;

/**
 * hosting application states
 */
class Game extends Application implements IStateContext {

    private static final String INPUT_MAPPING_EXIT = "INPUT_MAPPING_EXIT"; // event key

    private final EventService eventBus;

    Game() {
        super();
        eventBus = EventServiceLocator.getEventBusService();
    }

    protected AppSettings getFullscreenSetting() {
        final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final DisplayMode[] modes = device.getDisplayModes();
        final int i = 0; // note: there are usually several, let's pick the first
        final AppSettings settings = new AppSettings(true);
        settings.setResolution(modes[i].getWidth(), modes[i].getHeight());
        settings.setFrequency(modes[i].getRefreshRate());
        settings.setDepthBits(modes[i].getBitDepth());
        settings.setFullscreen(device.isFullScreenSupported());
        // slower but looks better
        settings.setVSync(true);
        // anti-aliasing can be a big performance problem...
        settings.setSamples(8);
        return settings;
    }

    protected AppSettings getWindowSetting() {
        final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();
        int index = 0; // note: there are usually several, let's pick the first
        AppSettings settings = new AppSettings(true);
        settings.setResolution(modes[index].getWidth() / 2, modes[index].getHeight() / 2);
        settings.setFrequency(modes[index].getRefreshRate());
        settings.setDepthBits(modes[index].getBitDepth());
        settings.setFullscreen(false);
        settings.setVSync(true);
        settings.setSamples(8);
        return settings;
    }

    @Override
    public void initialize() {
        super.initialize();

        // hook an exit event
        getInputManager().addMapping(INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
        getInputManager().addListener(new ActionListener() {
            @Override
            public void onAction(final String name, final boolean isPressed, final float tpf) {
                stop(false); // don't wait, true hangs on exit
            }
        }, INPUT_MAPPING_EXIT);

        getViewPort().setBackgroundColor(ColorRGBA.LightGray);
        getStateManager().attach(ApplicationStates.GAME.instance()); // initial state to render
    }


    // event loop, this is called by LwjglAbstractDisplay.runLoop()
    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks

        float timePerFrame = timer.getTimePerFrame() * speed;
        // update states; the stages updates their nodes unlike in the SimpleApp class
        // the states are responsible for the root node / gui node
        stateManager.update(timePerFrame);

        // render states
        stateManager.render(getRenderManager());
        // no idea what this is needed for
        renderManager.render(timePerFrame, context.isRenderable());
        // finish update
        stateManager.postRender();
    }

    // exit the game
    @Override
    public void stop(final boolean waitFor) {
        // TODO: do some cleanup on exit
        getViewPort().clearScenes();
        getViewPort().clearProcessors();
        getGuiViewPort().clearScenes();
        getGuiViewPort().clearProcessors();
        super.stop(waitFor);
    };



    public EventService getEventBus() {
        return eventBus;
    }

}
