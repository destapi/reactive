package com.akilisha.reactive.webzy.chat;

import com.akilisha.reactive.json.*;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class JoinServlet extends HttpServlet {

    private final ChatObserver observer;

    public JoinServlet(ChatObserver observer) {
        this.observer = observer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext async = req.startAsync();
        async.start(() -> {
            if (Objects.requireNonNull(req.getHeader("accept"), "Expected an 'Accept' header").contains("text/event-stream")) {
                String chatId = req.getParameter("chatId");
                String screenName = req.getParameter("screenName");

                resp.setHeader("Content-Type", "text/event-stream");
                resp.setHeader("Cache-Control", "no-store");
                resp.setHeader("Connection", "keep-alive");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(200);
                try {
                    PrintWriter out = resp.getWriter();
                    observer.getWriters().put(String.format("%s#%s", chatId, screenName), out);
                    ChatMessage message = new ChatMessage(chatId, "System", "", String.format("%s has joined the chat", screenName), LocalDateTime.now());
                    JNode node = JClass.nodify(message);
                    observer.write(chatId, "joined", JWriter.stringify(node).replaceAll("\\n", ""));
                } catch (IOException e) {
                    async.complete();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode user = JReader.parseJson(req.getInputStream());
        String screenName = user.getItem("screenName");
        Participant participant = MockPeople.get(screenName);
        if (participant != null) {
            String chatId = user.getItem("chatId");
            JNode chat = MockChatter.get(chatId);
            JNode node = JClass.nodify(participant);
            Objects.requireNonNull(node).putItem("chatId", chatId);
            JNode participants = chat.getItem("participants");
            Objects.requireNonNull(node).parent(participants);
            participants.putItem(screenName, node);

            //prepare response
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().write(JWriter.stringify(participants));
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
