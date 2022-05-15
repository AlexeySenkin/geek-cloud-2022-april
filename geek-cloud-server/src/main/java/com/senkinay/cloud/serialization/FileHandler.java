package com.senkinay.cloud.serialization;

import com.senkinay.cloud.model.AbstractMessage;
import com.senkinay.cloud.model.FileDownloadMessage;
import com.senkinay.cloud.model.FileUploadMessage;
import com.senkinay.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<AbstractMessage> {


    private final Path serverDir = Path.of("files-server");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        getFileListFromServerDir(ctx);
    }

    public void getFileListFromServerDir(ChannelHandlerContext ctx) throws Exception {
        log.info("send: {} massage", "ListMessage");
        ctx.writeAndFlush(new ListMessage(serverDir));
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
            Path path = Path.of(serverDir + "/" + fileDownload.getName());
            ctx.writeAndFlush(new FileDownloadMessage(path.getFileName().toString(),
                    Files.readAllBytes(path)));
        }

    }
}
