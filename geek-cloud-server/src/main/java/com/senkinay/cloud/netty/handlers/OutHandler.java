package com.senkinay.cloud.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

// string
@Slf4j
public class OutHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String message = (String) msg;
        log.info("received: {}",message);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeCharSequence(message, StandardCharsets.UTF_8);
        ctx.writeAndFlush(buffer);

    }
}
