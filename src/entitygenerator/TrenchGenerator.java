package entitygenerator;

import entity.Entity;
import entity.Wall;
import org.joml.Vector3f;

import java.util.List;

public class TrenchGenerator extends EntityGenerator {

    public TrenchGenerator(int start, int end) {
        super(start, end);
    }

    @Override
    public void generate(List<Entity> entities) {
        entities.add(new Wall(new Vector3f(0, -30f, -start * .4f), new Vector3f(100, 25, 0.0f),.35f));

        entities.add(new Wall(new Vector3f(36, 0f, -start * .4f), new Vector3f(30, 7, 0.0f),.35f));
        entities.add(new Wall(new Vector3f(-36, 0f, -start * .4f), new Vector3f(30, 7, 0.0f),.35f));

        for (int step = start; step < end; step += 5) {
            for (int i = -1; i <= 1; i+=2)
            {
                float randX = (float)(Math.random()*2)-1;
                float randY = (float)(Math.random()*.5)-.25f;
                float randZ = (float)(Math.random())-.5f;
                float randW = (float)(Math.random()*6)-3;
                float randColor = (float)(Math.random()*.02f);
                // floor
                entities.add(new Wall(new Vector3f(randX+i, randY-5.5f, -step * .4f+randZ), new Vector3f(3.5f+randW, 0.25f, 1.0f),.3f+randColor));
                // left wall
                entities.add(new Wall(new Vector3f(randY*2-5.5f, randX*3+i, -step * .4f+randZ), new Vector3f(0.25f, 3.5f+randW, 1.0f),.3f+randColor));
                // right wall
                entities.add(new Wall(new Vector3f(randY*2+5.5f, randX*3+i, -step * .4f+randZ), new Vector3f(0.25f, 3.5f+randW, 1.0f),.3f+randColor));
            }
            entities.add(new Wall(new Vector3f(0, -7.5f, -step * .4f), new Vector3f(20, 0.10f, 1.0f),.35f));
            entities.add(new Wall(new Vector3f(-7.5f, 0, -step * .4f), new Vector3f(0.10f, 6, 1.0f),.35f));
            entities.add(new Wall(new Vector3f(7.5f, 0, -step * .4f), new Vector3f(0.10f, 6, 1.0f),.35f));
        }
    }
}
