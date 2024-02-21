package com.akilisha.reactive.tcp.oio;

import java.io.PrintWriter;

public class Res {

    private final PrintWriter writer;

    public Res(PrintWriter writer) {
        this.writer = writer;
    }

    public PrintWriter getWriter() {
        return writer;
    }
}
