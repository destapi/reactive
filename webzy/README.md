# Building on the existing knowledge

Bootstrap a barebone server, with just a tcp connector and a resource handler

```java
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
```

Create a barebone servlet

```java
public class SessionDemo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(200);
        resp.getWriter().println("success");
    }
}
```

Fire up the server and verify that the Servlet is reachable

```java
public static void main(String[] args) throws Exception {
    int port = 9080;
    String resourceRoot = "C:\\Projects\\java\\reactive\\webzy\\www";
    Server server = createServer(port);

    // servlet resource context handler
    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    context.setBaseResource(Resource.newResource(resourceRoot));
    context.setWelcomeFiles(new String[]{"index.html"});

    // add and configure default servlet
    ServletHolder defaultHolder = context.addServlet(DefaultServlet.class, "/");
    defaultHolder.setInitParameter("dirAllowed", "false");
    defaultHolder.setInitParameter("gzip", "true");

    // add demo servlet
    ServletHolder sessionHolder = context.addServlet(SessionDemo.class, "/sess");
    sessionHolder.setInitParameter("sessionDir", sessionDir.toString());

    // start server
    server.setHandler(context);
    server.start();
}
```

Fire up a _curl_ request

```bash
curl http://localhost:9080/sess

#output
success
```

The next step will be to configure the session handler.

Configure a session id manager

```java
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
```

Configure a session cache factory

```java
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
```

Configure a data store factory

```java
    public static FileSessionDataStoreFactory addFileSessionCacheFactory(Server server, File dir) {
    //  There is one SessionDataStore per SessionCache. One file represents one session in one context.
    FileSessionDataStoreFactory storeFactory = new FileSessionDataStoreFactory();
    storeFactory.setStoreDir(dir);
    storeFactory.setGracePeriodSec(3600);
    storeFactory.setSavePeriodSec(0);
    server.addBean(storeFactory);
    return storeFactory;
}
```

Pull together all the _SessionHandler_ plumbing in one function

```java
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
```

Update the _Main_ file to reel in a _SessionHandler_ and add it to the _ServletContextHandler_

```java
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

    // the rest is the same as before
}
```

Update the servlet to take advantage of session handling

```java
public class SessionDemo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[][] choices = Arrays.stream(Objects.requireNonNullElse(req.getQueryString(), "expecting parameters in the query string")
                .split("&")).map(m -> m.split("=")).toArray(String[][]::new);
        int len = Optional.of(choices).map(q -> q.length).orElse(0);
        HttpSession session = req.getSession(true);
        JNode preferences = (JNode) session.getAttribute(PREFERENCES);
        if (preferences == null) {
            preferences = new JObject();
            session.setAttribute(PREFERENCES, preferences);
        }
        if (len > 1) {
            for (String[] pair : choices) {
                preferences.putItem(pair[0], pair[1]);
            }
        }
        resp.setStatus(200);
        resp.getWriter().println(preferences);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode preferences = JReader.fromJson(req.getInputStream());
        HttpSession session = req.getSession(true);
        session.setAttribute(PREFERENCES, preferences);
        preferences.putItem("mood", "happy");

        resp.setStatus(200);
        resp.getWriter().println(preferences);
    }
}
```

Restart the server and fire up a _curl_ request again

```bash
curl http://localhost:9080/sess

#output
Preferences(color=red, size=XL, brand=volvo)
```

The response now contains the user preference configured by default.
Now, pass color in the query string to see the response changing

```bash
curl http://localhost:9080/sess?color=green
Preferences(color=green, size=XL, brand=volvo)
```

The configured _sessionDir_ folder should now also contain some files

![img.png](www/img/session-files.png)

Phase 1 looks good, and in the meantime, it will be put on ice. Now moving onto the next phase, which is working on the
data servlet

