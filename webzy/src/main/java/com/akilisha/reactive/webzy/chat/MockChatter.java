package com.akilisha.reactive.webzy.chat;

import com.akilisha.reactive.json.JNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockChatter {

    public static final Map<String, JNode> chats = new ConcurrentHashMap<>();

    private MockChatter() {
    }

    public static JNode get(String name) {
        return chats.get(name);
    }

    public static JNode put(String name, JNode chat) {
        return chats.put(name, chat);
    }


    public static boolean containsKey(String key) {
        return chats.containsKey(key);
    }

    public static void remove(String key) {
        chats.remove(key);
    }
}
