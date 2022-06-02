package com.senkinay.cloud.model;

public enum MessageType {
    FILE_UPLOAD("file_upload"),
    FILE_DOWNLOAD("file_download"),
    LIST("list"),
    DELETE("delete");


    private final String name;

    MessageType(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

}
