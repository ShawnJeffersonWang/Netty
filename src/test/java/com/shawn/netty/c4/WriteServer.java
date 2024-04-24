package com.shawn.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        // 在注册时就可以关注他的accept事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    // 1. 向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    // 2. 返回值代表实际写入的字节数
                    // 网络缓冲区写满了，写不进去了，就返回0
                    int write = sc.write(buffer);
                    System.out.println(write);
                    // 3. 不要用while, 判断是否有剩余内容
                    if (buffer.hasRemaining()) {
                        // 4. 关注可写事件, selector就会有相应的事件触发 读事件：1      写事件：4
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
//                        scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        // 5. 把未写完的数据挂到 scKey 上
                        scKey.attach(buffer);
                    }
//                    while (buffer.hasRemaining()) {
//                        int write = sc.write(buffer);
//                        System.out.println(write);
//                    }
                    // 这是一种思想，响应式的处理事件，write缓冲区满的时候返回值为0, 是不可写的，这个时候就不要一直往里面写了
                    // 而是等缓冲区被清空再去往缓冲区里写
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    // 6. 清理buffer
                    if (!buffer.hasRemaining()) {
                        // 新关联一个null值就会把上次关联的attachment给替换掉，没人再引用这个ByteBuffer, 就会被垃圾回收掉
                        key.attach(null);
                        // 需要清除buffer
                        // 不需要关注可写事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
