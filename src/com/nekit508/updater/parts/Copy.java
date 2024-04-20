package com.nekit508.updater.parts;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.serialization.JsonValue;
import com.nekit508.updater.GHApi;
import com.nekit508.updater.RemoteFile;
import com.nekit508.updater.Updater;

public class Copy extends Part {
    Seq<RemoteFile> files = new Seq<>();
    boolean shouldUpdate = true, forced = false;
    Fi versionConfig;

    String latestVersion, currentVersion;

    public Copy() {
        name = "copy";
        Updater.parsers.add(new Updater.ArgParser("force", a -> forced = true));
    }

    @Override
    public void setup() {
        versionConfig = Updater.configDir.child("copy/version");
        currentVersion = versionConfig.exists() ? versionConfig.readString() : "";
        JsonValue value = GHApi.branchInfo(Updater.repo, Updater.branch);
        latestVersion = value.get("commit").get("sha").asString();

        shouldUpdate = !latestVersion.equals(currentVersion) || forced;
    }

    @Override
    public void prepare() {
        if (shouldUpdate)
            GHApi.getAllFiles(Updater.repo, Updater.branch, files);
    }

    @Override
    public void handle() {
        if (shouldUpdate) {
            Fi fi = Updater.localRoot;

            Log.info("Writing files @...", forced ? "(forced)" : "");
            Updater.logger.set("\r");
            files.each(f -> {
                Log.info(f.relativePath);
                f.getFi(fi).write(f.stream(), false);
            });
            Updater.logger.pop();

            Log.info("Wrote version @.", latestVersion);
        } else {
            Log.info("Up to date.");
        }
    }

    @Override
    public void postProcess() {
        if (shouldUpdate)
            versionConfig.writeString(latestVersion);
    }
}
