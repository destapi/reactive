package com.akilisha.reactive.webzy.todos;

import com.akilisha.reactive.json.*;
import com.akilisha.reactive.webzy.todos.model.Member;
import com.akilisha.reactive.webzy.todos.model.MockTodos;
import com.akilisha.reactive.webzy.todos.model.TodoList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Objects;

public class HandleTodosServlet extends HttpServlet {

    private final TodoListObserver observer;

    public HandleTodosServlet(TodoListObserver observer) {
        this.observer = observer;
    }

    /*
     * Generate a new task list, assign a list id and hold it in memory, using the json content in the request body
     * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode todos = JReader.parseJson(req.getInputStream());
        String listId = req.getParameter("listId");
        String listName = req.getParameter("listName");
        String listOwner = req.getParameter("listOwner");
        try {
            TodoList todoList = new TodoList();
            todoList.setListId(listId);
            todoList.setListName(listName);
            Member owner = new Member();
            owner.setListId(listId);
            owner.setName(listOwner);
            todoList.setListOwner(owner);
            todoList.setDateCreated(LocalDateTime.now());
            JNode todoListNode = JReader.parseJson(new StringReader(JWriter.stringify(JClass.nodify(todoList))));

            Objects.requireNonNull(todoListNode).putItem("todos", todos);
            todos.parent(todoListNode);

            MockTodos.put(listId, todoListNode);
            todoListNode.setObserver(this.observer);
            observer.write(listId, "initialized", JWriter.stringify(todos));    // generate client update event manually

            //prepare response
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().write(JWriter.stringify(todoListNode));
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
     * Modify the todo-list
     * */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JNode update = JReader.parseJson(req.getInputStream());
        String task = update.getItem("task");
        String taskUpdate = req.getParameter("task");
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

                if (action.equals("update")) {
                    for (Object t : todos) {
                        JNode taskNode = (JNode) t;
                        if (taskNode.getItem("task").equals(task)) {
                            taskNode.putItem(task, taskUpdate);    // this will trigger client update event
                            break;
                        }
                    }
                }

                if (action.equals("toggleAll")) {
                    for (Object t : todos) {
                        JNode taskNode = (JNode) t;
                        taskNode.putItem("completed", completed);    // this will trigger client update event
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
        String filter = req.getParameter("completed");

        try {
            JNode todoList = MockTodos.get(listId);
            if (todoList != null) {
                if (task != null) {
                    JArray todos = todoList.getItem("todos");
                    todos.removeFirst(node -> node.getItem("task").equals(task)); // this will trigger client update event
                }

                if (filter != null && filter.equalsIgnoreCase("all")) {
                    JArray todos = todoList.getItem("todos");
                    todos.removeAll(); // this will trigger client update event
                }

                if (filter != null && filter.equalsIgnoreCase("completed")) {
                    JArray todos = todoList.getItem("todos");
                    todos.removeAny(t -> t.getItem("completed").equals(true)); // this will trigger client update event
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
}
