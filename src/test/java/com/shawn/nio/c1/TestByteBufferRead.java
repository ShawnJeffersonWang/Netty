package com.shawn.nio.c1;

import java.nio.ByteBuffer;

import static com.shawn.nio.c1.ByteBufferUtil.debugAll;

public class TestByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        // rewind 从头开始读
//        buffer.get(new byte[4]);
//        debugAll(buffer);
//        buffer.rewind();
//        System.out.println((char)buffer.get());
//        debugAll(buffer);
        // mark & reset
        // mark 做一个标记，记录一个 position 位置，reset是将 position 重置到 mark 的位置
//        System.out.println((char) buffer.get());
//        System.out.println((char) buffer.get());
//        // 加标记，索引2的位置
//        buffer.mark();
//        System.out.println((char) buffer.get());
//        System.out.println((char) buffer.get());
//        // 将 position 重置到索引2
//        buffer.reset();
//        System.out.println((char) buffer.get());
//        System.out.println((char) buffer.get());

        // get(i) 不会改变读索引的位置
        System.out.println((char) buffer.get(3));
        debugAll(buffer);
    }
}
