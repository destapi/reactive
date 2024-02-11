package com.akilisha.reactive.webzy.sess;

import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.json.JObject;
import com.akilisha.reactive.json.JReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class SessionDemo extends HttpServlet {

    static final String PREFERENCES = "preferences";

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
