package entitygenerator;

import entity.*;

import java.util.List;

public class Boss2Generator extends EntityGenerator {

    public Boss2Generator() {
        super(0,0);
    }

    @Override
    public void generate(List<Entity> entity) {
        Boss2 b = new Boss2();
        entity.add(b);
        entity.add(new Boss2Core(b));

        RailHarness h1 = new RailHarness(-4.7f,-3);
        RailHarness h2 = new RailHarness(-4.7f,3);
        RailHarness h3 = new RailHarness(4.7f,-3);
        RailHarness h4 = new RailHarness(4.7f,3);
        entity.add(h1);
        entity.add(h2);
        entity.add(h3);
        entity.add(h4);
        entity.add(new Shield(h1,3));
        entity.add(new Shield(h2,2));
        entity.add(new Shield(h3,0));
        entity.add(new Shield(h4,1));
    }
}
