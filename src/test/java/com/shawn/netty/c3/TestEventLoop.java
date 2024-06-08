package com.shawn.netty.c3;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        // 1. 创建事件循环组
        // io 事件，普通任务，定时任务
        EventLoopGroup group = new NioEventLoopGroup(2);
        // 普通任务，定时任务
//        EventLoopGroup group = new DefaultEventLoop();
//        System.out.println(NettyRuntime.availableProcessors());
        // 2. 获取下一个事件循环对象 轮询的效果
//        System.out.println(group.next());
//        System.out.println(group.next());
//        System.out.println(group.next());

        // 3. 执行普通任务
//        group.next().submit(() -> {
//            // 用日志的好处就是可以看出是在那个线程里执行的
//            // 提交给事件循环组中某一个事件循环对象去处理，实现异步处理
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                log.error("TestEventLoop.main.submit.error:{}", e.getMessage(), e);
//            }
//            log.debug("ok");
//        });

        // 4. 执行定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);

        log.debug("main");
    }
}
