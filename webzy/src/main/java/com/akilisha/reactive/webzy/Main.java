package com.akilisha.reactive.webzy;

import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.webzy.cdc.CdcConnect;
import com.akilisha.reactive.webzy.chat.ChatObserver;
import com.akilisha.reactive.webzy.chat.ChatServlet;
import com.akilisha.reactive.webzy.chat.JoinServlet;
import com.akilisha.reactive.webzy.scrum.JoinScrumServlet;
import com.akilisha.reactive.webzy.scrum.ScrumObserver;
import com.akilisha.reactive.webzy.scrum.StartScrumServlet;
import com.akilisha.reactive.webzy.sess.SessionDemo;
import com.akilisha.reactive.webzy.sse.DataEvents;
import com.akilisha.reactive.webzy.sse.EventsServlet;
import com.akilisha.reactive.webzy.todos.JoinTodoServlet;
import com.akilisha.reactive.webzy.todos.StartTodosServlet;
import com.akilisha.reactive.webzy.todos.TodoListObserver;
import com.akilisha.reactive.webzy.ws.EventsEndpoint;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.http.pathmap.RegexPathSpec;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;

public class Main {

    public static final String SCRUMMING = "scrumming";
    public static final String CHATTING = "chatting";
    public static final String TODOLIST = "todolist";

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
        //EVICT_ON_INACTIVE: evict a sess after 60sec inactivity
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
        //  There is one SessionDataStore per SessionCache. One file represents one sess in one context.
        FileSessionDataStoreFactory storeFactory = new FileSessionDataStoreFactory();
        storeFactory.setStoreDir(dir);
        storeFactory.setGracePeriodSec(3600);
        storeFactory.setSavePeriodSec(0);
        server.addBean(storeFactory);
        return storeFactory;
    }

    public static JDBCSessionDataStoreFactory addJdbcDataStoreFactory(DataSource dataSource) {
        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDatasource(dataSource);
        // databaseAdaptor.setDatasource(myDataSource); // you can set data source here (for connection pooling, etc)
        JDBCSessionDataStoreFactory jdbcSessionDataStoreFactory = new JDBCSessionDataStoreFactory();
        jdbcSessionDataStoreFactory.setDatabaseAdaptor(databaseAdaptor);
        return jdbcSessionDataStoreFactory;
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

    public static SessionHandler configureJdbcSessionHandler(Server server, DataSource dataSource) throws Exception {
        SessionHandler sessionHandler = new SessionHandler();
        // default config
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSecureRequestOnly(true);
        sessionHandler.setSameSite(HttpCookie.SameSite.STRICT);
        // custom config
        sessionHandler.setSessionIdManager(configureSessionIdManager(server));
        sessionHandler.setSessionCache(addDefaultSessionCacheFactory(server).newSessionCache(sessionHandler));
        sessionHandler.getSessionCache().setSessionDataStore(addJdbcDataStoreFactory(dataSource).getSessionDataStore(sessionHandler));
        return sessionHandler;
    }

    public static ServletContextHandler configureEventsSource(Server server, String ctx) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder dataEventsHolder = new ServletHolder();
        dataEventsHolder.setServlet(new EventsServlet(new DataEvents()));
        dataEventsHolder.setInitOrder(2);
        dataEventsHolder.setAsyncSupported(true);
        context.addServlet(dataEventsHolder, "/*");
        return context;
    }

    public static ServletContextHandler configureJoinChatServlet(Server server, String ctx, ChatObserver observer) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder chatHolder = new ServletHolder();
        chatHolder.setServlet(new JoinServlet(observer));
        chatHolder.setInitOrder(1);
        chatHolder.setAsyncSupported(true);
        context.addServlet(chatHolder, "/*");
        return context;
    }

    public static ServletContextHandler configureJoinScrumServlet(Server server, String ctx, ScrumObserver observer) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder chatHolder = new ServletHolder();
        chatHolder.setServlet(new JoinScrumServlet(observer));
        chatHolder.setInitOrder(1);
        chatHolder.setAsyncSupported(true);
        context.addServlet(chatHolder, "/*");
        context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        return context;
    }

    public static ServletContextHandler configureJoinTodoServlet(Server server, String ctx, TodoListObserver observer) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder chatHolder = new ServletHolder();
        chatHolder.setServlet(new JoinTodoServlet(observer));
        chatHolder.setInitOrder(1);
        chatHolder.setAsyncSupported(true);
        context.addServlet(chatHolder, "/*");
        context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        return context;
    }

    public static ServletContextHandler configureStartChatServlet(Server server, String ctx, ChatObserver observer) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder chatHolder = new ServletHolder();
        chatHolder.setServlet(new ChatServlet(observer));
        chatHolder.setInitOrder(0);
        chatHolder.setAsyncSupported(true);
        context.addServlet(chatHolder, "/*");
        return context;
    }

    public static ServletContextHandler configureStartScrumServlet(Server server, String ctx, ScrumObserver observer) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder chatHolder = new ServletHolder();
        chatHolder.setServlet(new StartScrumServlet(observer));
        chatHolder.setInitOrder(0);
        chatHolder.setAsyncSupported(true);
        context.addServlet(chatHolder, "/*");
        context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        return context;
    }

    public static ServletContextHandler configureStartTodoServlet(Server server, String ctx, TodoListObserver observer) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        //add new data events listener
        ServletHolder chatHolder = new ServletHolder();
        chatHolder.setServlet(new StartTodosServlet(observer));
        chatHolder.setInitOrder(0);
        chatHolder.setAsyncSupported(true);
        context.addServlet(chatHolder, "/*");
        context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        return context;
    }

    public static ServletContextHandler configureWebsocket(Server server, String ctx, Function<String, Object> endpoint) {
        ServletContextHandler context = new ServletContextHandler(server, ctx);

        JettyWebSocketServletContainerInitializer.configure(context, (servletContext, container) -> {
            // Configure default max size
            container.setMaxTextMessageSize(128 * 1024);
            container.setIdleTimeout(Duration.ofMinutes(2));    // this allows you to estimate a ping interval of say 90 seconds on the client side
            // Add websockets
            container.addMapping("^/ws/(.+)$", (upgradeRequest, upgradeResponse) -> {
                upgradeResponse.setAcceptedSubProtocol("protocolOne");

                // Retrieve the Regex template (other templates are ServletPathSpec and UriTemplatePathSpec).
                RegexPathSpec pathSpec = (RegexPathSpec) upgradeRequest.getServletAttribute(PathSpec.class.getName());

                // Match the URI template.
                Matcher matcher = pathSpec.getPattern().matcher(upgradeRequest.getRequestPath());
                String id = "0";
                if (matcher.find()) {
                    id = matcher.group(1);
                }

                return endpoint.apply(id);
            });
        });
        return context;
    }

    public static void updateFromQueryString(JNode node, String query) {
        String[][] choices = Arrays.stream(query.split("&")).map(m -> m.split("=")).toArray(String[][]::new);
        int len = Optional.of(choices).map(q -> q.length).orElse(0);
        if (len > 0) {
            for (String[] pair : choices) {
                node.putItem(pair[0], pair[1]);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String activity = args.length > 0 ? args[0] : SCRUMMING;
        int port = 9080;
        String resourceRoot = "C:\\Projects\\java\\reactive\\webzy\\www";
        Server server = createServer(port);

        // configure file data session handler
        Path sessionDir = Path.of(System.getProperty("user.dir"), "webzy/sess");
        SessionHandler fileDataSessionHandler = configureSessionHandler(server, sessionDir.toFile());

        // configure jdbc data session handler
//        String driver = "org.h2.Driver";
//        String url = "jdbc:h2:tcp://localhost/~/.data/h2/jdbc-session;USER=sa;PASSWORD=sa";
        SessionHandler jdbcDataSessionHandler = configureJdbcSessionHandler(server, CdcConnect.instance.dataSource());

        // servlet context
        ServletContextHandler context = new ServletContextHandler(server, "/");
//        context.setSessionHandler(fileDataSessionHandler);
        context.setSessionHandler(jdbcDataSessionHandler);
        context.setBaseResource(Resource.newResource(resourceRoot));
        context.setWelcomeFiles(new String[]{"index.html"});

        // configure servlet for playing with sess
        ServletHolder sessionDemo = context.addServlet(SessionDemo.class, "/data");
        sessionDemo.setInitParameter("sessionDir", sessionDir.toString());

        //configure websocket
        ServletContextHandler wsContext = configureWebsocket(server, "/events", EventsEndpoint::new);

        //configure event source
        ServletContextHandler sseContext = configureEventsSource(server, "/sse");

        //configure either chat or scrum servlet
        ServletContextHandler startServlet = null;
        ServletContextHandler joinServlet = null;

        if (activity.equals(CHATTING)) {
            ChatObserver chatObserver = new ChatObserver();
            startServlet = configureStartChatServlet(server, "/chat", chatObserver);
            joinServlet = configureJoinChatServlet(server, "/join", chatObserver);
        } else if (activity.equals(SCRUMMING)) {
            ScrumObserver scrumObserver = new ScrumObserver();
            startServlet = configureStartScrumServlet(server, "/scrum", scrumObserver);
            joinServlet = configureJoinScrumServlet(server, "/join", scrumObserver);
        } else if(activity.equals(TODOLIST)){
            TodoListObserver todosObserver = new TodoListObserver();
            startServlet = configureStartTodoServlet(server, "/todos", todosObserver);
            joinServlet = configureJoinTodoServlet(server, "/join", todosObserver);
        }
        else {
            System.err.println("No activity has been registered");
        }

        // add and configure default servlet
        ServletHolder defaultHolder = context.addServlet(DefaultServlet.class, "/");
        defaultHolder.setInitParameter("dirAllowed", "false");
        defaultHolder.setInitParameter("gzip", "true");

        // configure handlers
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.addHandler(context);
        contexts.addHandler(wsContext);
        contexts.addHandler(sseContext);
        if (startServlet != null) contexts.addHandler(startServlet);
        if (joinServlet != null) contexts.addHandler(joinServlet);

        // add handler and start server
        server.setHandler(contexts);
        server.start();
    }
}