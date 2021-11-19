package com.mixno.cleo_sa.model;

public class ScriptFileModel {

    private String name;
    private String path;
    private int lastModified;

    public ScriptFileModel(String name, String path, int lastModified) {
        this.name = name;
        this.path = path;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getLastModified() {
        return lastModified;
    }
}
