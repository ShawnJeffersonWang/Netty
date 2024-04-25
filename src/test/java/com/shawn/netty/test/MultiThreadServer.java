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
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("master");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        // 1. 创建固定数量的 worker 并初始化
        log.debug("availableProcessors: {}", Runtime.getRuntime().availableProcessors());
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
//        worker.register();
        AtomicInteger index = new AtomicInteger();
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
                    // 静态内部类成员变量可以直接被访问，不管是不是private
                    log.debug("before register...{}", sc.getRemoteAddress());
                    // 初始化 selector, 启动 worker-0
                    // boss 线程调用
                    // round robin 轮询 负载均衡算法
                    workers[index.getAndIncrement() % workers.length].register(sc);
                    // boss
//                    sc.register(worker.selector, SelectionKey.OP_READ, null);
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
        public void register(SocketChannel sc) throws IOException {
            // 这里面的所有代码都是在boss线程中执行
            if (!start) {
                // 只有work-0线程启动了，run方法内的所有的代码才会在work-0执行
                thread = new Thread(this, name);
                thread.start();
//                log.debug("before selector: {}", selector);
                selector = Selector.open();
//                log.debug("after selector: {}", selector);
                start = true;
            }
            // 向队列添加了任务，但这个任务并没有立即执行
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            // 唤醒 select 方法
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
//                    log.debug("Worker.run----------selector: {}", selector);
                    // worker-0 阻塞，wakeup
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        // 在这个位置执行了 sc.register(selector, SelectionKey.OP_READ, null);
                        // 确保肯定是在work-0, 而不是boss
                        // 达到了两个线程间通信的效果
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
                            int read = channel.read(buffer);
                            if (read == -1) {
                                key.cancel();
                            } else {
                                buffer.flip();
                                debugAll(buffer);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("MultiThreadServer.Worker.run.error:{}", e.getMessage(), e);
                }
            }
        }
    }
}
