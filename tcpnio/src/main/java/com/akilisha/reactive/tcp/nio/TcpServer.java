package com.akilisha.reactive.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class TcpServer {
    private static final int BACKLOG_SIZE = 32;
    private static final int TIMEOUT = 3000; // timeout in milliseconds
    private boolean keepRunning = true;

    private static void configure(int port, String host, ServerSocketChannel server, Selector selector) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(host, port);
        server.socket().bind(addr, BACKLOG_SIZE);
        server.configureBlocking(false);
        int ops = server.validOps();
        server.register(selector, ops, null);
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public void start(int port, String host) throws IOException {
        // create a channel key selector and open a server socket channel
        try (Selector selector = SelectorProvider.provider().openSelector();
             ServerSocketChannel server = ServerSocketChannel.open()) {

            configure(port, host, server, selector);
            System.out.println("Server is listening on port " + port);

            Protocol protocol = new Protocol();
            while (keepRunning) {
                if (selector.select(TIMEOUT) == 0) {
                    continue;
                }

                System.out.println("Processing next selector key");

                //retrieve the set of selectable keys
                Set<SelectionKey> selKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        protocol.accept(key);
                    }

                    if (key.isReadable()) {
                        protocol.read(key);
                    }
                }
            }
        }
    }
}
