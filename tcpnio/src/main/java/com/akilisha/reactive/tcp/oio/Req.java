package com.akilisha.reactive.tcp.oio;

import java.io.InputStream;

public class Req {

    private final InputStream inputStream;

    public Req(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