This will make use of _SSE_ (on the server-side with htp2 to
overcome [http1 pipelining issues](https://en.wikipedia.org/wiki/HTTP_pipelining))
in conjunction with _EventSource_ (on the client-side) to form a conduit of channeling changes in backend data to the
frontend without manually
refetching the data

## The backend data store

This data store is initialized by sending a json object to the backend through the __PUT__ method of the data servlet
endpoint.
This json can be manipulated (set, get, delete) by sending json snippets which will be overlaid onto the data already
existing
and cached in the backend session (remember phase 1?).

```java
public class EventsServlet extends HttpServlet {

    final DataEvents observer;
    JNode preferences;

    public EventsServlet(DataEvents observer) {
        this.observer = observer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext async = req.startAsync();
        async.start(() -> {
            if (Objects.requireNonNull(req.getHeader("accept"), "Expected an 'Accept' header").contains("text/event-stream")) {

                resp.setHeader("Content-Type", "text/event-stream");
                resp.setHeader("Cache-Control", "no-store");
                resp.setHeader("Connection", "keep-alive");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(200);
                try {
                    PrintWriter out = resp.getWriter();
                    observer.setOut(out);
                    observer.write("connected", "now connected for data events");
                } catch (IOException e) {
                    async.complete();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (preferences == null) {
            preferences = JReader.fromJson(new StringReader("{}"));
            preferences.setObserver(observer);
        }
        String[][] choices = Arrays.stream(Objects.requireNonNullElse(req.getQueryString(), "expecting parameters in the query string")
                .split("&")).map(m -> m.split("=")).toArray(String[][]::new);
        int len = Optional.of(choices).map(q -> q.length).orElse(0);
        if (len > 0) {
            for (String[] pair : choices) {
                preferences.putItem(pair[0], pair[1]);
            }
        }

        resp.setStatus(200);
        resp.getWriter().println(preferences);
    }
}
```

The __GET__ method of the same data servlet endpoint will initiate a __server-sent events__ connection with the browser,
and will
push data whenever this change is detected in the __JNode preferences__ object. This changes made to the data will be
detected by
the __DataEvents__ observer.

From a developer perspective, the only development effort required to achieve this is to create an _Observer_ (in this
case, _DataEvents_
is the observer) to determine exactly what will be transmitted back the the client. In this illustration, we have 4
events of interest.

- set
- get
- delete

These events will be used in the client to accept the corresponding event written to the socket

```java

@Setter
@Getter
public class DataEvents implements Observer {

    PrintWriter out;

    @Override
    public void set(Object target, String path, String key, Object oldValue, Object newValue) {
        write("set", String.format("updated value of %s.%s from %s to %s", path, key, oldValue, newValue));
    }

    @Override
    public void get(Object target, String path, String key, Object value) {
        write("get", String.format("retrieved value '%s' from %s.%s", value, path, key));
    }

    @Override
    public void delete(Object target, String path, String key, Object value) {
        write("delete", String.format("deleted value '%s' from %s.%s", value, path, key));
    }

    @Override
    public void write(String event, String data) {
        out.write(String.format("event: %s\n", event));
        out.write(String.format("data: %s\n\n", data));
        out.flush();
        System.out.println("writing data event to - " + String.format("%s: %s (%d)", event, data, System.identityHashCode(out)));
    }
}
```

Additional plumbing will be required to connect the _EventsServlet_ to the server. This is accomplished by configuring a
new
__ServletContextHandler__

```java
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
```

Since the server no has more than one __ServletContextHandler__, the handlers assigned to the server needs to be
adjusted

```bash
# same as previous

// configure event source
ServletContextHandler sseContext = configureEventsSource(server, "/sse");
        
// start server
ContextHandlerCollection contexts = new ContextHandlerCollection();
contexts.addHandler(context);
contexts.addHandler(sseContext);
server.setHandler(contexts);
server.start();
```

And remember that making that connection to the backend will now make use of http2, and for that, some adjustment will
be needed as well

```java
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
```

## The frontend data display

In isolation, the backend changes made so far will not have any visible effect on the frontend. For this, there is a
need to connect
to the backend through the _EventSource_ object in javascript.

```js
const evtSource = new EventSource("/sse", {
    withCredentials: false
});

evtSource.onmessage = (event) => {
    addMessageLine(event);
    console.log(evtSource.readyState)
};

evtSource.addEventListener('connected', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.addEventListener('set', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.addEventListener('get', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.addEventListener('delete', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.onerror = function (e) {
    addMessageLine({data: "figure out why this error happened"})
    console.log(evtSource.readyState)
};
```

This is simply listening to named events, and sending them all to an _ordered list_ for display.

```html
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"
          name="viewport">
    <meta content="ie=edge" http-equiv="X-UA-Compatible">
    <title>Data Events Demo</title>
    <link href="favicon.ico" rel="shortcut icon" type="image/x-icon"/>
    <link href="index.css" rel="stylesheet" type="text/css">
    <script src="index.js" type="text/javascript"></script>
</head>
<body>
<ol id="list"></ol>
</body>
</html>
```

At this point, restart the server and fire off some _curl requests_.

```bash
 curl -X PUT "http://localhost:9080/sse/?color=blue&make=mazda"
 
 # output
{color=blue, make=mazda}
```

And the browser will continue to show the data which you are changing. This data could by arbitrary

![img.png](www/img/data-changes.png)

Phase 2 looks good, but for completion (for the curious), I will illustrate phase 2 using a _WebSocket_ which is really
an
overkill for the example, but someone else might find it useful.

