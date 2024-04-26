package com.shawn.nio.c5;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.shawn.nio.c1.ByteBufferUtil.debugAll;

@Slf4j
public class AioFileChannel {

    public static void main(String[] args) throws IOException {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            // 参数1 ByteBuffer
            // 参数2 读取的起始位置
            // 参数3 附件
            // 参数4 回调对象 CompletionHandler
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("read begin...");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                // read 成功 result 读到的实际字节数
                // bug: 这里的线程是守护线程：其他线程运行完了，他也会结束
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed...{}", result);
                    attachment.flip();
                    debugAll(attachment);
                    // 日志帮助我们看清楚是哪个线程在执行 completed
                }

                // read 失败
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
//                    exc.printStackTrace();
                    log.error("AioFileChannel.main.failed.error:{}", exc.getMessage(), exc);
                }
            });
            log.debug("read end...");
        } catch (IOException e) {
//            e.printStackTrace();
            log.error("AioFileChannel.main.error:{}", e.getMessage(), e);
        }
        System.in.read();
    }
}
