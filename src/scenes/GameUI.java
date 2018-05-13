package scenes;

import main.Input;
import main.Level;
import mote4.scenegraph.Scene;
import mote4.util.matrix.Transform;
import mote4.util.shader.ShaderMap;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.FontUtils;
import mote4.util.vertex.mesh.Mesh;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class GameUI implements Scene {

    private static int width;

    private static Mesh bossHealth;
    private static boolean showBossHealth = false, showPauseText = false, showWinText = false;
    public static void showBossHealth(boolean show, String name, int health, int max) {
        showBossHealth = show;
        if (bossHealth != null)
            bossHealth.destroy();
        if (show) {
            String bossString = name+"\n[" + hearts(health, max) + "]";
            float w = FontUtils.getStringWidth(bossString);
            int hw = (int)(w*12)/2;
            bossHealth = FontUtils.createString(bossString, width/2-hw, 180, 12, 18);
        }
    }
    public static void pause(boolean pause) {
        showPauseText = pause;
    }
    public static void showWinText() {
        showWinText = true;
    }
    public static boolean isShowingWinText() { return showWinText; }

    private Mesh text, keyboardHelpText, gamepadHelpText, pauseText, winText;
    private Transform trans;
    private int lastHealth, lastScore;
    private double helpTextTimeout;
    private boolean showHelpText = false;

    public GameUI() {
        trans = new Transform();

        winText = FontUtils.createString("MISSION COMPLETE",90,40,12,18);
        keyboardHelpText = FontUtils.createString("Space: Shoot\nArrow Keys: Steer\nGood Luck!",35,80,12,18);
        gamepadHelpText = FontUtils.createString("A: Shoot\nD-pad: Steer\nGood Luck!",50,80,12,18);
        lastHealth = lastScore = -1;
        helpTextTimeout = 4;

        showBossHealth = false;
    }

    private static String hearts(int num, int max) {
        // this is messy as hell, but it works
        num = Math.max(0,num);
        int halfNum = (int)Math.ceil(num/2f);
        max = Math.max(0,(int)Math.ceil(max/2f));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < halfNum-1; i++) {
            sb.append((char)0);
        }
        if (num/2f != halfNum)
            sb.append((char)1);
        else if (num > 0)
            sb.append((char)0);
        for (int i = halfNum; i < max; i++) {
            sb.append((char)2);
        }
        return sb.toString();
    }

    @Override
    public void update(double time, double delta) {
        int health = Level.getInstance().getPlayerHealth();
        int maxHealth = Level.getInstance().getMaxPlayerHealth();
        int score = Level.getInstance().getPlayerScore();
        if (health != lastHealth || score != lastScore)
        {
            String hearts = hearts(health, maxHealth);
            if (text != null)
                text.destroy();
            text = FontUtils.createString("HEALTH: ["+hearts+"]\nSCORE: "+score,0,0,12,18);
            lastHealth = health;
            lastScore = score;
        }
        helpTextTimeout -= delta;

        showHelpText = (helpTextTimeout > 0 && (helpTextTimeout>1 || (int)(helpTextTimeout*10%2)==0));
    }

    @Override
    public void render(double time, double delta) {
        glDisable(GL_DEPTH_TEST);

        ShaderMap.use("texture");
        trans.model.setIdentity();
        trans.model.translate(7,3);
        trans.bind();
        TextureMap.bind("font_1");
        Uniform.vec("color",0,0,0,1);
        text.render();
        if (showHelpText) {
            if (Input.showGamepad())
                gamepadHelpText.render();
            else
                keyboardHelpText.render();
        }
        if (showBossHealth)
            bossHealth.render();
        if (showPauseText && time%1<.8)
            pauseText.render();
        if (showWinText)
            winText.render();
        trans.model.translate(-1,-1);
        trans.model.bind();
        Uniform.vec("color",1,1,1,1);
        text.render();
        if (showHelpText) {
            if (Input.showGamepad())
                gamepadHelpText.render();
            else
                keyboardHelpText.render();
        }
        if (showBossHealth)
            bossHealth.render();
        if (showPauseText && time%1<.8)
            pauseText.render();
        if (showWinText)
            winText.render();
    }

    @Override
    public void framebufferResized(int width, int height) {
        trans.projection.setOrthographic(0,0,width,height,-1,1);
        if (pauseText != null)
            pauseText.destroy();
        this.width = width;
        pauseText = FontUtils.createString("[PAUSED]",width/2-30,85,12,18);
    }

    @Override
    public void destroy() {
        text.destroy();
        keyboardHelpText.destroy();
        gamepadHelpText.destroy();
        pauseText.destroy();
        winText.destroy();
    }
}
