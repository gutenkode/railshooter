package scenes;

import main.Level;
import mote4.scenegraph.Scene;
import mote4.scenegraph.target.FBO;
import mote4.scenegraph.target.Target;
import mote4.util.audio.AudioPlayback;
import mote4.util.shader.ShaderMap;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Peter on 1/11/17.
 */
public class Post implements Scene {

    private static Post instance;
    public static Post getInstance() {
        if (instance == null)
            instance = new Post();
        return instance;
    }

    private Runnable callback;
    private FBO ditherScene, brightpassScene, persistScene;
    private FBO[][] blurScene;
    private Random random;
    private float distortCoef, fadeValue, portalValue, portalTimeLimit = 7, portalFadeout = 1.5f;
    private boolean fadeOut, portalOut, portalLevelLoaded;
    private int portalOutLevelNum;

    private Post() {
        random = new Random();
        distortCoef = 0;

        blurScene = new FBO[4][2];
        int blurScale = 256;
        brightpassScene = new FBO(blurScale,blurScale);
        brightpassScene.addToTextureMap("fbo_brightpass");
        for (int i = 0; i < blurScene.length; i++) {
            int res = (int)(blurScale/Math.pow(2,i));
            blurScene[i][0] = new FBO(res,res);
            blurScene[i][0].addToTextureMap("fbo_blur"+i+"_0");
            blurScene[i][1] = new FBO(res,res);
            blurScene[i][1].addToTextureMap("fbo_blur"+i+"_1");
        }

        ShaderMap.use("quad_post");
        Uniform.sampler("tex_scanlines", 1, "scanlines", true);
        Uniform.sampler("tex_noise", 2, "noise", true);
        Uniform.sampler("tex_vignette", 3, "vignette", true);
        Uniform.sampler("tex_blur",4,"fbo_blur0_1",true);
        // 5 is persistance buffer

        ShaderMap.use("quad_stargate");
        Uniform.sampler("tex_color", 6, "colorband", true);
        Uniform.sampler("tex_gradient",7,"gradient",true);
        Uniform.sampler("tex_lensflare",8,"lensflare",true);
    }

    @Override
    public void update(double time, double delta) {
        distortCoef -= delta;
        if (distortCoef < 0)
            distortCoef = 0;
    }

    @Override
    public void render(double time, double delta) {
        glDisable(GL_DEPTH_TEST);
        Target t = Target.getCurrent();

        ShaderMap.use("quad_dither");
        ditherScene.makeCurrent();
        glClear(GL_COLOR_BUFFER_BIT);
        TextureMap.bindFiltered("fbo_scene");
        MeshMap.render("quad");

        createBlur();
        updatePersist();

        ShaderMap.use("quad_post");
        t.makeCurrent();
        glClear(GL_COLOR_BUFFER_BIT);
        TextureMap.bindFiltered("fbo_dither");
        Uniform.vec("colorMult",1-fadeValue,1-fadeValue,1-fadeValue);
        Uniform.vec("distortCoef",distortCoef);
        Uniform.vec("noiseOffset", random.nextFloat(), random.nextFloat());
        MeshMap.render("barrel");


        if (portalOut)
        {
            // portal fade in/out logic
            portalValue += delta;
            if (!portalLevelLoaded && portalValue > portalTimeLimit) {
                Level.getNewInstance(portalOutLevelNum);
                portalLevelLoaded = true;
            } else if (portalValue > portalTimeLimit+portalFadeout) {
                portalOut = false;
                AudioPlayback.stopMusic();
            }
            {
                float alpha;
                if (portalValue < portalTimeLimit)
                    alpha = Math.min(1,portalValue);
                else
                    alpha = Math.min(portalFadeout, portalFadeout-(portalValue-portalTimeLimit))/portalFadeout;

                ShaderMap.use("quad_stargate");
                float c = 1-alpha;
                Uniform.vec("colorMult", 1f,1f,1f,alpha);
                Uniform.vec("colorAdd", c,c,c,0);
                Uniform.vec("time", (float) time);
                TextureMap.bindFiltered("starfield");
                MeshMap.render("quad");
            }
        }

        // the fade callback is performed in the render method so that update will be called before render, after the callback
        // it also makes a bit of sense as being a rendering effect and not an update effect
        if (fadeOut) {
            fadeValue += delta*2.5;
            if (fadeValue >= 1) {
                fadeOut = false;
                callback.run();
            }
        } else {
            fadeValue = (float)Math.max(0, fadeValue-delta*2.5);
        }
    }

