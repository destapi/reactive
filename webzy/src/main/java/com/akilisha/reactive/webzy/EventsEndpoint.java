package com.akilisha.reactive.webzy;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class EventsEndpoint extends WebSocketAdapter {

    private final CountDownLatch closureLatch = new CountDownLatch(1);

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        log.debug("Endpoint connected: {}", sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        log.debug("Received TEXT message: {}", message);

        if (message.toLowerCase(Locale.US).contains("bye")) {
            getSession().close(StatusCode.NORMAL, "Thanks");
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        log.debug("Socket Closed: [{}] {}", statusCode, reason);
        closureLatch.countDown();
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public void awaitClosure() throws InterruptedException {
        log.debug("Awaiting closure from remote");
        closureLatch.await();
    }
}
