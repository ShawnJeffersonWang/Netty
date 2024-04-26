package com.shawn.nio.c4;

import com.shawn.nio.c1.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 阻塞模式下，某个方法执行时都会影响另一个方法的执行，accept的时候不能read, read的时候不能accept，让当前线程停止运行了
 */
@Slf4j
public class BIOServer {

    public static void main(String[] args) throws IOException {
        // 使用 nio 来理解阻塞模式, 单线程
        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1. 创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 3. 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4. accept 建立与客户端连接
            log.debug("connecting...");
            SocketChannel sc = ssc.accept();
            log.debug("connected...{}", sc);
            channels.add(sc);
            for (SocketChannel channel : channels) {
                // 5. 接受客户端发送的数据
                log.debug("before read... {}", channel);
                channel.read(buffer);
                buffer.flip();
                ByteBufferUtil.debugRead(buffer);
                buffer.clear();
                log.debug("after read... {}", channel);
            }
        }
    }
}
