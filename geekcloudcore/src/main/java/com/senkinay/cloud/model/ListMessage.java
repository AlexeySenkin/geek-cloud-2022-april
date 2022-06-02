package com.senkinay.cloud.model;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Getter
public class ListMessage extends AbstractMessage {

//    private final List<String> files;
//
//    public ListMessage(Path path) throws IOException {
//        files = Files.list(path)
//                .map(Path::getFileName)
//                .map(Path::toString)
//                .toList();
//    }

    private final List<FileAttribute> files;

    public ListMessage(List<FileAttribute> files) {
        this.files = files;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST;
    }
}
