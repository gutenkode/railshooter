package scenes;

import entity.Player;
import main.Input;
import main.Level;
import main.RootLayer;
import mote4.scenegraph.Scene;
import mote4.util.matrix.Transform;
import mote4.util.shader.ShaderMap;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.FontUtils;
import mote4.util.vertex.mesh.MeshMap;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Peter on 1/11/17.
 */
public class Ingame implements Scene {

    private static String backgroundTex = "background not set";
    private static float particle_b = 1;
    public static void setBackgroundTex(String s) {
        backgroundTex = s;

        // hardcoded for now, I guess
        if (backgroundTex.equals("lv2_background")) {
            particle_b = 0;
        } else
            particle_b = 1;
    }

    //private Level level;
    private Transform trans;
    private float[] camPos;
    private float particleDist, particleTime, gameResetDelay;
    private boolean paused;

    public Ingame() {
        trans = new Transform();
        camPos = new float[3];
        Level.getNewInstance(1);
        paused = false;
        gameResetDelay = 2;
    }

    @Override
    public void update(double time, double delta) {

        if (Level.getInstance().getPlayerHealth() <= 0 || GameUI.isShowingWinText()) {
            gameResetDelay -= delta;
            if (gameResetDelay <= 0) {
                gameResetDelay = 999999; // whatever
                Post.getInstance().fadeTo(RootLayer.getInstance()::resetGame);
            }
        }

        if ((Level.getInstance().getPlayerHealth() > 0 || paused) && Input.isKeyNew(Input.Key.NO)) {
            paused = !paused;
            GameUI.pause(paused);
            System.gc();
        }
        if (!paused) {
            Player p = Level.getInstance().player();

            Level.getInstance().update(delta);
            particleDist -= p.vel().z*.05;
            particleTime = (float)time*.2f;

            // update camera
            for (int i = 0; i < 3; i++) {
                camPos[i] = -p.pos().get(i) + p.vel().get(i);
            }
        }
    }

    @Override
    public void render(double time, double delta) {
        Player p = Level.getInstance().player();

    // render the background
        ShaderMap.use("background");
        TextureMap.bind(backgroundTex);
        float bgX = p.pos().x/20;
        float bgY = p.pos().y/20;
        Uniform.vec("pos", bgX, bgY);
        MeshMap.render("quad");

        glEnable(GL_DEPTH_TEST);
        glClear(GL_DEPTH_BUFFER_BIT);

        trans.view.setIdentity();
        //trans.view.rotate(.1f,1,0,0);
        trans.view.translate(camPos[0],camPos[1]-0.5f,camPos[2]-3); // translate to sit behind the player

        Level.getInstance().render(delta, trans);

    // render the reticle quads
        trans.model.push();
        {
            ShaderMap.use("texture");
            TextureMap.bind("reticle");
            trans.model.setIdentity();
            trans.model.translate(p.pos().x, p.pos().y + .25f, p.pos().z);
            trans.model.rotate(p.rot().x, 0, 1, 0);
            trans.model.rotate(p.rot().y, 1, 0, 0);

            trans.model.translate(0, 0, -3);
            trans.model.scale(.5f, .5f, 1);
            //trans.model.rotate((float)time, 0, 0, 1);
            trans.bind();
            MeshMap.render("quad");

            trans.model.translate(0, 0, -2);
            trans.model.scale(.65f, .65f, 1);
            //trans.model.rotate((float)time*-2, 0, 0, 1);
            trans.bind();
            MeshMap.render("quad");
        }
        trans.model.pop();

    // render particles
        trans.model.push();
        {
            trans.model.setIdentity();
            ShaderMap.use("particles");
            Uniform.vec("timestep",particleTime);
            Uniform.vec("distance",particleDist);
            Uniform.vec("color",1,1,particle_b);

            trans.view.setIdentity();
            //trans.view.rotate(.1f,1,0,0);
            trans.view.translate(camPos[0],camPos[1]-0.5f,0); // translate to sit behind the player

            trans.model.translate(-8, -8, -16);
            trans.model.scale(16, 16, 16);

            trans.bind();
            MeshMap.render("particles");
        }
        trans.model.pop();
    }

    @Override
    public void framebufferResized(int width, int height) {
        trans.projection.setPerspective(width,height,.1f,100,65);
    }

    @Override
    public void destroy() {
        Level.getInstance().destroy();
    }
}
