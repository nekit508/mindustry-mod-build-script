package com.nekit508.updater;

import arc.files.Fi;
import arc.util.serialization.JsonValue;

public class JsonExtHandler {
    public String externalPrefix = "ext:/";

    public void handle(JsonValue root, Fi rootFi, JsonLoadingCache cache) {
        if (root.isString()) {
            String path = root.asString();
            if (path.startsWith(externalPrefix)) {
                Fi externalFi = rootFi.parent().child(path.substring(externalPrefix.length()));

                JsonValue externalRoot;
                if (cache.contains(externalFi)) externalRoot = cache.get(externalFi);
                else externalRoot = Updater.jsonReader.parse(externalFi);

                handle(externalRoot, externalFi, cache);

            }
        } else if (root.isObject() || root.isArray()) {
            handle(root.child(), rootFi, cache);
        }

        if (root.next() != null)
            handle(root.next(), rootFi, cache);
    }

    public void handle(JsonValue root, Fi rootFi) {
        handle(root, rootFi, new JsonLoadingCache());
    }

}
