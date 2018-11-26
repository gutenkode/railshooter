package entity;

import main.Level;
import mote4.scenegraph.Window;
import mote4.util.audio.AudioPlayback;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector3f;
import scenes.GameUI;
import sprite.BossBullet;
import sprite.PlayerBullet;
import sprite.Sprite;

public class Boss2 extends Entity {

    private Vector3f rot;
    private double shakeVel;
    private int health, maxHealth, renderHealth;
    private double hitCooldown = 0, healthDisplayCooldown, bulletCooldown;
    private float entryOffset, introDelay = 3.5f;

    public Boss2() {
        super(MeshMap.get("sphere"));
        pos.set(0,0,-15);
        scale.set(3);
        rot = new Vector3f(0);
        hitbox.set(2.75f);
        color.set(.2f,.1f,.1f,1);

        health = maxHealth = 20;
        renderHealth = 0;

        collideEnemyBullet = false;
        bulletCooldown = 1.25;

        entryOffset = 20;
        offset.set(0,entryOffset,0);
        collidePlayerBullet = false;
    }

    @Override
    public void update(double delta) {
        rot.z += delta/2 - delta*shakeVel/2;
        vel.z = Level.getInstance().player().vel.z;
        pos.z = Level.getInstance().player().pos.z-15;

        if (Math.abs(Level.getInstance().player().pos.z) < 1000*.4f)
            return;

        if (entryOffset > 0) {
            entryOffset -= delta*4;
            entryOffset = Math.max(0,entryOffset);
            offset.set(0,entryOffset,0);
            if (entryOffset == 0) {
                collidePlayerBullet = true;
                AudioPlayback.playMusic("sorrydave",false);
            }
        } else
            introDelay -= delta;

        if (introDelay > 0)
            return;

        if (health == 0) {
            rot.y += 2*delta; // continue rotating once health is gone
            if (rot.y > 3) { // die
                scale.set(0,0,0);
                hitbox.set(0,0,0);
                collidePlayerBullet = false;
            }
        } else {
            rot.y *= 1 - delta;
            rot.y += shakeVel * .5 * delta;
        }
        shakeVel -= rot.y * 8 * delta;

        hitCooldown -= delta;
        healthDisplayCooldown -= delta;

        if (entryOffset == 0 && healthDisplayCooldown <= 0) {
            healthDisplayCooldown = .1;
            if (renderHealth < maxHealth) {
                renderHealth+=2;
                AudioPlayback.playSfx("blip");
                if (renderHealth == maxHealth) {
                    AudioPlayback.playMusic("boss",true);
                    health = maxHealth; // just to make sure the boss starts maxed out
                }
                GameUI.showBossHealth(true, "HAL 9000", renderHealth, maxHealth);
            }
        }

        if (health > 0) {// && RailHarness.getNumHarnesses() == 0) {
            addBullets(delta);
        }
    }
    public void addBullets(double delta) {
        if (bulletCooldown <= 0) {
            bulletCooldown = .25;
            float rX = pos.x - Level.getInstance().player().pos.x;
            float rY = pos.y - Level.getInstance().player().pos.y;
            float rZ = pos.z - Level.getInstance().player().pos.z;
            Vector3f newVel = new Vector3f(-rX, -rY, -rZ);
            newVel.normalize();
            newVel.mul(.25f);
            newVel.z *= .6;
            newVel.z += vel.z;
            Vector3f newPos = new Vector3f(pos);
            Level.getInstance().addSprite(
                    new BossBullet(newPos, newVel, new Vector3f(0,0, 0))
            );
        } else
            bulletCooldown -= delta;
    }
    @Override
    public void render(double delta, TransformationMatrix model) {
        if (rot.y > 4 || (health == 0 && Window.time()%.15>.075))
            return; // flash once health is gone

        float distort = Math.max(0,rot.y + (1-(float)health/maxHealth)/3);
        Uniform.vec("geomDistortCoef",distort);
        super.rot.x = (int)(this.rot.x*30)/30f;
        super.rot.y = (int)(this.rot.y*30)/30f;
        super.rot.z = (int)(this.rot.z*30)/30f;
        super.render(delta, model);
        Uniform.vec("geomDistortCoef",0);

        // render the eye
        TextureMap.bind("hal_eye");
        Uniform.vec("textureCoef",1f);
        Uniform.vec("color",1,1,1,1);
        float x = -Level.getInstance().player().pos.y/8;
        float y = Level.getInstance().player().pos.x/8;

        model.rotate(-rot.x,0,1,0);
        model.rotate(-rot.y,1,0,0);
        model.rotate(-rot.z,0,0,1);
        model.rotate(x,1,0,0);
        model.rotate(y,0,1,0);
        model.rotate((float)Math.PI,0,0,1);

        model.translate(0,0,1.2f);
        model.scale(.4f,.4f,1);
        model.bind();
        MeshMap.render("quad");
        Uniform.vec("textureCoef",0f);
    }

    @Override
    public void onCollide(Sprite s) {
        if (s instanceof PlayerBullet)
            damage(s.getEnemyDamage());
    }

    @Override
    public void damage(double dmg) {
        if (hitCooldown > 0 || health == 0 || renderHealth < maxHealth)
            return;
        hitCooldown = .3;
        AudioPlayback.playSfx("explode");
        shakeVel += 1;
        health--;
        if (health <= 0) {
            health = 0;
            AudioPlayback.playSfx("big_explode");
            Level.getInstance().addScore(BASE_SCORE*6);
        }
        GameUI.showBossHealth(true, "HAL 9000", health, maxHealth);
    }

    public int getHealth() { return health; }
}
