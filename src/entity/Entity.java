package entity;

import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.vertex.mesh.Mesh;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector3f;
import org.joml.Vector4f;
import sprite.Sprite;

/**
 * An object in the game world.
 * Created by Peter on 1/11/17.
 */
public abstract class Entity {

    public static final int BASE_SCORE = 100;

    protected Vector3f pos, vel, rot, scale, offset, hitbox;
    protected Vector4f color;
    protected Mesh mesh;
    protected boolean collideEnemyBullet = true, collidePlayerBullet = true, isSolid = true;

    public Entity(Mesh m) {
        mesh = m;
        pos = new Vector3f();
        offset = new Vector3f();
        vel = new Vector3f();
        scale = new Vector3f(1);
        hitbox = new Vector3f(1);
        rot = new Vector3f();
        color = new Vector4f(1);
    }

    public abstract void update(double delta);

    public void render(double delta, TransformationMatrix mat) {
        mat.setIdentity();
        mat.translate(pos.x+offset.x,pos.y+offset.y,pos.z+offset.z);
        mat.rotate(rot.x,0,1,0);
        mat.rotate(rot.y,1,0,0);
        mat.rotate(rot.z,0,0,1);
        mat.scale(scale.x,scale.y,scale.z);
        mat.bind();
        Uniform.vec("color", color.x, color.y, color.z, color.w);
        mesh.render();
    }
    public final void renderHitbox(TransformationMatrix mat) {
        mat.setIdentity();
        mat.translate(pos.x,pos.y,pos.z);
        mat.scale(hitbox.x,hitbox.y,hitbox.z);
        mat.bind();
        MeshMap.render("cube");
    }

    public Vector3f pos() { return pos; }
    public Vector3f vel() { return vel; }
    public Vector3f rot() { return rot; }

    /**
     * Whether the given Sprite is inside the hitbox of this Entity.
     * @param s
     * @return
     */
    public boolean collidesWith(Sprite s) {
        if (s.canCollide()) {
            // TODO add check for player and enemy collision
            return (s.pos().x + s.scale().x > pos.x - hitbox.x && s.pos().x - s.scale().x < pos.x + hitbox.x &&
                    s.pos().y + s.scale().y > pos.y - hitbox.y && s.pos().y - s.scale().y < pos.y + hitbox.y &&
                    s.pos().z + Math.abs(s.vel().z) > pos.z - hitbox.z && s.pos().z < pos.z + hitbox.z);
        }
        return false;
    }
    /**
     * Whether the hitbox of the given Entity intersects with the hitbox of this Entity.
     * Current unimplemented.
     * @param e
     * @return
     */
    public boolean collidesWith(Entity e) {
        if (this == e) // don't collide with yourself!
            return false;

        return (e.pos().x+e.hitbox.x > pos.x-hitbox.x && e.pos().x-e.hitbox.x < pos.x+hitbox.x &&
                e.pos().y+e.hitbox.y > pos.y-hitbox.y && e.pos().y-e.hitbox.y < pos.y+hitbox.y &&
                e.pos().z+e.hitbox.z > pos.z-hitbox.z && e.pos().z-e.hitbox.z < pos.z+hitbox.z);
    }

    public void onCollide(Sprite s) {}
    public void onCollide(Entity e) {}
    public boolean collideEnemyBullet() { return collideEnemyBullet; }
    public boolean collidePlayerBullet() { return collidePlayerBullet; }
    public boolean isSolid() { return isSolid; }

    public abstract void damage(double dmg);
}
