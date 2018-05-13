package entitygenerator;

import entity.BreakableWall;
import entity.Fighter;
import entity.Entity;
import entity.Wall;
import org.joml.Vector3f;

import java.util.List;

/**
 *
 * @author Peter
 */
public class SineTunnelGenerator extends EntityGenerator {

    public SineTunnelGenerator(int start, int end) {
        super(start, end);
    }

    @Override
    public void generate(List<Entity> entities) {
        int wallDelay = 0;
        for (int step = start; step < end; step += 15) {
            float offset = (float)Math.sin(step/40f)*3;

            wallDelay -= 10;
            for (int i = -1; i <= 1; i++) {
                entities.add(new Wall(new Vector3f(-3+offset,3*i,-step*.4f), new Vector3f(.85f,1,1.25f)));
                entities.add(new Wall(new Vector3f( 3+offset,3*i,-step*.4f), new Vector3f(.85f,1,1.25f)));

                if (wallDelay <= 0) {
                    entities.add(new BreakableWall(offset*1.2f,3*i,-step*.4f));
                }
                offset *= -1;
            }
            if (wallDelay <= 0)
                wallDelay = 100;
        }
    }
}