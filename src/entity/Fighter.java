package entity;

import main.Level;
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

/**
 * Created by Peter on 3/30/17.
 */
public class Fighter extends Entity {

    double health, cooldown;

    public Fighter(float x, float y, float z) {
        super(MeshMap.get("enemy"));
        collideEnemyBullet = false;
        pos.set(x,y,z);
        color.set(.5f,.2f,.2f,1);
        hitbox.set(1.3f,.6f,.6f);
        rot.y = (float)Math.PI;
        health = 10;
        cooldown = 1;
    }

    @Override
    public void update(double delta) {
        pos.z += delta*2;
        if (Level.getInstance().player().pos().x > pos.x)
            vel.x += delta*.1;
        else
            vel.x -= delta*.1;
        if (Level.getInstance().player().pos().y > pos.y)
            vel.y += delta*.1;
        else
            vel.y -= delta*.1;

        cooldown -= delta;
        if (cooldown <= 0) {
            cooldown = 1;
            float rX = (float)(Math.random()*.1)-.05f;
            float rY = (float)(Math.random()*.1)-.05f;
            Level.getInstance().addSprite(
                    new EnemyBullet(new Vector3f(pos), new Vector3f(rX,rY, .3f))
            );
        }

        //pos.x += vel.x;
        //pos.y += vel.y;
        vel.x *= 1-delta*3;
        vel.y *= 1-delta*3;
    }

    @Override
    public void onCollide(Sprite s) {
        damage(s.getEnemyDamage());
    }

    @Override
    public void onCollide(Entity e) {
        if (e instanceof Player) {
            damage(health+1); // instant death
        }
    }

    @Override
    public void damage(double dmg) {
        if (dmg <= 0)
            return;
        health -= dmg;
        health = Math.max(0,health);
        if (health <= 0) {
            AudioPlayback.playSfx("explode");
            Level.getInstance().addSprite(new AnimSprite(
                    TextureMap.get("explosion"),
                    new Vector3f(pos),
                    new Vector3f(0),
                    new Vector2f(2),
                    new Vector3i(5, 5, 25))
            );
            Level.getInstance().removeEntity(this);
            Level.getInstance().addScore(BASE_SCORE);
        }
    }
}
