package com.akilisha.reactive.webzy;

import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.json.JReader;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

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

