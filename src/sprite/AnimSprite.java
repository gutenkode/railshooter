package sprite;

import main.Level;
import mote4.util.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import scenes.Ingame;

/**
 * Created by Peter on 3/29/17.
 */
public class AnimSprite extends Sprite {

    private double delay;
    protected boolean repeat = false;
    private int frames, maxFrames;

    public AnimSprite(Texture t, Vector3f p, Vector3f v, Vector2f s, Vector3i si) {
        super(t, p, v, s);
        collides = false;
        maxFrames = si.z;
        frames = 0;
        delay = 1;
        spriteInfo.set(si.x, si.y, 0);
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        delay -= delta*35;
        while (delay <= 0) {
            delay++;
            frames++;
            spriteInfo.z = frames;
        }
        if (!repeat && frames >= maxFrames)
            Level.getInstance().removeSprite(this);
        else
            frames %= maxFrames;
    }
}
