package com.shawn.nio.c1;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.shawn.nio.c1.ByteBufferUtil.debugAll;

public class TestByteBufferString {

    public static void main(String[] args) {
        // 1. 字符串转为 ByteBuffer 因为网络上发送数据并不是直接发字符串，而是先转换为ByteBuffer，然后再写入到channel中
        ByteBuffer buffer1=ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);
        // 第一种方式还是写模式不能直接decode转换
        // 后两种是直接切换到读模式

        // 2. Charset
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        // 3. wrap
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        // 4. 转成字符串
        String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str1);

        buffer1.flip();
        String str2 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str2);
    }
}
