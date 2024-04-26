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
 * 阻塞是这个事件还没有发生，这个线程就得暂停
 * 非阻塞是这个事件还没有发生，这个线程还会继续运行
 */
@Slf4j
public class NIOServer {

    public static void main(String[] args) throws IOException {
        /*
        非阻塞模式的好处：尽管是单线程也可以正确处理
        阻塞模式比较傻，干一件事就不能干另一件事
        非阻塞模式比阻塞模式好，但线程一直空转，浪费资源
         */
        // 使用 nio 来理解阻塞模式, 单线程
        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(4);
        // 1. 创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 3. 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4. accept 建立与客户端连接
//            log.debug("connecting...");
            // 非阻塞，线程还会继续运行，如果没有连接建立，sc是null
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                log.debug("connected...{}", sc);
                // 非阻塞模式
                /*
                    非阻塞IO只在等待数据阶段非阻塞，在复制数据阶段用户线程仍然要阻塞
                    牵扯到多次用户空间和内核空间的切换，影响系统的性能
                 */
                sc.configureBlocking(false);
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                // 5. 接受客户端发送的数据
//                log.debug("before read... {}", channel);
                // sc.configureBlocking(false)后 现在read也是非阻塞了 线程仍然会继续运行
                // 如果没有读到数据 read 返回0
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.debug("after read... {}", channel);
                }
            }
        }
    }
}
