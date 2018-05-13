package entity;

import main.Level;
import mote4.util.audio.AudioPlayback;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import sprite.HealthPulse;

public class HealthPickup extends Entity {

    public HealthPickup(float x, float y, float z) {
        super(MeshMap.get("quad"));
        pos.set(x,y,z);
        collideEnemyBullet = false;
        collidePlayerBullet = false;
        scale.y = -1;
        isSolid = false;
    }

    @Override
    public void update(double delta) {
        rot.x += delta*3;
    }

    @Override
    public void render(double delta, TransformationMatrix model) {
        TextureMap.bind("healthpickup");
        Uniform.vec("textureCoef",1f);
        super.render(delta, model);
        Uniform.vec("textureCoef",0f);
    }

    @Override
    public void damage(double dmg) {

    }

    @Override
    public void onCollide(Entity e) {
        if (e instanceof Player) {
            AudioPlayback.playSfx("heal");
            Level.getInstance().addSprite(new HealthPulse(pos));
            Level.getInstance().removeEntity(this);
            Level.getInstance().healPlayer(2);
        }
    }
}
