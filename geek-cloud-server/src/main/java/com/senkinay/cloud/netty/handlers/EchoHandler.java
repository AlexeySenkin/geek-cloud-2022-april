package com.senkinay.cloud.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class EchoHandler extends SimpleChannelInboundHandler<String> {


    @Override
    public void channelActive(ChannelHandlerContext ctx){
        log.info("client connected!!!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        log.info("client disconnected!!!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s){
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        String date =  format.format(new Date());
        ctx.writeAndFlush(date + ": " + s);
    }
}
