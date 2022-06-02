package com.senkinay.cloud.netty;

import com.senkinay.cloud.netty.handlers.EchoHandler;
import com.senkinay.cloud.netty.handlers.FirstInHandler;
import com.senkinay.cloud.netty.handlers.OutHandler;
import com.senkinay.cloud.netty.handlers.SecondInHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

public class EchoPipeLine extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(
                new StringEncoder(),
                new StringDecoder(),
                new EchoHandler()

        );
    }
}
