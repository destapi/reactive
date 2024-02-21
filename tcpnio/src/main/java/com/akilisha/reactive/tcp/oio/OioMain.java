package com.akilisha.reactive.tcp.oio;

import java.io.IOException;
import java.util.Date;

public class OioMain {

    public static void main(String[] args) throws IOException {
        Handler handler = (Req in, Res out) -> out.getWriter().println(new Date());

        TcpServer timeServer = new TcpServer();
        timeServer.setHandlers((str) -> handler);
        timeServer.start(9080, "localhost");
    }
}
