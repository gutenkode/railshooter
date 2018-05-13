package scenes;

import main.CommandProcessor;
import main.Input;
import mote4.scenegraph.Scene;
import mote4.util.matrix.Transform;
import mote4.util.shader.ShaderMap;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.FontUtils;
import mote4.util.vertex.mesh.Mesh;
import mote4.util.vertex.mesh.ScrollingText;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Terminal implements Scene {

    // characters are 9x14
    private int maxLines = 16;
    private Transform trans;
    private List<ScrollingText> text;
    private StringBuilder builder;
    private Mesh input;

    public Terminal() {
        trans = new Transform();
        text = new ArrayList<>();
        builder = new StringBuilder();
        input = FontUtils.createString("", 0,0, 1,1);
        Input.recordTyped(true);

        String[] response = CommandProcessor.reset();
        for (String s : response)
            text.add(new ScrollingText(s,"font_term",0,0,9,14, 60));


    }

    @Override
    public void update(double time, double delta) {
        if (Input.isKeyNew(Input.Key.BACKSPACE) && builder.length() > 0) {
            builder.deleteCharAt(builder.length()-1);
        } else {
            String typed = Input.getTyped();
            builder.append(typed);
        }
        input.destroy();
        if (Input.isKeyNew(Input.Key.ENTER) && builder.length() > 0) {
            ScrollingText t = new ScrollingText(">"+builder.toString(),"font_term",0,0,9,14, 60);
            t.complete();
            text.add(t);

            String[] response = CommandProcessor.process(builder.toString());
            for (String s : response)
                text.add(new ScrollingText(s,"font_term",0,0,9,14, 60));

            builder = new StringBuilder();
        }
        if (time%0.5<.15)
            input = FontUtils.createString(">"+builder.toString(), 0,0, 9,14);
        else
            input = FontUtils.createString(">"+builder.toString()+(char)219, 0,0, 9,14);
    }

    @Override
    public void render(double time, double delta) {
        glDisable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT);
        ShaderMap.use("texture");
        TextureMap.bind("font_term");
        trans.model.setIdentity();
        trans.model.translate(14,4);
        trans.bind();

        int numLines = 0;
        boolean showInput = true;
        for (ScrollingText t : text) {
            t.render();
            trans.model.translate(0,13,0);
            trans.model.bind();
            if (!t.isDone()) {
                showInput = false;
                break;
            } else
                numLines++;
        }
        if (showInput)
            input.render();
        if (numLines > maxLines) {
            text.get(0).destroy();
            text.remove(0);
        }
    }

    @Override
    public void framebufferResized(int width, int height) {
        trans.projection.setOrthographic(0,0,width,height,-1,1);
    }

    @Override
    public void destroy() {
        for (ScrollingText t : text)
            t.destroy();
    }
}
