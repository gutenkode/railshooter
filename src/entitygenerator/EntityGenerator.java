package entitygenerator;

import entity.Entity;

import java.util.List;

/**
 *
 * @author Peter
 */
public abstract class EntityGenerator {
    
    public final int start, end;
    
    public EntityGenerator(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    public abstract void generate(List<Entity> entities);
}