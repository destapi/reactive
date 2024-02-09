package com.akilisha.reactive.webzy;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class SessionDemo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] color = Objects.requireNonNullElse(req.getQueryString(), "expecting a color parameter in the query string").split("=");
        int len = Optional.of(color).map(q -> q.length).orElse(0);
        HttpSession session = req.getSession(true);
        Preferences preferences = (Preferences) session.getAttribute("preferences");
        if (preferences == null) {
            preferences = new Preferences("red", "XL", "volvo");
            session.setAttribute("preferences", preferences);
        }
        if (len > 1) {
            preferences.color = color[1];
        }
        resp.setStatus(200);
        resp.getWriter().println(preferences);
    }
}
