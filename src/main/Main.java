package main;

import mote4.scenegraph.Window;
import mote4.util.ErrorUtils;
import mote4.util.FileIO;
import mote4.util.audio.ALContext;
import mote4.util.audio.AudioLoader;
import mote4.util.audio.AudioPlayback;
import mote4.util.shader.ShaderUtils;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.FontUtils;
import mote4.util.vertex.builder.MeshBuilder;
import mote4.util.vertex.builder.StaticMeshBuilder;
import mote4.util.vertex.mesh.MeshMap;
import scenes.Post;

import static main.Util.constructBarrel;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Peter on 1/11/17.
 */
public class Main {
    public static void main(String[] args) {

        // TODO credit the music:
        // youtu.be/C4_uIwsW9Oc
        // youtu.be/Qx-ogfjejwA

        ErrorUtils.debug(true);
        Window.displayDeltaInTitle(true);
        Window.initWindowedPercent(.85, 16/9d);
        Window.setVsync(false);

        Input.createGamepadCallback();
        Input.createCharCallback();
        Input.createKeyCallback();

        ALContext.initContext();
        AudioPlayback.enableMusic(true);

        loadResources();

        for (int i = GLFW_JOYSTICK_1; i <= GLFW_JOYSTICK_LAST; i++)
            if (glfwJoystickPresent(i))
                System.out.println("Gamepad "+i+" present, name: " + glfwGetJoystickName(i));

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glEnable(GL_CULL_FACE);
        glPointSize(2);

        //glfwSetInputMode(Window.getWindowID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetWindowSizeLimits(Window.getWindowID(), 320, 240, GLFW_DONT_CARE, GLFW_DONT_CARE);
        //glfwSetWindowAspectRatio(Window.getWindowID(), 16, 9);

        RootLayer l = RootLayer.getInstance();
        //l.loadTitleScreen();
        Window.addLayer(l);
        Window.addScene(Post.getInstance());
        Window.loop(60);
    }
    private static void loadResources() {

        for (String pn : Main.class.getModule().getPackages())
            Main.class.getModule().addOpens(pn, FileIO.class.getModule());
        FileIO.setResourceModule(Main.class.getModule());

        ShaderUtils.loadIndex("index.txt");
        TextureMap.loadIndex("index.txt");
        AudioLoader.loadIndex("index.txt");

        FontUtils.loadMetric("font/terminal_metric","font_term");
        FontUtils.loadMetric("font/8bit_metric","font_1");
        FontUtils.setCharPixelWidth(12);

        MeshMap.add(StaticMeshBuilder.constructVAOFromOBJ("arwing",false), "arwing");
        MeshMap.add(StaticMeshBuilder.constructVAOFromOBJ("cube",false), "cube");
        MeshMap.add(StaticMeshBuilder.constructVAOFromOBJ("enemy",false), "enemy");
        MeshMap.add(StaticMeshBuilder.constructVAOFromOBJ("enemy2",false), "enemy2");
        MeshMap.add(StaticMeshBuilder.constructVAOFromOBJ("missile",false), "missile");
        MeshMap.add(StaticMeshBuilder.constructVAOFromOBJ("sphere2",false), "sphere");
        MeshMap.add(StaticMeshBuilder.constructVAOFromOBJ("rail_harness",false), "rail_harness");
        MeshMap.add(StaticMeshBuilder.loadQuadMesh(), "quad");

        MeshBuilder builder = new MeshBuilder(3);
        for (int i = 0; i < 1000; i++) {
            builder.vertices(
                    (float)Math.random(),
                    (float)Math.random(),
                    (float)Math.random()
            );
        }
        MeshMap.add(builder.constructVAO(GL_POINTS), "particles");

        MeshMap.add(constructBarrel(), "barrel");
    }
}
