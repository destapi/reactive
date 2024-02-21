package com.akilisha.reactive.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.lang.System.exit;

public class TcpClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.err.println("Usage: java -cp <libs folder>/tcpnio.jar <package name>.reactive.nio.TcpClient  <host> <port>");
            exit(1);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        InetSocketAddress socketAddress = new InetSocketAddress(hostname, port);

        try (SocketChannel clientChannel = SocketChannel.open(socketAddress)) {
            byte[] message = "This is a test message".getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientChannel.write(buffer);

            // now read response
            ByteBuffer ackBuffer = ByteBuffer.allocate(256);
            clientChannel.read(ackBuffer);
            String ackRes = new String(ackBuffer.array()).trim();

            System.out.println("Ack from Server :" + ackRes);
            Thread.sleep(2000);
        }
    }
}
