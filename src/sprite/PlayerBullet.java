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
public class PlayerBullet extends AnimSprite {

    public PlayerBullet(Vector3f p, Vector3f v) {
        super(TextureMap.get("bullet1"), p, v, new Vector2f(.3f), new Vector3i(4,1,4));
        collides = true;
        repeat = true;
        enemyDamage = 5;
    }
    @Override
    public void onCollide(Entity e) {
        if (e.collidePlayerBullet()) {
            Level l = Level.getInstance();
            l.removeSprite(this);
            Vector3f newPos = new Vector3f(pos);
            newPos.z += 2; // so the explosion appears in front of whatever it hit
            l.addSprite(new AnimSprite(
                    TextureMap.get("blue_explosion"),
                    newPos,
                    new Vector3f(0),
                    new Vector2f(.6f),
                    new Vector3i(3, 2, 6))
            );
        }
    }
}
