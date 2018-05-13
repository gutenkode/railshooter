package entity;

import main.Level;
import mote4.scenegraph.Window;
import mote4.util.audio.AudioPlayback;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import scenes.GameUI;
import sprite.AnimSprite;
import sprite.BossBullet;
import sprite.PlayerBullet;
import sprite.Sprite;

public class Boss2Core extends Entity {

    private Boss2 shell;
    private double bulletCooldown = 2, wallCooldown = 3, healthDisplayCooldown, hitCooldown;
    private int health, maxHealth, renderHealth;
    private float hitSpin, zOffset = 0, linearScale, explodeDelay;
    private boolean defeated = false;

    public Boss2Core(Boss2 b) {
        super(MeshMap.get("cube"));
        shell = b;
        pos.set(b.pos);
        setScale(1);
        rot.z = (float)Math.PI/2;
        setScale(.7f);
        collideEnemyBullet = false;
        collidePlayerBullet = false;

        health = maxHealth = 36;
        renderHealth = 0;
    }
    private void setScale(float s) {
        linearScale = s;
        scale.set(-3*s,s,s);
        hitbox.set(s,3*s,s);
    }

    @Override
    public void update(double delta) {

        if (shell.getHealth() == 0) {
            if (zOffset > 7 && linearScale < 3) {
                linearScale += delta*2;
                linearScale = Math.min(linearScale, 2);
                setScale(linearScale);
            }
            if (zOffset < 15)
                zOffset += delta*3;
            else {
                if (health > 0) {
                    addBullets(delta);
                    addWalls(delta);
                } else {
                    if (offset.y > -35) {
                        if (explodeDelay <= 0) {
                            explodeDelay = .3f;
                            addExplosion();
                        } else
                            explodeDelay -= delta;
                        offset.y -= delta * 6; // move down, into the sun
                    } else {
                        if (!defeated) {
                            AudioPlayback.playSfx("big_explode");
                            AudioPlayback.stopMusic();
                            GameUI.showWinText();
                            defeated = true;
                        }
                    }
                }
                if (healthDisplayCooldown <= 0) {
                    healthDisplayCooldown = .1;
                    if (renderHealth < maxHealth) {
                        renderHealth += 2;
                        AudioPlayback.playSfx("blip");
                        if (renderHealth == maxHealth) {
                            health = maxHealth; // just to make sure the boss starts maxed out
                            collidePlayerBullet = true;
                        }
                        GameUI.showBossHealth(true, "HAL Core", renderHealth, maxHealth);
                    }
                }
            }
        }

        pos.set(shell.pos);
        offset = shell.offset;
        pos.z -= zOffset;
        vel.set(shell.vel);

        //rot.y += delta +delta*hitSpin;
        rot.x += delta*2 -delta*hitSpin*2;
        //rot.z += delta*hitSpin*3;

        hitCooldown -= delta;
        healthDisplayCooldown -= delta;
        if (hitSpin > 0)
            hitSpin -= delta*3;
        else
            hitSpin = 0;
    }
    public void addBullets(double delta) {
        if (bulletCooldown <= 0) {
            bulletCooldown = .1;
            double clock = Window.time();

            float rX = .08f * (float)Math.sin(clock);
            float rY = .08f * (float)Math.cos(clock);
            Level.getInstance().addSprite(
                    new BossBullet(new Vector3f(pos), new Vector3f(rX, rY, vel.z + .35f), new Vector3f(-.85f, -.85f, 0)));
            Level.getInstance().addSprite(
                    new BossBullet(new Vector3f(pos), new Vector3f(-rX, -rY, vel.z + .35f), new Vector3f(-.85f, -.85f, 0)));
            Level.getInstance().addSprite(
                    new BossBullet(new Vector3f(pos), new Vector3f(-rX, rY, vel.z + .35f), new Vector3f(-.95f, -.95f, 0)));
            Level.getInstance().addSprite(
                    new BossBullet(new Vector3f(pos), new Vector3f(rX, -rY, vel.z + .35f), new Vector3f(-.95f, -.95f, 0)));

        } else
            bulletCooldown -= delta;
    }
    public void addWalls(double delta) {
        wallCooldown -= delta;
        if (wallCooldown <= 0) {
            wallCooldown = .65+((double)health/maxHealth);
            Level.getInstance().addEntity(new BreakableWall(pos.x, pos.y, pos.z-1));
        }
    }

    @Override
    public void render(double delta, TransformationMatrix model) {
        TextureMap.bind("hal");
        Uniform.vec("textureCoef",1f);
        super.render(delta, model);
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
        hitSpin += 1;
        health--;
        health = Math.max(0,health);
        GameUI.showBossHealth(true, "HAL Core", health, maxHealth);
    }
    private void addExplosion() {
        AudioPlayback.playSfx("explode");
        Vector3f newPos = new Vector3f(pos);
        newPos.add(offset);
        Vector3f newVel = new Vector3f(vel);
        float x = (float)Math.random()-.5f;
        float y = (float)Math.random()-.5f;
        y *= 3;
        newPos.add(x,y,5);
        newVel.add(x*.2f,y*.2f,.05f);
        Level.getInstance().addSprite(new AnimSprite(
                TextureMap.get("explosion"),
                new Vector3f(newPos),
                new Vector3f(newVel),
                new Vector2f(2f),
                new Vector3i(5, 5, 25))
        );
    }
}
