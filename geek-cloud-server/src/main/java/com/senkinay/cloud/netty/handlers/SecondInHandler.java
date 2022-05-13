package com.senkinay.cloud.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

//msg - string
@Slf4j
public class SecondInHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        log.info("received {}", message);
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        String date =  format.format(new Date());
        message = date + " " + message;
        ctx.writeAndFlush(message);
    }
}
