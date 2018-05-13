package entitygenerator;

import entity.Fighter;
import entity.Entity;
import entity.Fighter2;

import java.util.List;

/**
 *
 * @author Peter
 */
public class FighterGenerator extends EntityGenerator {

    public FighterGenerator(int start, int end) {
        super(start, end);
    }

    @Override
    public void generate(List<Entity> entity) {
        for (int step = start; step < end; step += 100) {
            entity.add(new Fighter(-3,-2, -step*.4f-15));
            if (Math.random() > .8)
                entity.add(new Fighter2(0, 0, -step*.4f-10));
            else
                entity.add(new Fighter(0, 0, -step*.4f-10));
            entity.add(new Fighter(3, 2, -step*.4f-20));
        }
    }
}