package com.senkinay.cloud.serialization;

import com.senkinay.cloud.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final Path serverDir = Path.of("files-server");

    private final List<FileAttribute> fileAttributes = new ArrayList<>();

    public class ServerFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {

            if (fileAttributes.isEmpty()) {
                fileAttributes.add(new FileAttribute("server_root",
                        path.getParent().toString(),
                        false,
                        true,
                        false,
                        path.getParent().toString(),
                        new Date(attributes.lastModifiedTime().toMillis()),
                        0));

            }
            fileAttributes.add(new FileAttribute("server",
                    path.toString(),
                    attributes.isRegularFile(),
                    attributes.isDirectory(),
                    attributes.isSymbolicLink(),
                    path.getFileName().toString(),
                    new Date(attributes.lastModifiedTime().toMillis()),
                    attributes.size()));

            return FileVisitResult.CONTINUE;
        }
    }




    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        getFileListFromServerDir(ctx);
    }

    public void getFileListFromServerDir(ChannelHandlerContext ctx) throws Exception {
        fileAttributes.clear();
        Files.walkFileTree(serverDir,new ServerFileVisitor());
        ctx.writeAndFlush(new ListMessage(fileAttributes));
        log.info("send: {} massage", "ListMessage");
    }

    public void sendFileFromServerDir(ChannelHandlerContext ctx, AbstractMessage msg) {
        log.info("send: {} massage", "FileDownloadMessage");
        //ctx.writeAndFlush(new FileDownloadMessage();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
        log.info("received: {} massage", msg.getMessageType().getName());
        if (msg instanceof FileUploadMessage fileUpload) {

            if (!Files.exists(serverDir.resolve(fileUpload.getName()).getParent())) {
                Files.createDirectories(serverDir.resolve(fileUpload.getName()).getParent());
            }
            Files.write(serverDir.resolve(fileUpload.getName()), fileUpload.getBytes());

            getFileListFromServerDir(ctx);
        }
        if (msg instanceof FileDownloadMessage fileDownload) {
            //Path path = Path.of(serverDir + "/" + fileDownload.getName());
            Path path = Path.of(fileDownload.getName());
            ctx.writeAndFlush(new FileDownloadMessage(path.getFileName(),
                    Files.readAllBytes(path)));
        }
        if (msg instanceof ListMessage listMessage) {
            getFileListFromServerDir(ctx);
        }
    }
}
