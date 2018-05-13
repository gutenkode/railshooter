package sprite;

import org.joml.Vector3f;

public class BossBullet extends EnemyBullet {

    private Vector3f dampen;

    public BossBullet(Vector3f p, Vector3f v, Vector3f d) {
        super(p, v);
        dampen = d;
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        //vel.x *= 1-0.5f*delta;
        //vel.y *= 1-0.5f*delta;

        vel.x *= 1+dampen.x*delta;
        vel.y *= 1+dampen.y*delta;
        vel.z *= 1-dampen.z*delta;
    }
}
