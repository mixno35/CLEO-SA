package com.mixno.cleo_sa.model;

public class ScriptModel {

    private String name;
    private String path;
    private long date;

    public ScriptModel(String name, String path, long date) {
        this.name = name;
        this.path = path;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getDate() {
        return date;
    }
}
