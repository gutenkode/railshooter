package main;

import entitygenerator.*;
import entity.*;
import mote4.util.audio.AudioPlayback;
import mote4.util.matrix.Transform;
import mote4.util.shader.ShaderMap;
import mote4.util.shader.Uniform;
import scenes.Ingame;
import scenes.Post;
import sprite.Sprite;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Peter on 1/15/17.
 */
public class Level {

    private static final int PARTITION_SIZE = 100;
    private static final List<Entity> emptyList = new ArrayList<>();
    private static Level level;

    private Player player;
    private volatile Map<Integer, List<Entity>> entityStore;
    private List<List<Entity>> entities;
    private List<Entity> eAdd, eRemove;
    private List<Sprite> sprites, sAdd, sRemove;
    private boolean renderHitboxes = false;
    private List<EntityGenerator> generators;

    private int playerHealth, maxPlayerHealth, playerScore;
    private int lastPlayerIndex;
    private float mapUpateCooldown;

    public static Level getNewInstance(int levelNum) {
        if (level != null)
            level.destroy();
        level = new Level(levelNum);
        return level;
    }
    public static Level getInstance() {
        if (level == null)
            throw new IllegalStateException();//level = new Level();
        return level;
    }

    private Level(int levelNum) {
        player = new Player();
        playerHealth = maxPlayerHealth = 6;
        playerScore = 0;

        lastPlayerIndex = 999;

        entityStore = new HashMap<>();
        entities = new ArrayList<>();
        entities.add(new ArrayList<>());
        eAdd = new ArrayList<>();
        eRemove = new ArrayList<>();
        sprites = new ArrayList<>();
        sAdd = new ArrayList<>();
        sRemove = new ArrayList<>();

        entities.get(0).add(player);

        List<Entity> generatedEntities = new ArrayList<>();
        ShaderMap.use("object");
        switch (levelNum) {
            case 0:
                generators = createBossLevel();
                Uniform.vec("light",0,1,0);
                break;
            case 1:
                generators = createlevel1();
                Uniform.vec("light",0,1,0);
                break;
            case 2:
                generators = createlevel2();
                Uniform.vec("light",0,-1,0);
                break;
            default:
                throw new IllegalArgumentException();
        }
        for (EntityGenerator g : generators)
            g.generate(generatedEntities);
        for (Entity e : generatedEntities) {
            int index = (int)(e.pos().z/PARTITION_SIZE);
            entityStore.putIfAbsent(index, new ArrayList<>());
            entityStore.get(index).add(e);
        }
    }

    private List<EntityGenerator> createBossLevel() {
        Ingame.setBackgroundTex("lv1_background");
        List<EntityGenerator> generators = new ArrayList<>();

        generators.add(new TrenchGenerator(200,10000));
        generators.add(new BossGenerator(300));

        return generators;
    }

    private List<EntityGenerator> createTestLevel() {
        Ingame.setBackgroundTex("lv1_background");
        List<EntityGenerator> generators = new ArrayList<>();
        generators.add(new TrenchGenerator(0,5600));

        generators.add(new WallGenerator3x3(200,650, 7, "wall", false));
        generators.add(new BorderGenerator(0,700));

        generators.add(new FighterGenerator(800, 1600));
        generators.add(new ColumnGenerator(800,1600, false));
        generators.add(new BorderGenerator(800,1700));

        generators.add(new WallGenerator3x3(1850,2550, 5, "laser",false));
        generators.add(new SolidBorderGenerator(1800,2550));

        generators.add(new SineTunnelGenerator(2650, 3200));

        generators.add(new WallGenerator3x3(3350,4400, 3, "missile",false));
        generators.add(new WallGenerator3x3(4400,5400, 2, "wall",false));
        generators.add(new BorderGenerator(3300,5420));

        generators.add(new BossGenerator(5600));

        return generators;
    }
    private List<EntityGenerator> createlevel1() {
        Ingame.setBackgroundTex("lv1_background");
        List<EntityGenerator> generators = new ArrayList<>();
        //generators.add(new TrenchGenerator(0,5900));

        generators.add(new WallGenerator3x3(300,1000, 8, "none", false));
        generators.add(new WallGenerator3x3(1000,1600, 7, "wall", false));
        generators.add(new BorderGenerator(250,1500));

        entities.get(0).add(new HealthPickup(0,0,-1550*.4f));

        generators.add(new FighterGenerator(1600,3000));
        generators.add(new ColumnGenerator(1600,2300, false));
        generators.add(new ColumnGenerator(2300,3000, true));
        generators.add(new BorderGenerator(1600,3000));

        entities.get(0).add(new HealthPickup(0,0,-3050*.4f));

        generators.add(new SineTunnelGenerator(3100, 4000));

        //entities.get(0).add(new HealthPickup(0,0,-4100*.4f));

        generators.add(new WallGenerator3x3(4200,4700, 4, "missile",false));
        generators.add(new WallGenerator3x3(4850,5700, 4, "missile+wall",false));
        generators.add(new BorderGenerator(4100,5700));

        //entities.get(0).add(new HealthPickup(0,0,-5800*.4f));
        entities.get(0).add(new HealthPickup(0,0,-5900*.4f));

        generators.add(new BossGenerator(6100));
        generators.add(new TrenchGenerator(6000,20000));

        return generators;
    }
    private List<EntityGenerator> createlevel2() {
        Ingame.setBackgroundTex("lv2_background");
        List<EntityGenerator> generators = new ArrayList<>();

        entities.get(0).add(new HealthPickup(0,0,-650*.4f));
        generators.add(new WallGenerator3x3(450,1100, 999, "wall", false));
        generators.add(new Border2Generator(0,30000));
        generators.add(new Boss2Generator());

        return generators;
    }

