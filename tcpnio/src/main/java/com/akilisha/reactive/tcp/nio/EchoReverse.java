package com.akilisha.reactive.tcp.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class EchoReverse implements Handler {

    @Override
    public void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.flip();
        String input = new String(buffer.array());
        byte[] response = input.toUpperCase().getBytes(StandardCharsets.UTF_8);

        buffer.clear();
        buffer.put(response);
        buffer.flip();

        while (buffer.hasRemaining()) {
            int written = client.write(buffer);
            System.out.printf("Written %d bytes\n", written);
        }

        key.cancel();
    }
}
