package com.akilisha.reactive.webzy.chat;

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

public class HandleChatServlet extends HttpServlet {

    private final ChatObserver observer;

    public HandleChatServlet(ChatObserver observer) {
        this.observer = observer;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode chat = JReader.parseJson(req.getInputStream());
        String screenName = ((JNode) chat.getItem("organizer")).getItem("screenName");
        Participant participant = MockPeople.get(screenName);
        if (participant != null) {
            String chatId = UUID.randomUUID().toString();
            JNode node = JClass.nodify(participant);
            Objects.requireNonNull(node).parent(chat);
            chat.putItem("organizer", node);
            chat.putItem("chatId", chatId);
            chat.putItem("startTime", LocalDateTime.now());
            chat.setObserver(this.observer);
            MockTodos.put(chatId, chat);

            //prepare response
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().write(JWriter.stringify(chat));
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
        String chatId = req.getParameter("chatId");
        String screenName = req.getParameter("screenName");

        if (MockPeople.containsKey(screenName)) {
            JNode chat = MockTodos.get(chatId);

            //new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JNode message = JReader.parseJson(req.getInputStream());
            Objects.requireNonNull(message).putItem("timeSent", LocalDateTime.now());

            JNode chatMessages = chat.getItem("chatMessages");
            message.parent(chatMessages);
            chatMessages.putItem(screenName, message);

            //prepare response
            resp.setStatus(202);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(JWriter.stringify(chatMessages));
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
