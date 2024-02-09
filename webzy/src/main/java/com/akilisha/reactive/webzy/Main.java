package com.akilisha.reactive.webzy;

import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.session.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {

    public static Server createServer(int port) {
        Server server = new Server();
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
        try (ServerConnector connector = new ServerConnector(server, http11)) {
            connector.setPort(port);
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

        // add and configure proxy filter
//        FilterHolder filterHolder = context.addFilter(TryFilesFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
//        // Configure the filter.
//        filterHolder.setInitParameter("files", "$path /index.php?p=$path");
//        filterHolder.setAsyncSupported(true);

        // add and configure default servlet
        ServletHolder defaultHolder = context.addServlet(DefaultServlet.class, "/");
        defaultHolder.setInitParameter("dirAllowed", "false");
        defaultHolder.setInitParameter("gzip", "true");

        ServletHolder sessionHolder = context.addServlet(SessionDemo.class, "/sess");
        sessionHolder.setInitParameter("sessionDir", sessionDir.toString());

        // add and configure fastcgi servlet
//        ServletHolder cgiHolder = context.addServlet(FastCGIProxyServlet.class, "*.php");
//        cgiHolder.setInitParameter("proxyTo", "http://localhost:9000");
//        cgiHolder.setInitParameter("prefix", "/");
//        // cgiHolder.setInitParameter("scriptRoot", Paths.get(resourceRoot, "wordpress").toString());
//        cgiHolder.setInitParameter("scriptRoot", resourceRoot);
//        cgiHolder.setInitParameter("scriptPattern", "(.+?\\.php)");
//        cgiHolder.setInitOrder(1);
//        cgiHolder.setAsyncSupported(true);

        // start server
        server.setHandler(context);
        server.start();
    }
}