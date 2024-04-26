package com.shawn.nio.c1;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

import static com.shawn.nio.c1.ByteBufferUtil.debugAll;

@Slf4j
public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 'a'
        buffer.put((byte) 0x61);
        debugAll(buffer);
        // b c d
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        debugAll(buffer);

//        System.out.println(buffer.get());
        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x6f});
        debugAll(buffer);
    }
}
