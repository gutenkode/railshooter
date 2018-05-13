package entity;

import main.Level;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector3f;

/**
 * Created by Peter on 1/11/17.
 */
public class Wall extends Entity {

    public Wall(Vector3f p, Vector3f s) {
        this(p,s,.5f);
    }
    public Wall(Vector3f p, Vector3f s, float c) {
        super(MeshMap.get("cube"));
        pos.set(p);
        hitbox.set(s);
        scale.set(s);
        color.set(c,c,c,1);
    }

    @Override
    public void update(double delta) {}

    @Override
    public void damage(double dmg) {}
}
