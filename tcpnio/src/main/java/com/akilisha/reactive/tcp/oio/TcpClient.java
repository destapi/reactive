package com.akilisha.reactive.tcp.oio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static java.lang.System.exit;

public class TcpClient {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java -cp <libs folder>/tcpnio.jar <package name>.reactive.oio.TcpClient  <host> <port>");
            exit(1);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String time = reader.readLine();
            System.out.println("Time: " + time);
        }
    }
}
