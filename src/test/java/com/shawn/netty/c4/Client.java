package com.shawn.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        // 调试技巧：在Threads & Variables中选择sc 点击Evaluate Expression...
        // Evaluate Expression sc.write(Charset.defaultCharset().encode("hello)) 可以调试模拟客户端发消息
        // 如果一开始就写死的话就看不到accept read的阻塞过程了

        // 并行启动客户端：右上角Edit Configurations... -> Modify options -> Allow multiple instances
        SocketAddress address = sc.getLocalAddress();
//        sc.write(Charset.defaultCharset().encode("hello\nworld\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));
        System.in.read();
//        System.out.println("waiting...");
    }
}
