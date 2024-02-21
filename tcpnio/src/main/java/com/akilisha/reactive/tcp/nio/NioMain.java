package com.akilisha.reactive.tcp.nio;

import java.io.IOException;

public class NioMain {

    public static void main(String[] args) throws IOException {
        TcpServer timeServer = new TcpServer();
        timeServer.start(9090, "localhost");
    }
}
