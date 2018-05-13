package entitygenerator;

import entity.Boss;
import entity.Entity;

import java.util.List;

public class BossGenerator extends EntityGenerator {

    public BossGenerator(int start) {
        super(start, start);
    }

    @Override
    public void generate(List<Entity> entity) {
        entity.add(new Boss(0,0, -start*.4f));
    }
}
