package com.akilisha.reactive.webzy.todos;

import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.json.JWriter;
import com.akilisha.reactive.json.Observer;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TodoListObserver implements Observer {

    private final Map<String, PrintWriter> writers = new ConcurrentHashMap<>();

    @Override
    public void set(Object target, String path, String key, Object oldValue, Object newValue) {
        if (path.equals(".todos[]")) {
            String listId = ((JNode) target).getItem("listId");
            write(listId, "updateTask", String.format("{\"%s\": \"%s\"}", key, Objects.requireNonNull(newValue)));
        } else if (path.equals(".sharedTo")) {
            String listId = ((JNode) newValue).getItem("listId");
            write(listId, "addShare", JWriter.stringify(Objects.requireNonNull(newValue)));
        } else if (path.isBlank()) {
            String listId = ((JNode) target).getItem("listId");
            write(listId, "updateList", String.format("{\"%s\": \"%s\"}", key, Objects.requireNonNull(newValue)));
        } else {
            System.out.printf("Setting value in %s from %s to %s\n", path, oldValue, newValue);
        }
    }

    @Override
    public void add(Object target, String path, Object value) {
        if (path.equals(".todos")) {
            String listId = ((JNode) value).getItem("listId");
            write(listId, "newTask", JWriter.stringify(Objects.requireNonNull(value)));
        } else {
            System.out.printf("Adding %s value %s to list\n", path, value);
        }
    }

    @Override
    public void delete(Object target, String path, int index, Object value) {
        if (path.equals(".todos")) {
            String listId = ((JNode) value).getItem("listId");
            write(listId, "removeTask", JWriter.stringify(Objects.requireNonNull(value)));
        } else {
            System.out.printf("removing (%d) item %s from list %s\n", index, value, path);
        }
    }

    @Override
    public void delete(Object target, String path, String key, Object value) {
        if (path.equals(".sharedTo")) {
            String listId = ((JNode) value).getItem("listId");
            write(listId, "removeShare", JWriter.stringify(Objects.requireNonNull(value)));
        } else {
            System.out.printf("removing (%s) item %s from map %s\n", key, value, path);
        }
    }

    @Override
    public void delete(Object target, String path, Object value) {
        if (path.equals(".todos")) {
            String listId = ((JNode) value).getItem("listId");
            write(listId, "removeTask", JWriter.stringify(Objects.requireNonNull(value)));
        } else {
            System.out.printf("removing (%s) item from list %s\n", value, path);
        }
    }

    @Override
    public void write(String target, String data) {
        this.write(target, null, data);
    }

    @Override
    public void write(String target, String event, String data) {
        if (!writers.isEmpty() && data != null && !data.isEmpty()) {
            for (String key : writers.keySet()) {
                if (key.startsWith(target)) {
                    PrintWriter writer = writers.get(key);
                    if (event != null && !event.trim().isEmpty()) {
                        writer.write(String.format("event: %s\n", event));
                    }
                    writer.write(String.format("data: %s\n\n", data.replace("\n", "")));
                    writer.flush();
                    System.out.println("writing data event to - " + String.format("%s: %s (%d)", event, data, System.identityHashCode(writer)));
                }
            }
        }
    }
}
