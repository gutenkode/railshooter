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

public class Fighter2 extends Entity {
    double health, cooldown, angle;

    public Fighter2(float x, float y, float z) {
        super(MeshMap.get("enemy2"));
        collideEnemyBullet = false;
        pos.set(x,y,z);
        hitbox.set(.9f,.6f,.9f);
        color.set(.2f,1,.2f,1);
        health = 15;
        cooldown = 1;
        angle = Math.random()*Math.PI*2;
    }

    @Override
    public void update(double delta) {
        pos.z += delta/2;

        cooldown -= delta;
        if (cooldown <= 0) {
            cooldown = .4;
            float rX = (float)(Math.sin(angle)*.03);
            float rY = (float)(Math.cos(angle)*.03);
            angle += .14;
            float speed = .12f;

            Level.getInstance().addSprite(new EnemyBullet(new Vector3f(pos), new Vector3f(rX,rY, speed)));
            Level.getInstance().addSprite(new EnemyBullet(new Vector3f(pos), new Vector3f(-rX,-rY, speed)));
        }
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
