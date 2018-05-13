package entity;

import main.Input;
import main.Level;
import main.Util;
import mote4.util.audio.AudioPlayback;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import sprite.AnimSprite;
import sprite.PlayerBullet;
import sprite.Sprite;

/**
 * Created by Peter on 1/11/17.
 */
public class Player extends Entity {

    private int numBullets;
    private float bulletDelay, shake;
    private double damageCooldown, explosionCooldown, accelX, accelY;
    private Vector4f defaultColor;

    private final int
            BULLETS_PER_KEYPRESS = 3; // number of shots to auto-fire on one keypress
    private final float
            SHOT_REPEAT_DELAY = 5, // delay in (60 fps) frames between auto-firing
            XY_BOUNCE = .35f, // bounciness of collisions in x,y axis
            Z_BOUNCE = .97f; // bounciness of collisions in z axis
    private final double
            FORWARD_COEF = 1.5,
            TURN_COEF = 1.0,
            ROT_COEF = 1.2,
            DAMAGE_COOLDOWN_MAX = 1;

    public Player() {
        super(MeshMap.get("arwing"));
        collidePlayerBullet = false;
        scale.set(.5f,.5f,.5f);
        defaultColor = new Vector4f(.5f,.5f,.7f,1);
        color.set(defaultColor);
        hitbox.set(.3f,.2f,.15f);
        offset.y = -.1f; // center model in its hitbox
        damageCooldown = 0;
    }

    @Override
    public void update(double delta) {
        boolean alive = Level.getInstance().getPlayerHealth() > 0;
        if (alive) {
            damageCooldown -= delta;
        } else {
            damageCooldown = DAMAGE_COOLDOWN_MAX;
            explosionCooldown -= delta;
            if (explosionCooldown <= 0) {
                explosionCooldown = .2;
                AudioPlayback.playSfx("explode");
                Vector3f newPos = new Vector3f(pos);
                newPos.x += Math.random() * 1 - .5;
                newPos.y += Math.random() * 1 - .5;
                newPos.z += Math.random() * 1 - .5;
                float randSize = (float)(Math.random() * .3);
                Level.getInstance().addSprite(new AnimSprite(
                        TextureMap.get("explosion"),
                        newPos,
                        new Vector3f(0),
                        new Vector2f(.3f+randSize),
                        new Vector3i(5, 5, 25)));
            }
        }
        damageCooldown = Math.max(0,damageCooldown);
        float deltaCoef = (float)(delta*60); // == 1 at 60fps

        // turn red when hit
        color.y = defaultColor.y*(float)(DAMAGE_COOLDOWN_MAX -damageCooldown);
        color.z = defaultColor.z*(float)(DAMAGE_COOLDOWN_MAX -damageCooldown);
        // shake when hit
        shake = (float)Math.sin(damageCooldown*20)/2*(float)damageCooldown;

        // steering
        if (alive) {
            if (Input.isKeyDown(Input.Key.UP)) {
                accelY += delta * 2;
                accelY = Math.min(1, accelY);
                vel.y += delta * TURN_COEF;
                rot.y += delta * ROT_COEF * accelY;
            } else if (Input.isKeyDown(Input.Key.DOWN)) {
                accelY += delta * 2;
                accelY = Math.min(1, accelY);
                vel.y -= delta * TURN_COEF;
                rot.y -= delta * ROT_COEF * accelY;
            } else {
                //accelY = .3;
                accelY -= delta;
                accelY = Math.max(.5, accelY);
            }
            if (Input.isKeyDown(Input.Key.LEFT)) {
                accelX += delta * 2;
                accelX = Math.min(1, accelX);
                vel.x -= delta * TURN_COEF;
                rot.x += delta * ROT_COEF * accelX;
                rot.z += delta * ROT_COEF * accelX;
            } else if (Input.isKeyDown(Input.Key.RIGHT)) {
                accelX += delta * 2;
                accelX = Math.min(1, accelX);
                vel.x += delta * TURN_COEF;
                rot.x -= delta * ROT_COEF * accelX;
                rot.z -= delta * ROT_COEF * accelX;
            } else {
                //accelX = .3;
                accelX -= delta;
                accelX = Math.max(.5, accelX);
            }
        }

        // move forward, cannot thrust while moving backward e.g. in knockback
        if (alive && vel.z < .07) {
            vel.z -= delta*FORWARD_COEF;
        }

        // shoot
        if (Input.isKeyDown(Input.Key.YES) && alive) {
            if (numBullets > 0 && bulletDelay <= 0) {
                bulletDelay = SHOT_REPEAT_DELAY;
                numBullets--;

                // for some reason the .7 tweak is needed to make bullets shoot accurately... no idea why, but it works
                double[] v = Util.sphereToRect(1, rot.x*.7, rot.y*.7+Math.PI/2);
                Vector3f newVel = new Vector3f(vel);
                newVel.add((float)-v[1],(float)-v[2],(float)-v[0]);

                AudioPlayback.playSfx("laser");
                Level.getInstance().addSprite(new PlayerBullet(new Vector3f(pos), newVel));
            } else {
                bulletDelay -= deltaCoef;
            }
        } else {
            bulletDelay = 0;
            numBullets = BULLETS_PER_KEYPRESS;
        }


        // add velocity to position, and collision detection
        pos.x += vel.x*deltaCoef;
        if (Level.getInstance().collidesWith(this) != null || (Math.abs(pos.x) > 4)) {
            pos.x -= vel.x*deltaCoef;
            vel.x *= -XY_BOUNCE;
        }
        pos.y += vel.y*deltaCoef;
        if (Level.getInstance().collidesWith(this) != null || (Math.abs(pos.y) > 4)) {
            pos.y -= vel.y*deltaCoef;
            vel.y *= -XY_BOUNCE;
        }
        pos.z += vel.z*deltaCoef;
        if (Level.getInstance().collidesWith(this) != null) {
            pos.z -= vel.z*deltaCoef;
            vel.z *= -Z_BOUNCE;
            damage(1);
        }

        // dampen velocity and rotation
        vel.mul(1-.06f*deltaCoef);
        rot.mul(1-.02f*deltaCoef);
    }

    @Override
    public void render(double delta, TransformationMatrix mat) {
        if (damageCooldown <= 0 || (int)(damageCooldown*10) % 2 == 0) {// flash after damage

            mat.setIdentity();
            mat.translate(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);
            mat.rotate(rot.x+shake, 0, 1, 0);
            mat.rotate(rot.y+shake, 1, 0, 0);
            mat.rotate(rot.z, 0, 0, 1);
            mat.scale(scale.x, scale.y, scale.z);
            mat.bind();
            Uniform.vec("color", color.x, color.y, color.z, color.w);
            mesh.render();
        }
    }

    @Override
    public void onCollide(Sprite s) {
        damage(s.getPlayerDamage());
    }
    @Override
    public void onCollide(Entity e) {
        if (e.isSolid) {
            damage(1);
            vel.z = Math.abs(vel.z);
        }
    }

    @Override
    public void damage(double dmg) {
        if (dmg <= 0 || damageCooldown > 0)
            return;
        damageCooldown = DAMAGE_COOLDOWN_MAX;
        Level.getInstance().damagePlayer(1);
        AudioPlayback.playSfx("hurt");
    }
}
