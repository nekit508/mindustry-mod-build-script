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
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import javax.tools.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class Updater {
    public static JsonReader jsonReader = new JsonReader();
    public static Logger logger = new Logger();
    public static Globals globals;
    public static InternalFileTree internalFileTree = new InternalFileTree(Updater.class);
    public static Fi root = new Fi("");
    public static GitHub gitHub;

    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        Log.logger = logger;
        try {
            gitHub = System.getenv().containsKey("GITHUB") ? GitHub.connect() : GitHub.connectAnonymously();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Seq<Runnable> forRun = load(Seq.with("nekit508/mindustry-mod-build-script/any-remote/core"));

        Log.info("Waiting for commands");
        try {
            boolean running = true;
            while (running) {
                System.out.print(">>> ");
                String[] command = reader.readLine().split("[\t ]");
                if (command.length == 0)
                    continue;
                if (command[0].equals("exit"))
                    running = false;
                else if (command[0].equals("chroot")) {
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
                    forRun.each(Runnable::run);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Seq<Runnable> load(Seq<String> forLoad) {
        try {
            Seq<Runnable> out = new Seq<>();

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

            for (String dep : forLoad) {
                Log.info("Loading @.", dep);
                // user/repo/path
                String[] path = dep.split("[\\\\/]", 3);
                GHRepository repo = gitHub.getRepository(path[0] + "/" + path[1]);

                GHContent infoContent = repo.getFileContent(path[2] + "/info.json");
                JsonValue info = jsonReader.parse(infoContent.read());
                String[] deps = info.get("dependencies").asStringArray();
                for (String s : deps) {
                    forLoad.add(s);
                }

                String mainFileName = info.get("main").asString();
                NetJavaFileObject javaFileObject = new NetJavaFileObject();
                javaFileObject.file = mainFileName;
                javaFileObject.path = path[2];
                javaFileObject.provider = repo;
                Seq<JavaFileObject> forCompile = Seq.with(javaFileObject);

                Log.info("    compile java.");
                JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnosticCollector, null, null, forCompile);
                task.call();

                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                    Log.info(diagnostic.getMessage(Locale.getDefault()));
                }
                Log.info("    [OK]");
                Log.info("    @ compiled classes @.", deps, forCompile);
            }

            return out.reverse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
