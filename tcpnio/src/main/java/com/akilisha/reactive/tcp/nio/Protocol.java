package com.akilisha.reactive.tcp.nio;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Protocol {

    private static final int BUFFER_SIZE = 1024;
    private final ExecutorService executor;

    public Protocol() {
        this.executor = Executors.newCachedThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(this.executor::shutdownNow));
    }

    void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        client.setOption(StandardSocketOptions.TCP_NODELAY, true);
        client.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(BUFFER_SIZE));
        System.out.println("New client connected");
    }

    void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        long numBytes;
        try {
            numBytes = client.read(buffer);
        } catch (IOException e) {
            key.cancel();
            client.close();
            return;
        }

        if (numBytes == -1) {
            key.cancel();
            key.channel().close();
            return;
        }

        //process request (in the same or different thread)
        key.interestOps(SelectionKey.OP_WRITE);
        new EchoReverse().write(key);
    }
}
