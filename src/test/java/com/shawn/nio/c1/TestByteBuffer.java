package com.shawn.nio.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel
        // 1. 输入输出流，2. RandomAccessFile
        // new FileInputStream.twr (Java7 try with resource语法，方便对资源的释放,
        // 会帮我们加一个finally块，在finally块中把FileChannel关闭)
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区 (划分内存作为缓冲区)
            // ByteBuffer不能去new, 通过静态方法allocate获取
            ByteBuffer buffer = ByteBuffer.allocate(10);
//            // 从 channel 读取数据, 向缓冲区 buffer 写入
//            int len = channel.read(buffer);
//            // 打印buffer的内容
//            // 切换到buffer的读模式
//            buffer.flip();
//            // 是否还有剩余未读数据
//            while (buffer.hasRemaining()) {
//                // 读到的是字节
//                byte b = buffer.get();
//                // 要转换成字符
//                System.out.println((char) b);
//            }
            while (true) {
                int len = channel.read(buffer);
                // lombok注解加一个日志记录器，就不用System.out了
                log.debug("读取到的字节数 {}", len);
                // 没有内容了
                if (len == -1) {
                    break;
                }
                // 切换至读模式
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
//                    System.out.println((char) b);
                    log.debug("实际字节 {}", (char) b);
                }
                // 切换为写模式
                buffer.clear();
            }
        } catch (IOException e) {

        }
    }
}
