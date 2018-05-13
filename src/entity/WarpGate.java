package entity;

import main.Level;
import mote4.scenegraph.Window;
import mote4.util.audio.AudioPlayback;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector3f;
import scenes.Post;

public class WarpGate extends Entity {

    private boolean playCharge = true, playPortal = true;
    private float radius, rotateSpeed, portalScale, portalPulse;
    public WarpGate(float x, float y, float z) {
        super(MeshMap.get("cube"));
        collideEnemyBullet = false;
        collidePlayerBullet = false;
        pos.set(x,y,z);
        scale.set(.6f,.6f,.2f);
        hitbox.set(1);
        offset.y = 0;
        radius = 0;
    }

    @Override
    public void update(double delta) {
        pos.x *= 1-delta;
        pos.y *= 1-delta;
        if (radius < 3.5)
            radius += delta;

        if (playCharge && rotateSpeed > 2) {
            AudioPlayback.playSfx("portal_charge");
            playCharge = false;
        }
        if (playPortal && rotateSpeed > 6) {
            AudioPlayback.playSfx("portal");
            playPortal = false;
        }

        if (rotateSpeed < 7)
            rotateSpeed += delta;
        else {
            Post.getInstance().warpTo(2);
        }

        rot.z += delta*rotateSpeed;
        pos.z = Level.getInstance().player().pos.z-10;// + 3*(float)Math.cos(clock/2);
        vel.z = Level.getInstance().player().vel.z;

        portalPulse = (.15f*radius)*(float)Math.sin(Window.time()*rotateSpeed);
        portalScale = radius*.8f;
    }

    @Override
    public void render(double delta, TransformationMatrix model) {
        color.set(1,1,1,1);
        TextureMap.bind("obelisk");
        Uniform.vec("textureCoef",1f);
        {
            int steps = 7;
            double piStep = Math.PI/(steps/2d);
            for (int i = 0; i < steps; i++) {
                float x = radius*(float)Math.cos(i*piStep-rot.z);
                float y = radius*(float)Math.sin(i*piStep-rot.z);
                offset.set(x,y, 0);
                super.render(delta, model);
            }
        }

        // render the glowing center portal
        model.setIdentity();
        model.translate(pos.x,pos.y,pos.z);
        model.rotate(rot.z/2f,0,0,1);
        model.scale(portalScale+portalPulse,portalScale+portalPulse,1);
        model.bind();
        TextureMap.bind("portal");
        MeshMap.render("quad");

        Uniform.vec("textureCoef",0f);
    }

    @Override
    public void damage(double dmg) {

    }
}
