package nekit508.updater;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
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

    public static void main(String[] args) {
        Log.logger = logger;

        try {
            gitHub = GitHub.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        globals = JsePlatform.standardGlobals();

        Fi G = internalFileTree.child("G.lua");
        globals.load(G.reader(), "G");

        Seq<String> forLoad = Seq.with("nekit508/mindustry-mod-build-script/any-remote/any-ext");
        Seq<LuaValue> forRun = forLoad.map(dep -> {
            String[] path = dep.split("[\\\\/]", 3);

            try {
                GHRepository repo = gitHub.getRepository(path[0] + "/" + path[1]);
                GHContent info = repo.getFileContent(path[2] + "/info.json");
                JsonValue infoJ = jsonReader.parse(info.read());

                String name = infoJ.get("name").asString();
                String[] deps = infoJ.get("dependencies").asStringArray();
                forLoad.addAll(deps);
                String main = infoJ.get("main-file").asString();
                GHContent mainF = repo.getFileContent(path[2] + "/" + main);

                return globals.load(new InputStreamReader(mainF.read()), name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).reverse();
        forRun.each(run -> globals.call(run));

        Log.info("Waiting for commands");
        try {
            boolean running = true;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (running) {
                Log.info(">>> ");
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
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
