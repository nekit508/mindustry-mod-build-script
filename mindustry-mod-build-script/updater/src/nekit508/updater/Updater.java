package nekit508.updater;

import arc.util.Log;
import arc.util.serialization.JsonReader;
import nekit508.updater.log.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Updater {
    public static JsonReader jsonReader = new JsonReader();
    public static Logger logger = new Logger();
    public static Globals globals;

    public static void main(String[] args) {
        Log.logger = logger;
        globals = JsePlatform.standardGlobals();
        LuaValue script = globals.load("print \"Hello world!\"");

        script.call();
    }
}
