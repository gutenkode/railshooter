package entity;

import main.Level;
import main.Util;
import mote4.util.audio.AudioPlayback;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import scenes.Ingame;
import sprite.AnimSprite;
import sprite.EnemyBullet;
import sprite.Sprite;

public class Missile extends Entity {

    private float rotation;

    public Missile(float x, float y, float z) {
        super(MeshMap.get("missile"));
        collideEnemyBullet = false;
        pos.set(x,y,z);
        hitbox.set(.5f,.5f,1.5f);
        color.set(1,.8f,.2f,1);
    }

    @Override
    public void update(double delta) {
        pos.z += delta*.6;
        rotation += delta*2.5;
        rot.z = (int)(rotation*5)/5f;
    }

    @Override
    public void onCollide(Sprite s) {
        damage(s.getEnemyDamage());
    }

    @Override
    public void onCollide(Entity e) {
        damage(1);
    }

    @Override
    public void damage(double dmg) {
        if (dmg <= 0)
            return;
        AudioPlayback.playSfx("explode");
        Level.getInstance().addSprite(new AnimSprite(
                TextureMap.get("explosion"),
                new Vector3f(pos),
                new Vector3f(0),
                new Vector2f(2),
                new Vector3i(5, 5, 25))
        );
        Level.getInstance().removeEntity(this);
        Level.getInstance().addScore(BASE_SCORE*2);

        for (int i = 0; i < 100; i++) {
            double[] v = Util.sphereToRect(Math.random()*.025+.06, Math.random()*Math.PI*2, Math.random()*Math.PI-Math.PI/2);

            Level.getInstance().addSprite(new EnemyBullet(
                    new Vector3f(pos),
                    new Vector3f((float)v[0],(float)v[1],(float)v[2]))
            );
        }
    }
}
