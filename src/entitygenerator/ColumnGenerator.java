package entitygenerator;

import entity.Entity;
import entity.Wall;
import org.joml.Vector3f;

import java.util.List;

/**
 *
 * @author Peter
 */
public class ColumnGenerator extends EntityGenerator {

    private boolean both;

    public ColumnGenerator(int start, int end, boolean both) {
        super(start, end);
        this.both = both;
    }

    @Override
    public void generate(List<Entity> entities) {
        for (int step = start; step < end; step += 50) {
            if (both) {
                entities.add(createHoriz(step));
                entities.add(createVert(step));
            } else {
                if (Math.random() > .5)
                    entities.add(createHoriz(step));
                else
                    entities.add(createVert(step));
            }
        }
    }
    public Entity createHoriz(int step) {
        float i = (float)(Math.random()*7-3.5f);
        return new Wall(new Vector3f(i,0,-step*.4f), new Vector3f(.5f,4,.5f), .65f);
    }
    public Entity createVert(int step) {
        float i = (float)(Math.random()*7-3.5f);
        return new Wall(new Vector3f(0,i,-step*.4f), new Vector3f(4,.5f,.5f), .65f);
    }
}