    public void update(double delta) {
        entities.get(0).addAll(eAdd);
        eAdd.clear();
        sprites.addAll(sAdd);
        sAdd.clear();

        for (List<Entity> el : entities)
        for (Entity e : el)
            if (isInRange(e))
                e.update(delta);

        for (Sprite s : sprites)
            if (isInRange(s))
                s.update(delta);

        for (List<Entity> el : entities)
        for (Entity e : el) {
            if (isInRange(e)) {
                for (Sprite s : sprites) {
                    if (isInRange(s))
                        if (e.collidesWith(s)) {
                            s.onCollide(e);
                            e.onCollide(s);
                        }
                }

                if (e.collidesWith(player)) {
                    player.onCollide(e);
                    e.onCollide(player);
                }
                /*
                for (Entity e2 : entities) {
                    if (isInRange(e2))
                        if (e.collidesWith(e2)) { // self-collision handled in canCollide()
                            e2.onCollide(e);
                            e.onCollide(e2);
                        }
                }*/
            }
        }

        for (List<Entity> el : entities)
            el.removeAll(eRemove);
        eRemove.clear();
        sprites.removeAll(sRemove);
        sRemove.clear();

        if (Input.isKeyNew(Input.Key.ENTER))
            renderHitboxes = !renderHitboxes;

        if (mapUpateCooldown <= 0) {
            // add entities from the generated list
            int index = (int) (player.pos().z / PARTITION_SIZE);
            if (lastPlayerIndex != index) {
                for (List<Entity> el : entities)
                    for (Entity e : el) {
                        if (e.pos().z > player.pos().z + 30)
                            eRemove.add(e);
                    }
                for (Sprite s : sprites) {
                    if (s.pos().z > player.pos().z + 30)
                        sRemove.add(s);
                }
                for (int i = -1; i <= 1; i++) {
                    entities.add(entityStore.getOrDefault(index + i, emptyList));
                    entityStore.remove(index + i);
                }
            }
            lastPlayerIndex = index;
            mapUpateCooldown = .5f;
        } else
            mapUpateCooldown -= delta;

    }
    public void render(double delta, Transform trans) {
        ShaderMap.use("object");
        //Uniform.varFloat("geomDistortCoef",1);
        trans.bind();
        //player.render(delta, trans.model);
        for (List<Entity> el : entities)
        for (Entity e : el)
            if (isInRange(e))
                e.render(delta, trans.model);

        ShaderMap.use("sprite");
        trans.bind();
        for (Sprite s : sprites)
            if (isInRange(s))
                s.render(delta, trans.model);

        if (renderHitboxes) {
            ShaderMap.use("color");
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            trans.bind();
            player.renderHitbox(trans.model);
            for (List<Entity> el : entities)
            for (Entity e : el)
                e.renderHitbox(trans.model);
            for (Sprite s : sprites)
                s.renderHitbox(trans.model);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
    }

    private boolean isInRange(Entity e) {
        return (e.pos().z < player.pos().z+10) && (e.pos().z > player.pos().z-100);
    }
    private boolean isInRange(Sprite s) {
        return (s.pos().z < player.pos().z+10) && (s.pos().z > player.pos().z-100);
    }

    public Player player() { return player; }

    public Entity collidesWith(Entity test) {
        for (List<Entity> el : entities)
        for (Entity e : el) {
            if (isInRange(e) && e.isSolid()) {
                if (test.collidesWith(e)) {
                    return e;
                }
            }
        }
        return null;
    }

    public void addSprite(Sprite s) {
        sAdd.add(s);
    }
    public void removeSprite(Sprite s) { sRemove.add(s); }
    public void addEntity(Entity e) {
        eAdd.add(e);
    }
    public void removeEntity(Entity e) { eRemove.add(e); }

    public void damagePlayer(int d) {
        playerHealth -= d;
        Post.getInstance().distort();
        if (playerHealth <= 0) {
            playerHealth = 0;
            AudioPlayback.stopMusic();
        }
    }
    public void healPlayer(int h) {
        playerHealth += h;
        playerHealth = Math.min(maxPlayerHealth, playerHealth);
    }
    public void addScore(int s) { playerScore += s; }
    public int getPlayerHealth() { return playerHealth; }
    public int getMaxPlayerHealth() { return maxPlayerHealth; }
    public int getPlayerScore() { return playerScore; }

    public void destroy() {
        // whatever
    }
}
