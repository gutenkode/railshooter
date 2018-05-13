package entity;

import mote4.util.audio.AudioPlayback;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import sprite.PlayerBullet;
import sprite.Sprite;

import static org.lwjgl.opengl.GL11.*;

public class Shield extends Entity {

    private RailHarness harness;
    private float offsetX, offsetY, alpha, minAlpha = .0f, rotation;

    public Shield(RailHarness harness, float rotation) {
        super(MeshMap.get("quad"));
        this.harness = harness;
        harness.setShield(this);
        setActive(false);

        offsetX = 2*Math.signum(harness.pos.x);
        offsetY = 2*Math.signum(harness.pos.y);
        pos.add(offsetX,offsetY,harness.pos.z);
        alpha = minAlpha;
        this.rotation = rotation*(float)Math.PI/2;

        collideEnemyBullet = false;
    }

    @Override
    public void update(double delta) {
        pos.z = harness.pos.z+3.7f;
        vel.set(harness.vel);
        if (alpha > minAlpha)
            alpha -= delta*2.5f;
        else
            alpha = minAlpha;
    }

    @Override
    public void render(double delta, TransformationMatrix model) {
        glBlendFunc(GL_SRC_ALPHA,GL_ONE);
        //glDisable(GL_DEPTH_TEST);
        Uniform.vec("textureCoef",1f);
        Uniform.vec("alpha",alpha);
        TextureMap.bind("shield");
        super.render(delta, model);
        Uniform.vec("alpha",1);
        //TextureMap.bind("shield_corner");
        //rot.z = rotation;
        //super.render(delta, model);
        //rot.z = 0;
        Uniform.vec("textureCoef",0f);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        //glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void onCollide(Sprite s) {
        if (s instanceof PlayerBullet) {
            alpha = 1;
            AudioPlayback.playSfx("reflect");
        }
    }

    @Override
    public void damage(double dmg) {

    }

    public void setActive(boolean b) {
        if (b) {
            collidePlayerBullet = true;
            scale.set(2,2,1);
            hitbox.set(2,2,.05f);
        } else {
            collidePlayerBullet = false;
            hitbox.set(0, 0, 0);
            scale.set(0, 0, 0);
        }
    }
}
