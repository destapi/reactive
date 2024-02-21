package com.akilisha.reactive.tcp.oio;

public interface Handler {

    void handle(Req in, Res out);
}
