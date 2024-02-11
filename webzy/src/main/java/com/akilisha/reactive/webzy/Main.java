package com.akilisha.reactive.webzy;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Main {

    public static Server createServer(int port) {
        Server server = new Server();
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
        // The ConnectionFactory for clear-text HTTP/2.
        HTTP2CServerConnectionFactory h2c = new HTTP2CServerConnectionFactory(httpConfig);
        // The ALPN ConnectionFactory.
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        // The default protocol to use in case there is no negotiation.
        alpn.setDefaultProtocol(http11.getProtocol());

        try (ServerConnector connector = new ServerConnector(server, http11, h2c, alpn)) {
            connector.setPort(port);
            connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
            server.setConnectors(new Connector[]{connector});
        }
        return server;
    }

    public static SessionIdManager configureSessionIdManager(Server server) throws Exception {
        //There is a maximum of one SessionIdManager per Server instance.
        DefaultSessionIdManager idMgr = new DefaultSessionIdManager(server);
        idMgr.setWorkerName("webzy-worker" + LocalDateTime.now().getSecond());
        server.setSessionIdManager(idMgr);

        //There is a maximum of one HouseKeeper per SessionIdManager
        HouseKeeper houseKeeper = new HouseKeeper();
        houseKeeper.setSessionIdManager(idMgr);
        //set the frequency of scavenging cycles
        houseKeeper.setIntervalSec(600L);
        idMgr.setSessionHouseKeeper(houseKeeper);
        return idMgr;
    }

    public static DefaultSessionCacheFactory addDefaultSessionCacheFactory(Server server) {
        // There is one SessionCache per SessionHandler, and thus one per context.
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        //EVICT_ON_INACTIVE: evict a session after 60sec inactivity
        cacheFactory.setEvictionPolicy(SessionCache.NEVER_EVICT);
        //Only useful with the EVICT_ON_INACTIVE policy
        //cacheFactory.setSaveOnInactiveEvict(true);
        cacheFactory.setFlushOnResponseCommit(true);
        cacheFactory.setInvalidateOnShutdown(false);
        cacheFactory.setRemoveUnloadableSessions(true);
        cacheFactory.setSaveOnCreate(true);
        server.addBean(cacheFactory);
        return cacheFactory;
    }

    public static FileSessionDataStoreFactory addFileSessionCacheFactory(Server server, File dir) {
        //  There is one SessionDataStore per SessionCache. One file represents one session in one context.
        FileSessionDataStoreFactory storeFactory = new FileSessionDataStoreFactory();
        storeFactory.setStoreDir(dir);
        storeFactory.setGracePeriodSec(3600);
        storeFactory.setSavePeriodSec(0);
        server.addBean(storeFactory);
        return storeFactory;
    }

    public static SessionHandler configureSessionHandler(Server server, File dir) throws Exception {
        SessionHandler sessionHandler = new SessionHandler();
        // default config
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSecureRequestOnly(true);
        sessionHandler.setSameSite(HttpCookie.SameSite.STRICT);
        // custom config
        sessionHandler.setSessionIdManager(configureSessionIdManager(server));
        sessionHandler.setSessionCache(addDefaultSessionCacheFactory(server).newSessionCache(sessionHandler));
        sessionHandler.getSessionCache().setSessionDataStore(addFileSessionCacheFactory(server, dir).getSessionDataStore(sessionHandler));
        return sessionHandler;
    }

    public static ServletContextHandler configureEventsSource(Server server, String ctx) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder dataEventsHolder = new ServletHolder();
        dataEventsHolder.setServlet(new EventsServlet(new DataEvents()));
        dataEventsHolder.setInitOrder(0);
        dataEventsHolder.setAsyncSupported(true);
        context.addServlet(dataEventsHolder, "/*");
        return context;
    }

    public static ServletContextHandler configureWebsocket(Server server, String ctx, Supplier<Object> endpoint) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        JettyWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            // Configure default max size
            wsContainer.setMaxTextMessageSize(128 * 1024);
            // Add websockets
            wsContainer.addMapping("/*", (upgradeRequest, upgradeResponse) -> {
                upgradeResponse.setAcceptedSubProtocol("protocolOne");
                return endpoint.get();
            });
        });
        return context;
    }

    public static void main(String[] args) throws Exception {
        int port = 9080;
        String resourceRoot = "C:\\Projects\\java\\reactive\\webzy\\www";
        Server server = createServer(port);

        // configure session factories
        Path sessionDir = Path.of(System.getProperty("user.dir"), "webzy/session");
        SessionHandler sessionHandler = configureSessionHandler(server, sessionDir.toFile());

        // servlet context
        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.setSessionHandler(sessionHandler);
        context.setBaseResource(Resource.newResource(resourceRoot));
        context.setWelcomeFiles(new String[]{"index.html"});

        // configure servlet for playing with session
        ServletHolder sessionHolder = context.addServlet(SessionDemo.class, "/data");
        sessionHolder.setInitParameter("sessionDir", sessionDir.toString());

        //configure websocket
        ServletContextHandler wsContext = configureWebsocket(server, "/events", EventsEndpoint::new);

        //configure event source
        ServletContextHandler sseContext = configureEventsSource(server, "/sse");

        // add and configure default servlet
        ServletHolder defaultHolder = context.addServlet(DefaultServlet.class, "/");
        defaultHolder.setInitParameter("dirAllowed", "false");
        defaultHolder.setInitParameter("gzip", "true");

        // start server
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.addHandler(context);
        contexts.addHandler(wsContext);
        contexts.addHandler(sseContext);
        server.setHandler(contexts);
        server.start();
    }
}