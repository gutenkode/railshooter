package main;

import mote4.scenegraph.Window;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Centralized input management.  All input checks for the game should be done
 * through this class, as it provides and easy-to-edit abstraction.
 * @author Peter
 */
public class Input {
    
    public enum Key { // these are the macOS bindings for a DS4
        YES(0, 1,0),// GLFW_GAMEPAD_BUTTON_A),
        NO(1, 9,1),// GLFW_GAMEPAD_BUTTON_START),
        UP(2, 14,10),// GLFW_GAMEPAD_BUTTON_DPAD_UP),
        DOWN(3, 16,12),// GLFW_GAMEPAD_BUTTON_DPAD_DOWN),
        LEFT(4, 17,13),// GLFW_GAMEPAD_BUTTON_DPAD_LEFT),
        RIGHT(5, 15,11),// GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
        ENTER(6, -1,-1),
        BACKSPACE(7, -1,-1),
        //F5(8, -1,-1),
        ALT(8, -1,-1);
        
        int index, gamepadButton_macOS, gamepadButton_PC;
        Key(int i, int gm, int gp) {
            index = i;
            gamepadButton_macOS = gm;
            gamepadButton_PC = gp;
        }
    }

    private static boolean[] isNew, isNewGamepad, isDown, isDownGamepad;
    private static boolean recordTyped = false, showGamepad = false;
    private static StringBuilder charBuffer;
    private static List<Integer> gamepads = new ArrayList<>();
    
    static {
        isNew  = new boolean[Key.values().length];
        isNewGamepad  = new boolean[Key.values().length];
        isDown = new boolean[Key.values().length];
        isDownGamepad = new boolean[Key.values().length];
        charBuffer = new StringBuilder();
    }
    
    public static boolean isKeyDown(Key k) {
        return isDown[k.index] || isDownGamepad[k.index];
    }
    public static boolean isKeyNew(Key k) {
        boolean b =  isNew[k.index] || isNewGamepad[k.index];
        isNew[k.index] = false;
        isNewGamepad[k.index] = false;
        return b;
    }
    public static void clearKeys() {
        Arrays.fill(isNew, false);
        Arrays.fill(isNewGamepad, false);
        Arrays.fill(isDown, false);
        Arrays.fill(isDownGamepad, false);
    }
    
    /**
     * Return any typed characters since getTyped() was last called.
     * @return 
     */
    public static String getTyped() {
        String s = charBuffer.toString();
        charBuffer = new StringBuilder();
        return s;
    }
    /**
     * Dump any typed characters
     */
    public static void flushTyped() {
    }
    /**
     * Whether to save a buffer of typed characters or not.
     * Disabled by default to prevent recording large amounts of junk data.
     * @param b 
     */
    public static void recordTyped(boolean b) {
        recordTyped = b;
    }
    
    public static void createCharCallback() {
        glfwSetCharCallback(Window.getWindowID(), (long window, int c) -> {
            if (recordTyped)
                charBuffer.append((char)c);
        });
    }

    public static void pollGamepad() {
        //for (int i : gamepads) {
        for (int i = GLFW_JOYSTICK_1; i <= GLFW_JOYSTICK_LAST; i++) {
            if (glfwJoystickPresent(i)) {
                ByteBuffer axes = glfwGetJoystickButtons(i);
                int limit = axes.limit();
                for (int j = 0; j < limit; j++) {
                    byte b = axes.get(j);
                    if (b == GLFW_PRESS)
                        System.out.println("Controller "+i+", key "+j);
                }
                for (Key k : Key.values()) {
                    if (k.gamepadButton_macOS == -1)
                        continue;
                    if (k.gamepadButton_macOS < limit) {
                        int state = axes.get(k.gamepadButton_macOS);
                        if (isKeyDown(k) && state == GLFW_PRESS) {
                            callbackActionGamepad(GLFW_REPEAT, k.index);
                        } else {
                            callbackActionGamepad(state, k.index);
                            if (state == GLFW_PRESS)
                                showGamepad = true;
                        }
                    }
                }
            }
        }
    }

    public static void createGamepadCallback() {
        glfwSetJoystickCallback((int joy, int event) ->
        {
            if (event == GLFW_CONNECTED)
            {
                gamepads.add(joy);
                showGamepad = true;
            }
            else if (event == GLFW_DISCONNECTED)
            {
                gamepads.remove((Integer)joy);
                if (gamepads.isEmpty())
                    showGamepad = false;
            }
        });
    }
    
    /**
     * Override the default key callback created by the engine.
     */
    public static void createKeyCallback() {
        glfwSetKeyCallback(Window.getWindowID(), (long window, int key, int scancode, int action, int mods) ->
        {
            // action is GLFW_PRESS, GLFW_REPEAT, GLFW_RELEASE
            showGamepad = false;
            switch (key)
            {
                case GLFW_KEY_SPACE:
                    callbackAction(action, Key.YES.index);
                    break;
                case GLFW_KEY_ESCAPE:
                    callbackAction(action, Key.NO.index);
                    break;
                case GLFW_KEY_ENTER:
                    callbackAction(action, Key.ENTER.index);
                    break;
                case GLFW_KEY_BACKSPACE:
                    callbackAction(action, Key.BACKSPACE.index);
                    break;
                case GLFW_KEY_LEFT_ALT:
                case GLFW_KEY_RIGHT_ALT:
                    callbackAction(action, Key.ALT.index);
                    break;

                case GLFW_KEY_W:
                case GLFW_KEY_UP:
                    callbackAction(action, Key.UP.index);
                    break;
                case GLFW_KEY_S:
                case GLFW_KEY_DOWN:
                    callbackAction(action, Key.DOWN.index);
                    break;
                case GLFW_KEY_A:
                case GLFW_KEY_LEFT:
                    callbackAction(action, Key.LEFT.index);
                    break;
                case GLFW_KEY_D:
                case GLFW_KEY_RIGHT:
                    callbackAction(action, Key.RIGHT.index);
                    break;
            }
        });
    }
    private static void callbackAction(int action, int i) {
        isNew[i] = (action == GLFW_PRESS);
        isDown[i] = (action != GLFW_RELEASE);
    }
    private static void callbackActionGamepad(int action, int i) {
        isNewGamepad[i] = (action == GLFW_PRESS);
        isDownGamepad[i] = (action != GLFW_RELEASE);
    }

    public static boolean showGamepad() { return showGamepad; }
}
