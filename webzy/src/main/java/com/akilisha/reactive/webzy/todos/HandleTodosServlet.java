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
import java.util.Optional;

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
        JNode initialItems = JReader.parseJson(req.getInputStream());
        String listId = initialItems.getItem("listId");
        String listName = initialItems.getItem("listName");
        String listOwner = initialItems.getItem("listOwner");
        try {
            TodoList todoList = new TodoList();
            todoList.setListId(listId);
            todoList.setListName(listName);
            Member owner = new Member();
            owner.setListId(listId);
            owner.setName(listOwner);
            todoList.setListOwner(owner);
            todoList.setDateCreated(LocalDateTime.now());
            JNode cachedTodoList = JReader.parseJson(new StringReader(JWriter.stringify(JClass.nodify(todoList))));

            Objects.requireNonNull(cachedTodoList).putItem("todos", initialItems.getItem("todos"));
            initialItems.parent(cachedTodoList);

            MockTodos.put(listId, cachedTodoList);
            cachedTodoList.setObserver(this.observer);
            observer.write(listId, "initialized", JWriter.stringify(initialItems));    // generate client update event manually

            //prepare response
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.getWriter().write(JWriter.stringify(cachedTodoList));
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
        String listId = update.getItem("listId");
        String action = req.getParameter("action");

        try {
            JNode todoList = MockTodos.get(listId);
            if (todoList != null) {
                JArray todos = todoList.getItem("todos");
                if (action.equals("toggle")) {
                    String id = update.getItem("id");
                    todos.replaceItem(node -> {
                        if (node.getItem("id").equals(id)) {
                            node.putItem("completed", Optional.ofNullable(node.getItem("completed")).map(bool -> !(boolean) bool).orElse(true)); // this will trigger client update event
                            return true;
                        }
                        return false;
                    });
                }

                if (action.equals("update")) {
                    String task = update.getItem("task");
                    String id = update.getItem("id");
                    todos.replaceItem(node -> {
                        if (node.getItem("id").equals(id)) {
                            node.putItem("task", task); // this will trigger client update event
                            return true;
                        }
                        return false;
                    });
                }

                if (action.equals("toggleAll")) {
                    boolean completed = update.getItem("completed");
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
        String taskId = req.getParameter("id");
        String listId = req.getParameter("listId");

        try {
            JNode todoList = MockTodos.get(listId);
            if (todoList != null) {
                if (taskId != null) {
                    JArray todos = todoList.getItem("todos");
                    todos.removeFirst(node -> node.getItem("id").equals(taskId)); // this will trigger client update event

                    JNode ok = new JObject();
                    ok.putItem("status", "ok");

                    //prepare response
                    resp.setStatus(202);
                    resp.setHeader("Content-Type", "application/json");
                    resp.getWriter().write(JWriter.stringify(ok));
                } else {
                    JNode error = new JObject();
                    error.putItem("message", String.format("Task by id %s does not exist", taskId));

                    //prepare response
                    resp.setStatus(403);
                    resp.setHeader("Content-Type", "application/json");
                    resp.getWriter().write(JWriter.stringify(error));
                }
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
