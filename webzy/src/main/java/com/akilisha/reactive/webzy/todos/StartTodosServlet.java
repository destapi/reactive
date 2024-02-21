package com.akilisha.reactive.webzy.todos;

import com.akilisha.reactive.json.*;
import com.akilisha.reactive.webzy.todos.model.MockTodos;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class StartTodosServlet extends HttpServlet {

    private final TodoListObserver observer;

    public StartTodosServlet(TodoListObserver observer) {
        this.observer = observer;
    }

    /*
     * Generate a new task list, assign a list id and hold it in memory, using the json content in the request body
     * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode todoList = JReader.parseJson(req.getInputStream());
        try {
            String listId = UUID.randomUUID().toString();
            MockTodos.put(listId, todoList);
            todoList.putItem("dateCreated", LocalDateTime.now());
            todoList.putItem("listId", listId);
            todoList.setObserver(this.observer);
            observer.write(listId, JWriter.stringify(todoList));    // generate client update event manually

            //prepare response
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().write(JWriter.stringify(todoList));
        } catch (Exception e) {
            JNode error = new JObject();
            error.putItem("message", e.getMessage());

            //prepare response
            resp.setStatus(500);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(JWriter.stringify(error));
        }
    }

    /*
     * Toggle the 'completed' status of a task
     * */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode update = JReader.parseJson(req.getInputStream());
        String task = update.getItem("task");
        String listId = update.getItem("listId");
        boolean completed = update.getItem("completed");
        String action = req.getParameter("action");

        try {
            JNode todoList = MockTodos.get(listId);
            if (todoList != null) {
                JArray todos = todoList.getItem("todos");
                if (action.equals("toggle")) {
                    for (Object t : todos) {
                        JNode taskNode = (JNode) t;
                        if (taskNode.getItem("task").equals(task)) {
                            taskNode.putItem("completed", !completed);    // this will trigger client update event
                            break;
                        }
                    }
                }
                if (action.equals("create")) {
                    todos.addItem(update);                  // this will trigger client update event
                }

                JNode ok = new JObject();
                ok.putItem("status", "ok");

                //prepare response
                resp.setStatus(202);
                resp.setHeader("Content-Type", "application/json");
                resp.getWriter().write(JWriter.stringify(ok));
            } else {
                JNode error = new JObject();
                error.putItem("message", String.format("Todo list by id %s does not exist", listId));

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

    /*
     * Delete task from the todo-list
     * */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String task = req.getParameter("task");
        String listId = req.getParameter("listId");

        try {
            JNode todoList = MockTodos.get(listId);
            if (todoList != null) {
                JArray todos = todoList.getItem("todos");
                todos.removeWhere(node -> node.getItem("task").equals(task)); // this will trigger client update event

                JNode ok = new JObject();
                ok.putItem("status", "ok");

                //prepare response
                resp.setStatus(202);
                resp.setHeader("Content-Type", "application/json");
                resp.getWriter().write(JWriter.stringify(ok));
            } else {
                JNode error = new JObject();
                error.putItem("message", String.format("Todo list by id %s does not exist", listId));

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
