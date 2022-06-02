package com.senkinay.cloud.netty;

import com.senkinay.cloud.serialization.SerializationPipeLine;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {
    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup light = new NioEventLoopGroup(1);
        EventLoopGroup hard = new NioEventLoopGroup();
        try {
            bootstrap.group(light, hard)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SerializationPipeLine());

            ChannelFuture channelFuture = bootstrap
                    .bind(8189)
                    .sync();
            log.info("server started...");
            channelFuture.channel().
                    closeFuture().
                    sync();
        } catch (Exception e) {
            log.error("", e);

        } finally {
            light.shutdownGracefully();
            hard.shutdownGracefully();
        }

    }
}
