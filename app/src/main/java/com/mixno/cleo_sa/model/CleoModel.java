package com.mixno.cleo_sa.model;

public class CleoModel {

    private String name;
    private String description;
    private String file_cs;
    private String file_fxt;

    public CleoModel(String name, String description, String file_cs, String file_fxt) {
        this.name = name;
        this.description = description;
        this.file_cs = file_cs;
        this.file_fxt = file_fxt;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFileCS() {
        return file_cs;
    }

    public String getFileFXT() {
        return file_fxt;
    }
}
