package scenes;

import main.Input;
import main.RootLayer;
import mote4.scenegraph.Scene;
import mote4.util.matrix.Transform;
import mote4.util.shader.ShaderMap;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Peter on 1/11/17.
 */
public class TitleScreen implements Scene {


    private Transform trans, titleTrans;
    private float particleDist, titleScroll;

    public TitleScreen() {
        trans = new Transform();
        titleTrans = new Transform();
        titleScroll = 2.3f;
    }

    @Override
    public void update(double time, double delta) {
        titleTrans.view.setIdentity();
        titleTrans.view.translate(0,titleScroll);

        titleScroll -= delta * .4;
        if (titleScroll < 0) {
            titleScroll = 0;
        }
        if (Input.isKeyDown(Input.Key.YES) || Input.isKeyDown(Input.Key.NO)) {
            Post.getInstance().fadeTo(RootLayer.getInstance()::loadGame);
        }
    }

    @Override
    public void render(double time, double delta) {
        glDisable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT);

        // render particles
        trans.model.push();
        {
            particleDist += delta*.1;

            trans.model.setIdentity();
            ShaderMap.use("particles");
            Uniform.vec("timestep", (float)time*.2f);
            Uniform.vec("distance", (float) particleDist);
            Uniform.vec("color",1,1,1);

            trans.view.setIdentity();
            //trans.view.rotate(.1f,1,0,0);
            trans.view.translate(0,-0.5f,0); // translate to sit behind the player

            trans.model.translate(-8, -8, -16);
            trans.model.scale(16, 16, 16);

            trans.bind();
            MeshMap.render("particles");
        }
        trans.model.pop();

        ShaderMap.use("texture");
        titleTrans.bind();
        TextureMap.bind("title");
        MeshMap.render("quad");
        if (titleScroll <= 0 && ((time%1) < .5)) {
            if (Input.showGamepad())
                TextureMap.bind("title2_gamepad");
            else
                TextureMap.bind("title2");
            MeshMap.render("quad");
        }
    }

    @Override
    public void framebufferResized(int width, int height) {
        float aspectratio = (float)width/height * (9f/16f);
        trans.projection.setPerspective(width,height,.1f,100,65);
        titleTrans.projection.setOrthographic(-aspectratio, -1,aspectratio,1,-1,1);
    }

    @Override
    public void destroy() {

    }
}
