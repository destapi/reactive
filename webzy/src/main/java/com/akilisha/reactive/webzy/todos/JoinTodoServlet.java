package com.akilisha.reactive.webzy.todos;

import com.akilisha.reactive.json.*;
import com.akilisha.reactive.webzy.todos.model.MockTodos;
import com.akilisha.reactive.webzy.todos.model.TodoEvent;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public class JoinTodoServlet extends HttpServlet {

    private final TodoListObserver observer;

    public JoinTodoServlet(TodoListObserver observer) {
        this.observer = observer;
    }

    /*
     * EventSource establishing connection with the server
     * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        AsyncContext async = req.startAsync();
        async.start(() -> {
            if (Objects.requireNonNull(req.getHeader("accept"), "Expected an 'Accept' header").contains("text/event-stream")) {
                String listId = req.getParameter("listId");
                String listName = req.getParameter("listName");
                String listOwner = req.getParameter("listOwner");

                resp.setHeader("Content-Type", "text/event-stream");
                resp.setHeader("Cache-Control", "no-store");
                resp.setHeader("Connection", "keep-alive");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(200);
                try {
                    PrintWriter out = resp.getWriter();
                    observer.addConnection(listId, listOwner, out);
                    TodoEvent event = new TodoEvent(listId, "System", String.format("'%s' can now access todos named '%s'", listOwner, listName));
                    JNode node = JClass.nodify(event);
                    observer.write(listId, JWriter.stringify(node).replaceAll("\\n", ""));
                } catch (IOException e) {
                    async.complete();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /*
     * accept invitation to join a shared todo-list
     * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JNode member = JReader.parseJson(req.getInputStream());
        try {
            String listId = member.getItem("listId");
            JNode todoList = MockTodos.get(listId);

            if (todoList != null) {
                String memberName = member.getItem("guestName");
                JNode listMembers = todoList.getItem("sharedTo");
                member.parent(listMembers);
                listMembers.putItem(memberName, member); // this will trigger client update event

                //prepare response
                resp.setStatus(201);
                resp.setContentType("application/json");
                resp.getWriter().write(JWriter.stringify(todoList));
            } else {
                JNode error = new JObject();
                error.putItem("message", String.format("Todo list %s is not accessible", listId));

                //prepare response
                resp.setStatus(403);
                resp.setHeader("Content-Type", "application/json");
                resp.getWriter().write(JWriter.stringify(error));
            }
        } catch (Exception e) {
            JNode error = new JObject();
            error.putItem("message", e.getMessage());

            //prepare response
            resp.setStatus(500);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(JWriter.stringify(error));
        }
    }
}
