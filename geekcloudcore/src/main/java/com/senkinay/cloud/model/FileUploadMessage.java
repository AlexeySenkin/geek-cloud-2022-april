package com.senkinay.cloud.model;

import lombok.Getter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class FileUploadMessage extends AbstractMessage{

    private final String name;
    private final byte[] bytes;

    public FileUploadMessage(Path path) throws IOException {
        //this.name = path.getFileName().toString();
        this.name = path.toString();
        this.bytes = Files.readAllBytes(path);

    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_UPLOAD;
    }
}
