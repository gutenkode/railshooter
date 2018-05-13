package entity;

import main.Level;
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

public class Boss extends Entity {

    private Vector3f rot;
    private double clock;
    private int health, maxHealth, phase;
    private double shootCooldown, damageCooldown, explosionCooldown, healthGainCooldown, deathDelay;
    private boolean isFirstStep, isActive, isIntro;

    public Boss(float x, float y, float z) {
        super(MeshMap.get("cube"));
        collideEnemyBullet = false;
        pos.set(x,y,z);
        //scale.set(1.5f);
        scale.set(1.5f,.35f,2.5f);
        hitbox.set(1.5f);
        offset.y = 60;

        rot = new Vector3f(0,0,0);

        deathDelay = 3;
        phase = 0;
        isIntro = true;
        health = 0;
        maxHealth = 30;
        shootCooldown = .5;
        setActive(false);
        isFirstStep = true;
    }

    @Override
    public void update(double delta) {
        clock += delta;
        damageCooldown -= delta;
        if (damageCooldown < 0)
            damageCooldown = 0;

        if (isIntro) {
            if (isFirstStep) {
                isFirstStep = false;
                AudioPlayback.playMusic("monolith",true);
            }
            if (offset.y > 0)
                offset.y -= delta*5;
            else {
                offset.y = 0;
                healthGainCooldown -= delta;
                if (healthGainCooldown <= 0) {
                    health+=2;
                    AudioPlayback.playSfx("blip");
                    healthGainCooldown = .1;
                    GameUI.showBossHealth(true, "MONOLITH",health, maxHealth);
                }
                if (health == maxHealth) {
                    isIntro = false;
                    healthGainCooldown = 0;
                    setActive(true);
                }
            }
        }
        if (isActive) {
            shootCooldown -= delta;
            if (health < maxHealth*.4) {
                phase = 2;
            } else if (health < maxHealth*.75) {
                phase = 1;
            }
        } else if (!isIntro && health <= 0) {
            deathDelay -= delta;
            if (deathDelay <= 0) {
                Level.getInstance().addEntity(new WarpGate(pos.x,pos.y,pos.z));
                //GameUI.showWinText();
                Level.getInstance().addScore(BASE_SCORE*10);
                Level.getInstance().removeEntity(this);
                //AudioPlayback.stopMusic();
                AudioPlayback.playSfx("big_explode");
                Level.getInstance().addSprite(new AnimSprite(
                        TextureMap.get("explosion"),
                        new Vector3f(pos),
                        new Vector3f(vel),
                        new Vector2f(5.5f),
                        new Vector3i(5, 5, 25)));
            }
        }

        double hitSpin = 5*Math.pow(damageCooldown,2)*delta;
        rot.y += delta +hitSpin;
        rot.x += delta +hitSpin;
        rot.z += delta/3 +hitSpin;

        pos.x = 2*(float)Math.sin(clock);
        pos.y = 2*(float)Math.cos(clock);
        pos.z = Level.getInstance().player().pos.z-15 + 3*(float)Math.cos(clock/2);
        vel.z = Level.getInstance().player().vel.z;

        float angle,rX,rY;
        if (shootCooldown <= 0) {
            switch (phase)
            {
                case 0: // basic arcing shot
                    shootCooldown = .1;
                    angle = 0;
                    float shotVel = .0015f;
                    float shotZVel = .25f;
                    float shotAtten = .1f;
                    float shotZAtten = -.666f;
                    rX = shotVel * (float) Math.sin(clock*6 +angle);
                    rY = shotVel * (float) Math.cos(clock*6 +angle);
                    Level.getInstance().addSprite(
                            new BossBullet(new Vector3f(pos), new Vector3f( rX,  rY, vel.z+shotZVel), new Vector3f(shotAtten,shotAtten,shotZAtten)));
                    /*
                    angle = (float)Math.PI*2f/3f;
                    rX = shotVel * (float) Math.sin(clock*6 +angle);
                    rY = shotVel * (float) Math.cos(clock*6 +angle);
                    Level.getInstance().addSprite(
                            new BossBullet(new Vector3f(pos), new Vector3f( rX,  rY, vel.z+.2f), new Vector3f(shotAtten,shotAtten,-.5f)));

                    angle = (float)Math.PI*4f/3f;
                    rX = shotVel * (float) Math.sin(clock*6 +angle);
                    rY = shotVel * (float) Math.cos(clock*6 +angle);
                    Level.getInstance().addSprite(
                            new BossBullet(new Vector3f(pos), new Vector3f( rX,  rY, vel.z+.2f), new Vector3f(shotAtten,shotAtten,-.5f)));
                    */
                    break;

                case 2: // the weird curling thing
                    shootCooldown = .03;
                    angle = (float) Math.sin(clock * 5);
                    rX = angle*.1f * (float) Math.sin(clock*4+angle);
                    rY = angle*.1f * (float) Math.cos(clock*4+angle);
                    Level.getInstance().addSprite(
                            new BossBullet(new Vector3f(pos), new Vector3f( rX,  rY, vel.z), new Vector3f(-.999f,-.999f,0.75f)));
                    break;

                case 1: // spiraling strands
                    shootCooldown = .06;
                    angle = (float) Math.sin(clock * 1.5);
                    rX = angle * .08f * (float) Math.sin(clock);
                    rY = angle * .08f * (float) Math.cos(clock);
                    Level.getInstance().addSprite(
                            new BossBullet(new Vector3f(pos), new Vector3f( rX,  rY, vel.z + .1f), new Vector3f(-.5f,-.5f,0)));
                    Level.getInstance().addSprite(
                            new BossBullet(new Vector3f(pos), new Vector3f(-rX, -rY, vel.z + .1f), new Vector3f(-.5f,-.5f,0)));
                    break;

                default:
                    break;
            }
        }

        // create explosions based on remaining health
        if (!isIntro && health < maxHealth/2) {
            explosionCooldown -= delta;
            if (explosionCooldown <= 0) {
                explosionCooldown = .2+health/(maxHealth/2f);
                Vector3f newPos = new Vector3f(pos);
                newPos.x += Math.random() * 1 - .5;
                newPos.y += Math.random() * 1 - .5;
                newPos.z += 1;
                Vector3f newVel = new Vector3f(vel);
                newVel.z += .1;
                float randSize = (float)Math.random()*.4f;
                if (health <= 0) {
                    AudioPlayback.playSfx("explode");
                    randSize += 0.5;
                }
                Level.getInstance().addSprite(new AnimSprite(
                        TextureMap.get("explosion"),
                        newPos,
                        newVel,
                        new Vector2f(1.3f+randSize),
                        new Vector3i(5, 5, 25)));
            }
        }
    }

