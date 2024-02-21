package com.akilisha.reactive.webzy.scrum;

import com.akilisha.reactive.json.*;
import com.akilisha.reactive.webzy.todos.model.MockTodos;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class StartScrumServlet extends HttpServlet {

    private final ScrumObserver observer;

    public StartScrumServlet(ScrumObserver observer) {
        this.observer = observer;
    }

    /*
     * Generate a new scrum, assign a scrum id and hold it in memory, using the json content in the request body
     * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode scrum = JReader.parseJson(req.getInputStream());
        String screenName = ((JNode) scrum.getItem("member")).getItem("screenName");
        Member member = MockFolks.get(screenName);
        if (member != null) {
            String scrumId = UUID.randomUUID().toString();
            JNode node = JClass.nodify(member);
            Objects.requireNonNull(node).parent(scrum);
            scrum.putItem("member", node);
            scrum.putItem("scrumId", scrumId);
            scrum.putItem("startTime", LocalDateTime.now());
            scrum.setObserver(this.observer);
            MockTodos.put(scrumId, scrum);

            //prepare response
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().write(JWriter.stringify(scrum));
        } else {
            JNode error = new JObject();
            error.putItem("message", String.format("User %s does not exist", screenName));

            //prepare response
            resp.setStatus(403);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(JWriter.stringify(error));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode voting = JReader.parseJson(req.getInputStream());
        String scrumId = req.getParameter("scrumId");
        String screenName = voting.getItem("screenName");
        String choices = voting.getItem("choices");
        voting.putItem("choices", choices.split("[\n,]"));

        if (MockFolks.containsKey(screenName)) {
            observer.write(scrumId, "voting", JWriter.stringify(voting).replaceAll("\\n", ""));

            JNode ok = new JObject();
            ok.putItem("status", "ok");

            //prepare response
            resp.setStatus(200);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(JWriter.stringify(ok));
        } else {
            JNode error = new JObject();
            error.putItem("message", String.format("User %s does not exist", screenName));

            //prepare response
            resp.setStatus(403);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(JWriter.stringify(error));
        }
    }

//    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String chatId = req.getParameter("chatId");
//        String screenName = req.getParameter("screenName");
//
//        JNode chat = MockTodos.get(chatId);
//        if (!((JNode) chat.getItem("organizer")).getItem("screenName").equals(screenName)) {
//            JNode error = new JObject();
//            error.putItem("message", String.format("User %s is not the chat organizer", screenName));
//
//            //prepare response
//            resp.setStatus(403);
//            resp.setHeader("Content-Type", "application/json");
//            resp.getWriter().write(JWriter.stringify(error));
//        } else {
//            JNode participants = chat.getItem("participants");
//            participants.removeAll();
//
//            MockTodos.remove(chatId);
//
//            JNode success = new JObject();
//            success.putItem("message", String.format("%s chat has been deleted", chatId));
//
//            //prepare response
//            resp.setStatus(200);
//            resp.setHeader("Content-Type", "application/json");
//            resp.getWriter().write(JWriter.stringify(success));
//        }
//    }
}
