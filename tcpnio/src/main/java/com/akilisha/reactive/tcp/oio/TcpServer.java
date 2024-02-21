package com.akilisha.reactive.tcp.oio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

public class TcpServer {

    private Function<String, Handler> handlers;
    private boolean keepRunning = true;

    public void setHandlers(Function<String, Handler> handlers) {
        this.handlers = handlers;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public void start(int port, String host) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(host, port);
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(addr, 128);
            System.out.println("Server is listening on port " + port);
            while (keepRunning) {
                Socket socket = serverSocket.accept();  //block and await connection
                System.out.println("New client connected");

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                handlers.apply("/time").handle(new Req(socket.getInputStream()), new Res(writer));
                socket.close();
            }
        }
    }
}
