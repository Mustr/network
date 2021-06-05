package com.mustr.http.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class ClientChannelInit extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //客户端编解码器
        pipeline.addLast(new HttpClientCodec());
        
        pipeline.addLast("aggre", new HttpObjectAggregator(10 * 1024 * 1024));
        pipeline.addLast("decompressor", new HttpContentDecompressor());
        
        pipeline.addLast(new MustrClientHandler());
    }

}
