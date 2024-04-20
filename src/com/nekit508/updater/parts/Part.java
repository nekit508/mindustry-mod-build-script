package com.nekit508.updater.parts;

public abstract class Part {
    public String name = "";

    public String name() {
        if (name.equals(""))
            throw new RuntimeException(new IllegalArgumentException("name must be specified."));
        return name;
    }

    public void setup() {

    }

    public void prepare() {

    }

    public void handle() {

    }

    public void postProcess() {

    }
}
