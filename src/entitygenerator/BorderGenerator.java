package entitygenerator;


import entity.Entity;
import entity.Wall;
import org.joml.Vector3f;

import java.util.List;

/**
 *
 * @author Peter
 */
public class BorderGenerator extends EntityGenerator {

    public BorderGenerator(int start, int end) {
        super(start, end);
    }

    @Override
    public void generate(List<Entity> entities) {
        for (int step = start; step < end; step += 15) {

            entities.add(new Wall(new Vector3f( 4.7f,0,-step*.4f), new Vector3f(.1f,1.4f,2)));
            entities.add(new Wall(new Vector3f(-4.7f,0,-step*.4f), new Vector3f(.1f,1.4f,2)));
                        
            entities.add(new Wall(new Vector3f(0, 4.7f,-step*.4f), new Vector3f(1.4f,.1f,2)));
            entities.add(new Wall(new Vector3f(0,-4.7f,-step*.4f), new Vector3f(1.4f,.1f,2)));
        }
    }
}