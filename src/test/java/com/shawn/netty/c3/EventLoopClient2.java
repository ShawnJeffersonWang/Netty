package com.shawn.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class EventLoopClient2 {

    public static void main(String[] args) throws InterruptedException {
        // 1. 创建启动器类
        // 带有 Future, Promise 的类型都是和 异步方法配套使用的，用来正确处理结果
        ChannelFuture channelFuture = new Bootstrap()
                // 2. 添加 EventLoop
                .group(new NioEventLoopGroup())
                // 3. 选择客户端 channel 实现
                .channel(NioSocketChannel.class)
                // 4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 在连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 5. 连接到服务器
                // connect 是一个异步非阻塞的方法, 主线程main 发起了调用, 真正执行 connect连接的是另一个 nio 线程
                // 1s 秒后
                .connect(new InetSocketAddress("localhost", 8080));

        // 2.1 使用 sync 方法同步处理结果
        // 阻塞住当前线程，直到nio线程连接建立完毕
//        channelFuture.sync();

        // 瞬间就会执行到获取channel的动作，无阻塞的向下执行获取channel
        // 这里的channel其实还没有真正建立好连接
//        Channel channel = channelFuture.channel();
//        log.debug("channel: {}", channel);
//        channel.writeAndFlush("hello, world");

        // 2.2 使用 addListener(回调对象) 方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            // 在 nio 线程连接建立好之后，会调用 operationComplete
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                log.debug("operationComplete.channel: {}", channel);
                channel.writeAndFlush("hello, world");
            }
        });
    }
}
