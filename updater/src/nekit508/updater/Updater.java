package nekit508.updater;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonValue;
import nekit508.updater.log.Logger;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Updater {
    public static JsonReader jsonReader = new JsonReader();
    public static Logger logger = new Logger();
    public static Globals globals;
    public static InternalFileTree internalFileTree = new InternalFileTree(Updater.class);
    public static Fi root = new Fi("");
    public static GitHub gitHub;

    public static Seq<LuaValue> forRun = new Seq<>();
    public static Seq<String> loaded = new Seq<>();

    public static void main(String[] args) {
        Log.logger = logger;
        try {
            gitHub = System.getenv().containsKey("GITHUB") ? GitHub.connect() : GitHub.connectAnonymously();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        globals = JsePlatform.standardGlobals();

        Fi G = internalFileTree.child("G.lua");
        globals.load(G.reader(), "G");

        loaded = Seq.with("nekit508/mindustry-mod-build-script/any-remote/core");
        forRun = loaded.map(dep -> {
            String[] path = dep.split("[\\\\/]", 3);

            try {
                Log.info("Loading @.", dep);

                GHRepository repo = gitHub.getRepository(path[0] + "/" + path[1]);
                GHContent info = repo.getFileContent(path[2] + "/info.json");
                JsonValue infoJ = jsonReader.parse(info.read());

                String name = infoJ.get("name").asString();
                if (infoJ.has("dependencies")) {
                    String[] deps = infoJ.get("dependencies").asStringArray();
                    loaded.addAll(deps);
                }
                String main = infoJ.get("main").asString();
                GHContent mainF = repo.getFileContent(path[2] + "/" + main);

                return globals.load(new InputStreamReader(mainF.read()), name);
            } catch (Exception e) {
                Log.err("Error due loading @.", dep);
                return globals.load(Strings.format("print(\"Err @\")", dep), "err");
            }
        }).reverse();

        Log.info("Waiting for commands");
        try {
            boolean running = true;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (running) {
                System.out.print(">>> ");
                String[] command = reader.readLine().split("[\t ]");
                if (command.length == 0)
                    continue;
                if (command[0].equals("exit"))
                    running = false;
                else if (command[0].equals("chmod")) {
                    if (command.length > 1) {
                        String path = command[1];
                        if (path.startsWith("/"))
                            root = new Fi(path);
                        else if (path.equals("$"))
                            root = root.parent();
                        else
                            root = root.child(path);
                    }
                    Log.info("Root now is @.", root.absolutePath());
                } else if (command[0].equals("start")) {
                    forRun.each(LuaValue::call);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
