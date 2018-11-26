package main;

import mote4.scenegraph.Layer;
import mote4.scenegraph.Scene;
import mote4.scenegraph.Window;
import mote4.scenegraph.target.FBO;
import mote4.util.audio.AudioPlayback;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.FontUtils;
import scenes.*;

import java.util.ArrayList;
import java.util.List;

import static main.Input.Key.ALT;
import static main.Input.Key.ENTER;

public class RootLayer extends Layer {

    public enum State {
        TITLE, INGAME, TERMINAL;
    }
    private State state;
    private boolean updateState = false;
    private int width, height;

    private List<Scene> title, ingame, terminal;
    private static RootLayer instance;
    public static RootLayer getInstance() {
        if (instance == null)
            instance = new RootLayer();
        return instance;
    }

    private RootLayer() {
        super(null);
        title = new ArrayList<>();
        ingame = new ArrayList<>();
        terminal = new ArrayList<>();
        resetGame();
        loadTerminal();
    }

    private boolean altEnterPressed = false;
    @Override
    public void update(double time, double delta) {
        Input.pollGamepad();

        // check for alt+enter to toggle fullscreen
        if (Input.isKeyDown(ALT) && Input.isKeyDown(ENTER)) {
            if (!altEnterPressed) {
                if (Window.isFullscreen())
                    Window.setWindowedPercent(.85,16/9d);
                else
                    Window.setFullscreen();
            }
            altEnterPressed = true;
        } else
            altEnterPressed = false;

        if (updateState) {
            updateState = false;
            switch (state) {
                case TITLE:
                    FontUtils.useMetric("font_1");
                    //FontUtils.setCharPixelWidth(12);
                    scenes = title;
                    break;
                case INGAME:
                    FontUtils.useMetric("font_1");
                    //FontUtils.setCharPixelWidth(12);
                    scenes = ingame;
                    break;
                case TERMINAL:
                    FontUtils.useMetric("font_term");
                    //FontUtils.setCharPixelWidth(9);
                    scenes = terminal;
                    break;
            }
        }
        super.update(time, delta);
    }

    @Override
    public void framebufferResized(int width, int height) {
        this.width = width;//s[0];
        this.height = height;//s[1];

        int[] s = Post.getInstance().updateInternalResolution(width,height);
        for (Scene sc : title)
            sc.framebufferResized(s[0],s[1]);
        for (Scene sc : ingame)
            sc.framebufferResized(s[0],s[1]);
        for (Scene sc : terminal)
            sc.framebufferResized(s[0],s[1]);

        if (target != null) {
            target.destroy();
            TextureMap.delete("fbo_scene");
        }
        if (target != null)
            target.destroy();
        int internalScale = 1;
        FBO scene = new FBO(s[0]*internalScale,s[1]*internalScale, true, false, null);
        scene.addToTextureMap("fbo_scene");
        target = scene;
    }

    public void setState(State s) {
        state = s;
        updateState = true;
    }
    public void loadTerminal() {
        setState(State.TERMINAL);
    }
    public void loadTitleScreen() {
        setState(State.TITLE);
    }
    public void loadGame() {
        Level.resetScore();
        GameUI.reset();
        AudioPlayback.playMusic("tunnel",true);
        setState(State.INGAME);
    }

    public void resetGame() {
        AudioPlayback.stopMusic();
        for (Scene sc : title)
            sc.destroy();
        for (Scene sc : ingame)
            sc.destroy();
        for (Scene sc : terminal)
            sc.destroy();
        title.clear();
        ingame.clear();
        terminal.clear();

        title.add(new TitleScreen());
        ingame.add(new Ingame());
        ingame.add(new GameUI());
        terminal.add(new Terminal());

        if (width != 0)
            framebufferResized(width,height);

        setState(State.TITLE);
    }
}
