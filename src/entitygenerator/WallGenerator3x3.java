package entitygenerator;

import entity.*;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Peter
 */
public class WallGenerator3x3 extends EntityGenerator {
    
    private int holeGen;
    private String enemyType = "none";
    private boolean lattice;

    public WallGenerator3x3(int start, int end, int holes, String enemy, boolean l) {
        super(start, end);
        holeGen = holes;
        enemyType = enemy;
        lattice = l;
    }

    public boolean[] shuffle(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int randomPos = ThreadLocalRandom.current().nextInt(0, arr.length);
            boolean temp = arr[i];
            arr[i] = arr[randomPos];
            arr[randomPos] = temp;
        }
        return arr;
    }
    
    @Override
    public void generate(List<Entity> entities) {
        for (int step = start; step < end; step += 150) {

            boolean[] mask = new boolean[9];
            if (holeGen > 0) {
                int numHoles = ThreadLocalRandom.current().nextInt(Math.max(1, holeGen / 2), holeGen);
                numHoles = Math.min(mask.length, numHoles);
                for (int i = 0; i < numHoles; i++)
                    mask[i] = true;
                shuffle(mask);
            }
            
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (!mask[(i+1) + (j+1)*3])
                        entities.add(new Wall(new Vector3f(3*i,3*j,-step*.4f), new Vector3f(1.4f,1.4f,1)));
                    else
                        switch (enemyType) {
                            case "laser":
                                entities.add(new Fighter2(3*i,3*j,-step*.4f+10));
                                break;
                            case "missile":
                                entities.add(new Missile(3*i,3*j,-step*.4f+10));
                                break;
                            case "missile+wall":
                                entities.add(new Missile(3*i,3*j,-step*.4f+10));
                            case "wall":
                                entities.add(new BreakableWall(3*i,3*j,-step*.4f));
                                break;
                        }
                }
                if (lattice) {
                    entities.add(new Wall(new Vector3f(0,3*i, -step*.4f), new Vector3f(3.3f,.3f,.3f)));
                    entities.add(new Wall(new Vector3f(3*i,0, -step*.4f), new Vector3f(.3f,3.3f,.3f)));
                }
            }
        }
    }
}