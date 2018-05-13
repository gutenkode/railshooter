package entity;

import main.Level;
import mote4.util.audio.AudioPlayback;
import mote4.util.texture.TextureMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import sprite.AnimSprite;
import sprite.Sprite;

public class BreakableWall extends Wall {

    int health;
    private float rotation;

    public BreakableWall(float x, float y, float z) {
        super(new Vector3f(x,y,z), new Vector3f(1));
        color.set(.7f,.3f,.3f,1);
        hitbox.set(1.1f);
        health = 15;
        collideEnemyBullet = false;
    }

    @Override
    public void update(double delta) {
        rotation += delta*3;
        rot.y = (int)(rotation*5)/5f;
    }

    @Override
    public void onCollide(Sprite s) {
        damage(s.getEnemyDamage());
    }

    @Override
    public void damage(double dmg) {
        health -= dmg;
        if (health <= 0) {

        }
        if (health <= 0) {
            AudioPlayback.playSfx("explode");
            Level.getInstance().addSprite(new AnimSprite(
                    TextureMap.get("explosion"),
                    new Vector3f(pos),
                    new Vector3f(0),
                    new Vector2f(1),
                    new Vector3i(5, 5, 25))
            );
            Level.getInstance().removeEntity(this);
            Level.getInstance().addScore(BASE_SCORE/2);
        }
    }
}
