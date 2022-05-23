package com.senkinay.cloud.model;

import java.io.Serializable;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Objects;

public class FileAttribute implements Serializable {
    private final String code;
    private final String name;
    private final Boolean isFile;
    private final Boolean isDir;
    private final Boolean isLink;
    private final String fileType;
    private final String type;
    private final FileTime fileModificationDate;
    private final long size;

    public FileAttribute(String code, String name, Boolean isFile, Boolean isDir, Boolean isLink, String type, FileTime fileModificationDate, long size) {
        this.code = code;
        this.name = name;
        this.isFile = isFile;
        this.isDir = isDir;
        this.isLink = isLink;
        this.type = type;
        this.fileModificationDate = fileModificationDate;
        this.size = size;
        this.fileType = getFileType();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Boolean getFile() {
        return isFile;
    }

    public Boolean getDir() {
        return isDir;
    }

    public Boolean getLink() {
        return isLink;
    }

    public String getType() {
        return type;
    }

    public FileTime getFileModificationDate() {
        return fileModificationDate;
    }

    public long getSize() {
        return size;
    }

    public String getFileType() {
        if (getDir()) {
            return  "Директория";
        } else if(getLink()) {
            return "Ссылка";

        } else if (getFile()) {
            return "Файл";
        } else {
            return "Неизвестно";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileAttribute that = (FileAttribute) o;
        return size == that.size && code.equals(that.code) && name.equals(that.name) && isFile.equals(that.isFile) && isDir.equals(that.isDir) && isLink.equals(that.isLink) && type.equals(that.type) && fileModificationDate.equals(that.fileModificationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, isFile, isDir, isLink, type, fileModificationDate, size);
    }

    @Override
    public String toString() {
        return type;
    }
}
