package com.akilisha.reactive.webzy.chat;

import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.json.JWriter;
import com.akilisha.reactive.json.Observer;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ChatObserver implements Observer {

    private final Map<String, PrintWriter> writers = new ConcurrentHashMap<>();

    @Override
    public void set(Object target, String path, String key, Object oldValue, Object newValue) {
        if (path.equals(".chatMessages")) {
            String chatId = ((JNode) newValue).getItem("chatId");
            write(chatId, "messages", JWriter.stringify(newValue));
        } else if (path.equals(".participants")) {
            String chatId = ((JNode) newValue).getItem("chatId");
            write(chatId, "participants", JWriter.stringify(newValue));
        } else {
            System.out.printf("Setting value in %s from %s to %s\n", path, oldValue, newValue);
        }
    }

    @Override
    public void write(String target, String event, String data) {
        if (!writers.isEmpty()) {
            for (String key : writers.keySet()) {
                if (key.startsWith(target)) {
                    PrintWriter writer = writers.get(key);
                    writer.write(String.format("event: %s\n", event));
                    writer.write(String.format("data: %s\n\n", data.replace("\n", "")));
                    writer.flush();
                    System.out.println("writing data event to - " + String.format("%s: %s (%d)", event, data, System.identityHashCode(writer)));
                }
            }
        }
    }
}
