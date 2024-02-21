package com.akilisha.reactive.tcp.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Handler {

    void write(SelectionKey key) throws IOException;
}
