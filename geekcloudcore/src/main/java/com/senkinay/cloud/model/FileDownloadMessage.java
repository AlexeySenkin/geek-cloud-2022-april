package com.senkinay.cloud.model;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;


@Getter
public class FileDownloadMessage extends AbstractMessage {

    private final String name;
    private final byte[] bytes;

    public FileDownloadMessage(Path path, byte[] bytes) {
        this.name = path.toString();
        this.bytes = bytes;

    }
    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_DOWNLOAD;
    }
}
