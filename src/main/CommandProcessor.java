package main;

import mote4.util.audio.AudioPlayback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandProcessor {

    private static enum State {
        BOOT, GAME, CRASH, TERMINAL;
    }
    private static State state;
    private static final String EXECUTABLE = "space.exe", README = "readme.txt", CREDITS = "credits.txt";


    private static Process proc;
    static {
        try {
            proc =
                    new ProcessBuilder(new String[] {"bash"})//, "-c", command})
                            .redirectErrorStream(true)
                            //.directory(new File(directory))
                            .start();
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public static String[] reset() {
        AudioPlayback.playMusic("computer",true);
        state = State.BOOT;
        return new String[] {
                "                                                                ",
                "...                        ",
                "...                        ",
                "...                        ",
                "...                        ",
                "...                        ",
                "...                        ",
                "SpaceOS v1.3",
                "(c)2118 Weyland Softworks                     ",
                "                                                                     ",
                "Start boot sequence ...            ",
                " ",
                "BootROM check ...                        ",
                "OK. 256KB mounted.",
                "RAM check ...                                 ",
                "OK. 8MB mounted.",
                "Disk check ...                                                                                       ",
                "OK. 2PB mounted.",
                " ",
                "System integrity verified.",
                "Ready.",
        };
    }

    public static String[] process(String command) {
        switch (state) {
            case BOOT:
                return processBoot(command);
            case GAME:
                return processGame(command);
            case CRASH:
                return processCrash(command);
            case TERMINAL:
                return processTerminal(command);
            default:
                return new String[0];
        }
    }
    private static String[] processBoot(String command) {
        command = command.trim().toLowerCase();
        if (command.startsWith("run ")) {
            command = command.substring(4);
            switch (command) {
                case EXECUTABLE:
                    state = State.GAME;
                    return new String[] {
                            "Loading...                 ",
                            "...                        ",
                            "...                        ",
                            "Loaded successfully.",
                            "Type 'BEGIN' to engage the enemy..."
                    };
                case CREDITS:
                case README:
                    return new String[] {
                            "Loading...",
                            "Execute action failed with status: 2",
                            "File is not executable."
                    };
                default:
                    return new String[] {
                            "Execute action failed with status: 1",
                            "File not found: "+command
                    };
            }
        } else if (command.startsWith("read ")) {
            command = command.substring(5);
            switch (command) {
                case EXECUTABLE:
                    return new String[] {
                            getGibberish(0),
                            getGibberish(1),
                            getGibberish(2),
                            getGibberish(3),
                            getGibberish(4),
                            getGibberish(5),
                            getGibberish(6),
                            getGibberish(7),
                    };
                case README:
                    return new String[] {
                            "===== Weyland Softworks Presents =====",
                            "= Thank you for installing our latest",
                            "= in interactive entertainment for",
                            "= your personal workstation.",
                            "= ",
                            "= YOU are the last remaining fighter",
                            "= pilot able to repel the invasion of",
                            "= the evil STROGGOS.  You must DESTROY",
                            "= their commander and flagship, in",
                            "= orbit around our STAR.  Good luck!",
                            "==============    (c)2201    ==============",
                    };
                case CREDITS:
                    return new String[] {
                            "Code/Design:",
                            "   Peter Gutenko",
                            "Music:",
                            "   X Tunnel Theme Remix - VinylCheese",
                            "   Monolith theme - Francis Travis",
                            "   Dusk Boss - Nick Nuwe",
                            "Textures/Models:",
                            "   Star Fox 2, Star Fox 64",
                    };
                default:
                    return new String[] {
                            "File not found: "+command
                    };
            }
        } else
            switch (command) {
                case "help":
                    return new String[] {
                            "Common commands:",
                            "ls : view directory contents",
                            "run [file] : execute a file",
                            "read [file] : display file contents"
                    };
                case "ls":
                    return new String[] {
                            "..",
                            //"/usr",
                            //"/bin",
                            EXECUTABLE,
                            README,
                            CREDITS,
                    };
                default:
                    return new String[] {
                            "Unrecognized command.",
                            "Type 'help' for command list."
                    };
            }
    }
    private static String[] processGame(String command) {
        command = command.trim().toLowerCase();
        switch (command) {
            case "begin":
                RootLayer.getInstance().loadTitleScreen();
                return new String[0];
            default:
                state = State.BOOT;
                return new String[] {
                        "Load aborted."
                };
        }
    }
    private static String[] processCrash(String command) {
        return new String[0];
    }
    private static String[] processTerminal(String command) {
        List<String> strs = new ArrayList<>();
        //command = "ping -c 3 www.google.com";
        try {
            /*
            //Process proc = Runtime.getRuntime().exec(command);
            Process proc =
                    new ProcessBuilder(new String[] {"bash"})//, "-c", command})
                            .redirectErrorStream(true)
                            //.directory(new File(directory))
                            .start();*/

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            writer.write(command);
            writer.newLine();
            writer.flush();
            //writer.close();
            proc.getOutputStream().flush();
            proc.getOutputStream().close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty())
                    strs.add(line); //System.out.print(line + "\n");
            }
            //reader.close();

            //proc.waitFor();
        } catch (IOException /*| InterruptedException*/ e) {
            System.err.println(e.getMessage());
        }
        return strs.toArray(new String[strs.size()]);
    }

    private static String getGibberish(int index) {
        Random r = new Random(index+900);
        r.nextInt(999);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            char c = (char)r.nextInt(16*16);
            b.append(c);
        }
        return b.toString();
    }
}
