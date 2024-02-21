package com.akilisha.reactive.webzy.todos.model;

import com.akilisha.reactive.json.JNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockTodos {

    public static final Map<String, JNode> todos = new ConcurrentHashMap<>();

    private MockTodos() {
    }

    public static JNode get(String name) {
        return todos.get(name);
    }

    public static JNode put(String name, JNode chat) {
        return todos.put(name, chat);
    }


    public static boolean containsKey(String key) {
        return todos.containsKey(key);
    }

    public static void remove(String key) {
        todos.remove(key);
    }
}
