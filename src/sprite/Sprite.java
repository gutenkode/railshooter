package sprite;

import entity.Entity;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.Texture;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

/**
 * Simple quad that displays a texture.
 * Used for displaying bullets and other effects.
 * Created by Peter on 1/15/17.
 */
public abstract class Sprite {

    protected Vector3f pos, vel;
    protected Vector2f scale;
    protected Vector3i spriteInfo;
    protected Vector4f color;
    protected Texture texture;
    protected boolean collides;
    protected double enemyDamage = 0, playerDamage = 0;

    public Sprite(Texture t, Vector3f p, Vector3f v, Vector2f s) {
        texture = t;
        pos = p;
        vel = v;
        scale = s;

        color = new Vector4f(1);
        collides = true;
        spriteInfo = new Vector3i(1,1,0);
    }

    public void update(double delta) {
        pos.x += vel.x*(delta*60);
        pos.y += vel.y*(delta*60);
        pos.z += vel.z*(delta*60);
    }
    public void onCollide(Entity e) {}

    public void render(double delta, TransformationMatrix mat) {
        mat.setIdentity();
        mat.translate(pos.x,pos.y,pos.z);
        mat.scale(scale.x,scale.y,1);
        mat.bind();
        //Uniform.vec("color", color);
        Uniform.vec("spriteInfo", (float)spriteInfo.x, spriteInfo.y, spriteInfo.z);
        texture.bind();
        MeshMap.render("quad");
    }
    public final void renderHitbox(TransformationMatrix mat) {
        mat.setIdentity();
        mat.translate(pos.x,pos.y,pos.z);
        mat.scale(scale.x,scale.y,1);
        mat.bind();
        MeshMap.render("quad");
    }

    public Vector3f pos() { return pos; }
    public Vector3f vel() { return vel; }
    public Vector2f scale() { return scale; }

    public boolean canCollide() { return collides; }
    public double getEnemyDamage() { return enemyDamage; }
    public double getPlayerDamage() { return playerDamage; }
}
