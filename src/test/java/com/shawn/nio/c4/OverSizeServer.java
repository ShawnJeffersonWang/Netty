package com.shawn.nio.c4;

import com.shawn.nio.c1.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

@Slf4j
public class OverSizeServer {

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        // 0123456789abcdef position 16 limit 16
        source.compact();
    }

    public static void main(String[] args) throws IOException {
        // 1. 创建 selector, 管理多个channel,
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过他可以知道事件，和哪个channel发生的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // key 只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", sscKey);
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 3. select 方法, 没有事件发生，线程阻塞，有事件发生，线程才会恢复运行
            // select 在事件未处理时，他不会阻塞, 事件 已经处理了 或者 取消了 他会阻塞
            // 事件发生后要么处理，要么取消，不能置之不理
            selector.select();
            // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
            // accept, read
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            // 要在集合遍历的时候还想删除得用迭代器遍历，而不能用增强for
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 如果处理完一个key, 应该自己把他从集合里删掉，要是留在这个集合里，那么下一次拿到这个集合
                // 但那个key上又没有真正触发那个事件（已经处理过了）
                // 处理key时，要从 selectedKeys 集合里删除，否则下次处理就会有问题
                iter.remove();
                log.debug("key: {}", key);
                // 5. 区分事件类型
                // 如果是accept
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // accept 方法在非阻塞模式下没有连接返回的是null
                    SocketChannel sc = channel.accept();
                    // selector 配合非阻塞模式一起用
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                    log.debug("scKey: {}", scKey);
//                key.cancel();
                } else if (key.isReadable()) {
                    try {
                        // 读取数据的事件
                        // 拿到触发事件的channel
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        // 如果是正常断开，read 方法的返回值是 -1
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            buffer.flip();
//                            debugRead(buffer);
                            System.out.println(Charset.defaultCharset().decode(buffer));
//                            split(buffer);
                        }
                    } catch (IOException e) {
                        log.error("Server.main.error:{}", e.getMessage(), e);
                        // 没必要再监视断开的channel了
                        // 因为客户端断开了，因此需要将key取消（从selector的keys集合中真正删除key）
                        key.cancel();
                    }
                }
            }
        }
    }
}