    @Override
    public void framebufferResized(int width, int height) {}

    public int[] updateInternalResolution(int width, int height) {
        float lockedAr = 5/4f;
        int h = 240;
        int w = (int)(h*lockedAr);//(int)(h * (float)width / height);

        if (ditherScene != null) {
            ditherScene.destroy();
            TextureMap.delete("fbo_dither");
        }
        ditherScene = new FBO(w,h);
        ditherScene.addToTextureMap("fbo_dither");

        if (persistScene != null) {
            persistScene.destroy();
            TextureMap.delete("fbo_persist");
        }
        persistScene = new FBO(w,h);
        persistScene.addToTextureMap("fbo_persist");

        ShaderMap.use("quad_dither");
        Uniform.vec("screenSize", (float)w,h);

        ShaderMap.use("quad_post");
        Uniform.vec("texSize", (float)w,h);
        float ar = (float)width/height;
        Uniform.vec("aspectRatio",lockedAr/ar);
        Uniform.sampler("tex_persist",5,"fbo_persist",true);

        ShaderMap.use("quad_stargate");
        Uniform.vec("xScale",ar);

        return new int[] {w,h};
    }

    private void createBlur() {
        glBlendFunc(GL_ONE, GL_ONE);
        float stepSize = 1f;

        ShaderMap.use("quad_brightpass");
        ditherScene.getTexture().bindFiltered();
        brightpassScene.makeCurrent();
        glClear(GL_COLOR_BUFFER_BIT);
        MeshMap.render("quad");

        // create the blurred pass at each resolution scale
        ShaderMap.use("quad_blur");
        for (int i = 0; i < blurScene.length; i++) {
            Uniform.vec("screenSize", (float)blurScene[i][0].getWidth(),blurScene[i][0].getHeight());

            blurScene[i][0].makeCurrent();
            if (i == 0)
                brightpassScene.getTexture().bindFiltered();
            else
                blurScene[i-1][1].getTexture().bindFiltered();
            glClear(GL_COLOR_BUFFER_BIT);
            Uniform.vec("step",stepSize,0);
            MeshMap.render("quad");

            blurScene[i][1].makeCurrent();
            blurScene[i][0].getTexture().bindFiltered();
            glClear(GL_COLOR_BUFFER_BIT);
            Uniform.vec("step",0,stepSize);
            MeshMap.render("quad");

            blurScene[i][0].makeCurrent();
            blurScene[i][1].getTexture().bindFiltered();
            glClear(GL_COLOR_BUFFER_BIT);
            Uniform.vec("step",stepSize*2,0);
            MeshMap.render("quad");

            blurScene[i][1].makeCurrent();
            blurScene[i][0].getTexture().bindFiltered();
            glClear(GL_COLOR_BUFFER_BIT);
            Uniform.vec("step",0,stepSize*2);
            MeshMap.render("quad");
        }

        // add all of the passes together, render each pass into the first highest-res pass
        ShaderMap.use("quad");
        blurScene[0][1].makeCurrent();
        for (int i = 1; i < blurScene.length; i++) {
            //glClear(GL_COLOR_BUFFER_BIT);
            blurScene[i][1].getTexture().bindFiltered();
            MeshMap.render("quad");
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    private void updatePersist() {

    }

    public void distort() {
        distortCoef = 1.2f;
    }

    public void fadeTo(Runnable func) {
        if (fadeOut)
            return;
        fadeValue = 0;
        callback = func;
        fadeOut = true;
    }
    public void warpTo(int levelNum) {
        if (portalOut)
            return;
        portalValue = 0;
        portalOut = true;
        portalLevelLoaded = false;
        portalOutLevelNum = levelNum;
    }

    @Override
    public void destroy() {
        ditherScene.destroy();
    }
}
