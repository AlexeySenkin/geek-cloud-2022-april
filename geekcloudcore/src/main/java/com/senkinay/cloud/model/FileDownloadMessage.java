package com.senkinay.cloud.model;

import lombok.Getter;

import java.io.IOException;


@Getter
public class FileDownloadMessage extends AbstractMessage {

    private final String name;
    private final byte[] bytes;

    public FileDownloadMessage(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;

    }
    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_DOWNLOAD;
    }
}
