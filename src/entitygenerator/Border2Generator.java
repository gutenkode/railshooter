package entitygenerator;


import entity.Entity;
import entity.Wall;
import org.joml.Vector3f;

import java.util.List;

/**
 *
 * @author Peter
 */
public class Border2Generator extends EntityGenerator {

    public Border2Generator(int start, int end) {
        super(start, end);
    }

    @Override
    public void generate(List<Entity> entities) {
        for (int step = start; step < end; step += 45) {

            entities.add(new Wall(new Vector3f( 4.7f,3,-step*.4f), new Vector3f(.1f,1f,6)));
            entities.add(new Wall(new Vector3f(-4.7f,3,-step*.4f), new Vector3f(.1f,1f,6)));

            entities.add(new Wall(new Vector3f( 4.7f,-3,-step*.4f), new Vector3f(.1f,1f,6)));
            entities.add(new Wall(new Vector3f(-4.7f,-3,-step*.4f), new Vector3f(.1f,1f,6)));

            //entities.add(new Wall(new Vector3f(0, 4.7f,-step*.4f), new Vector3f(.3f,.1f,10)));
            //entities.add(new Wall(new Vector3f(0,-4.7f,-step*.4f), new Vector3f(.3f,.1f,10)));
        }
    }
}