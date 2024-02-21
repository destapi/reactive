package com.akilisha.reactive.webzy.scrum;

import com.akilisha.reactive.json.*;
import com.akilisha.reactive.webzy.todos.model.MockTodos;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Objects;

public class JoinScrumServlet extends HttpServlet {

    private final ScrumObserver observer;

    public JoinScrumServlet(ScrumObserver observer) {
        this.observer = observer;
    }

    /*
     * EventSource establishing connection with the server
     * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext async = req.startAsync();
        async.start(() -> {
            if (Objects.requireNonNull(req.getHeader("accept"), "Expected an 'Accept' header").contains("text/event-stream")) {
                String scrumId = req.getParameter("scrumId");
                String screenName = req.getParameter("screenName");

                resp.setHeader("Content-Type", "text/event-stream");
                resp.setHeader("Cache-Control", "no-store");
                resp.setHeader("Connection", "keep-alive");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(200);
                try {
                    PrintWriter out = resp.getWriter();
                    observer.getWriters().put(scrumId, out);
                    ScrumMessage message = new ScrumMessage(scrumId, "System", String.format("%s has joined scrum", screenName), LocalDateTime.now());
                    JNode node = JClass.nodify(message);
                    observer.write(scrumId, JWriter.stringify(node).replaceAll("\\n", ""));
                } catch (IOException e) {
                    async.complete();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /*
     * accept invitation to join a scrum
     * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode user = JReader.parseJson(req.getInputStream());
        String screenName = user.getItem("screenName");
        Member member = MockFolks.get(screenName);
        if (member != null) {
            String scrumId = user.getItem("scrumId");
            JNode scrum = MockTodos.get(scrumId);
            JNode node = JClass.nodify(member);
            Objects.requireNonNull(node).putItem("scrumId", scrumId);
            JNode participants = scrum.getItem("members");
            Objects.requireNonNull(node).parent(participants);
            participants.putItem(screenName, node);

            //prepare response
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().write(JWriter.stringify(node));
        } else {
            JNode error = new JObject();
            error.putItem("message", String.format("User %s does not exist", screenName));

            //prepare response
            resp.setStatus(403);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(JWriter.stringify(error));
        }
    }
}
