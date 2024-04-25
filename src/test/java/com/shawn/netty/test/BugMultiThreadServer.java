package com.shawn.netty.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.shawn.netty.c1.ByteBufferUtil.debugAll;

@Slf4j
public class BugMultiThreadServer {


    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("master");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        // 1. 创建固定数量的 worker 并初始化
        Worker worker = new Worker("worker-0");
        worker.register();
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
//                    log.debug("connected...{}", sc.getRemoteAddress());
                    // 2. 关联selector
                    log.debug("before register...{}", sc.getRemoteAddress());
                    worker.register();
                    sc.register(worker.selector, SelectionKey.OP_READ, null);
                    log.debug("after register...{}", sc.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable {

        private Thread thread;
        private Selector selector;
        private final String name;
        // 还未初始化 volatile 保证可见性
        private volatile boolean start = false;
        // 用消息队列来解耦, 用队列作为通道在两个线程中传递数据
        private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        // 初始化线程，和 selector
        // SocketChannel没有就从参数那传递过来
        public void register() throws IOException {
            // 这里面的所有代码都是在boss线程中执行
            if (!start) {
                // 只有work-0线程启动了，run方法内的所有的代码才会在work-0执行
                thread = new Thread(this, name);
                thread.start();
                selector = Selector.open();
                start = true;
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    log.error("MultiThreadServer.Worker.run.error:{}", e.getMessage(), e);
                }
            }
        }
    }
}
