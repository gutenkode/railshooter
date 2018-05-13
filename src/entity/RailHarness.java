package entity;

import main.Level;
import mote4.util.audio.AudioPlayback;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import sprite.AnimSprite;
import sprite.EnemyBullet;
import sprite.PlayerBullet;
import sprite.Sprite;

public class RailHarness extends Entity {

    private static int numHarnesses;

    private boolean active = true;
    private Shield shield;
    private float damageAmt, knockback, bulletCooldown = 2, explosionCooldown, entryOffset;

    public RailHarness(float x, float y) {
        super(MeshMap.get("rail_harness"));
        pos.set(x,y,-15);
        scale.set(1.25f, 1.5f,5);
        hitbox.set(1.25f, 1.5f,5);
        color.set(.3f,.2f,.2f,1);
        collidePlayerBullet = true;
        collideEnemyBullet = false;

        entryOffset = 35;
        offset.set(0,0,entryOffset);

        numHarnesses++;
    }

    public void setShield(Shield s) {
        shield = s;
    }

    @Override
    public void update(double delta) {

        boolean playerInRange = Math.abs(Level.getInstance().player().pos.z) > 400*.4f;
        if (playerInRange) {
            if (entryOffset > 0) {
                entryOffset -= delta * 15;
                offset.set(0, 0, entryOffset);
                if (entryOffset <= 0)
                    shield.setActive(true);
            } else {
                entryOffset = 0;
                offset.set(0, 0, 0);

                if (knockback > 12) {
                    if (active) {
                        shield.setActive(false);
                        numHarnesses--;
                        active = false;
                        AudioPlayback.playSfx("big_explode");
                    }
                    damageAmt *= 1 + delta * 3;
                    rot.z = (int) ((knockback % 1 - .5f) * 15) / 15f;
                    rot.z *= .2;
                    explosionCooldown -= delta;
                    if (explosionCooldown < 0) {
                        addExplosion();
                        explosionCooldown = .3f;
                    }
                } else {
                    damageAmt *= 1 - delta;
                    //knockback *= 1-delta;

                    bulletCooldown -= delta;
                    if (bulletCooldown <= 0) {
                        bulletCooldown = numHarnesses - .55f;
                        float rX = pos.x - Level.getInstance().player().pos.x;
                        float rY = pos.y - Level.getInstance().player().pos.y;
                        float rZ = pos.z - Level.getInstance().player().pos.z;
                        Vector3f newVel = new Vector3f(-rX, -rY, -rZ);
                        newVel.mul(.01f);
                        newVel.z *= .6;
                        newVel.z += vel.z;//+.06f;
                        Vector3f newPos = new Vector3f(pos);
                        newPos.add(-.6f * Math.signum(pos.x), 0, 5);
                        Level.getInstance().addSprite(
                                new EnemyBullet(newPos, newVel)
                        );
                    }
                }
                knockback += damageAmt * delta;
            }
        }
        pos.z = Level.getInstance().player().pos.z-15f+knockback/3f;
        vel.z = Level.getInstance().player().vel.z;
    }

    @Override
    public void onCollide(Sprite s) {
        if (s instanceof PlayerBullet)
            damage(s.getEnemyDamage());
    }

    @Override
    public void damage(double dmg) {
        damageAmt += 1;
        addExplosion();
    }
    private void addExplosion() {
        AudioPlayback.playSfx("explode");
        Vector3f newPos = new Vector3f(pos);
        Vector3f newVel = new Vector3f(vel);
        float x = (float)Math.random()-.5f;
        float y = (float)Math.random()-.5f;
        newVel.add(x*.1f,y*.1f,.05f);
        Level.getInstance().addSprite(new AnimSprite(
                TextureMap.get("explosion"),
                new Vector3f(newPos),
                new Vector3f(newVel),
                new Vector2f(1f),
                new Vector3i(5, 5, 25))
        );
    }

    public static int getNumHarnesses() { return numHarnesses; }
}
