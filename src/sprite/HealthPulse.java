package sprite;

import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.MeshMap;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class HealthPulse extends Sprite {
    double clock;
    public HealthPulse(Vector3f p) {
        super(TextureMap.get("healthpulse"), p, new Vector3f(0,0,-0.65f), new Vector2f(1,1));
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        scale.mul(1.02f);
        vel.z *= .93;
    }

    @Override
    public void render(double delta, TransformationMatrix mat) {
        mat.setIdentity();
        clock += delta*4;
        mat.rotate((float)clock,0,0,1);
        mat.translate(pos.x,pos.y,pos.z);
        mat.scale(scale.x,scale.y,1);
        mat.bind();
        //Uniform.vec("color", color);
        Uniform.vec("spriteInfo", (float)spriteInfo.x, spriteInfo.y, spriteInfo.z);
        texture.bind();
        MeshMap.render("quad");
    }
}