    @Override
    public void render(double delta, TransformationMatrix model) {
        float c = 1-(float)(damageCooldown);
        color.set(1,c,c,1);
        TextureMap.bind("obelisk");
        Uniform.vec("textureCoef",1f);
        super.rot.x = (int)(this.rot.x*20)/20f;
        super.rot.y = (int)(this.rot.y*20)/20f;
        super.rot.z = (int)(this.rot.z*20)/20f;
        super.render(delta, model);
        Uniform.vec("textureCoef",0f);
    }

    @Override
    public void onCollide(Sprite s) {
        if (!isActive)
            return;
        if (s instanceof PlayerBullet)
            damage(s.getEnemyDamage());
    }

    @Override
    public void damage(double dmg) {
        if (dmg <= 0 || damageCooldown > .25f)
            return;
        damageCooldown = 1;
        health -= 1;
        if (health > 0)
            GameUI.showBossHealth(true, "MONOLITH", health, maxHealth);
        else {
            GameUI.showBossHealth(false, "",0, 0);
            setActive(false);
        }

        AudioPlayback.playSfx("explode");
        Level.getInstance().addSprite(new AnimSprite(
                TextureMap.get("explosion"),
                new Vector3f(pos),
                new Vector3f(vel),
                new Vector2f(2),
                new Vector3i(5, 5, 25))
        );
    }

    private void setActive(boolean active) {
        isActive = active;
        collidePlayerBullet = isActive;
    }
}
