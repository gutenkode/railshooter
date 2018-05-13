package sprite;

import entity.Entity;
import main.Level;
import mote4.util.texture.Texture;
import mote4.util.texture.TextureMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import scenes.Ingame;

/**
 * Created by Peter on 3/30/17.
 */
public class EnemyBullet extends AnimSprite {
    public EnemyBullet(Vector3f p, Vector3f v) {
        super(TextureMap.get("bullet2"), p, v, new Vector2f(.5f), new Vector3i(6,1,6));
        collides = true;
        playerDamage = 1;
        repeat = true;
    }
    @Override
    public void onCollide(Entity e) {
        if (e.collideEnemyBullet()) {
            Level.getInstance().removeSprite(this);
            Level.getInstance().addSprite(new AnimSprite(
                    TextureMap.get("red_explosion"),
                    new Vector3f(pos),
                    new Vector3f(0),
                    new Vector2f(.5f),
                    new Vector3i(3, 2, 6))
            );
        }
    }
}
