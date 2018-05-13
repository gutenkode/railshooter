package entitygenerator;

import entity.Entity;
import entity.Wall;
import org.joml.Vector3f;

import java.util.List;

/**
 *
 * @author Peter
 */
public class SolidBorderGenerator extends EntityGenerator {

    public SolidBorderGenerator(int start, int end) {
        super(start, end);
    }

    @Override
    public void generate(List<Entity> entities) {
        for (int step = start; step < end; step += 15) {
            for (int i = -1; i <= 1; i++) {
                entities.add(new Wall(new Vector3f( 4.7f,i*3.3f,-step*.4f), new Vector3f(.1f,1.0f,3)));
                entities.add(new Wall(new Vector3f(-4.7f,i*3.3f,-step*.4f), new Vector3f(.1f,1.0f,3)));

                entities.add(new Wall(new Vector3f(i*3.3f, 4.7f,-step*.4f), new Vector3f(1.0f,.1f,3)));
                entities.add(new Wall(new Vector3f(i*3.3f,-4.7f,-step*.4f), new Vector3f(1.0f,.1f,3)));
            }
        }
    }
}