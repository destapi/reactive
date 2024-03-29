package com.akilisha.reactive.webzy.scrum;

import com.akilisha.reactive.json.JNode;
import com.akilisha.reactive.json.JObject;
import com.akilisha.reactive.json.JWriter;
import com.akilisha.reactive.json.Observer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScrumObserver implements Observer {

    private final Map<String, Map<String, PrintWriter>> writers = new ConcurrentHashMap<>();

    public void addConnection(String scrumId, String screenName, PrintWriter out) {
        if (!writers.containsKey(scrumId)) {
            writers.put(scrumId, new HashMap<>());
        }
        if (writers.get(scrumId).containsKey(screenName)) {
            PrintWriter existing = (PrintWriter) writers.get(scrumId);
            existing.close();
        }
        //save new writer
        writers.get(scrumId).put(screenName, out);
    }

    public void dropConnection(String scrumId, String screenName) {
        if (writers.containsKey(scrumId)) {
            if (writers.get(scrumId).containsKey(screenName)) {
                PrintWriter existing = (PrintWriter) writers.get(scrumId);
                existing.close();
            }
        }
    }

    @Override
    public void set(Object target, String path, String key, Object oldValue, Object newValue) {
        if (path.isEmpty() && key.equals("choices")) {
            String scrumId = ((JNode) target).getItem("scrumId");
            write(scrumId, "submitChoices", JWriter.stringify(newValue));
        } else if (path.isEmpty() && key.equals("question")) {
            JNode question = new JObject();
            question.putItem("question", newValue);
            String scrumId = ((JNode) target).getItem("scrumId");
            write(scrumId, "submitQuestion", JWriter.stringify(question));
        } else if (path.equals(".members")) {
            String scrumId = ((JNode) newValue).getItem("scrumId");
            write(scrumId, "joinScrum", JWriter.stringify(newValue));
        } else if (path.equals(".voting")) {
            String scrumId = ((JNode) newValue).getItem("scrumId");
            write(scrumId, "submitVote", JWriter.stringify(newValue));
        } else if (path.matches(".voting.\\w+$")) {
            String scrumId = ((JNode) target).getItem("scrumId");
            String screenName = ((JNode) target).getItem("screenName");
            JNode change = new JObject();
            change.putItem("screenName", screenName);
            change.putItem("from", oldValue);
            change.putItem("to", newValue);
            write(scrumId, "updateVote", JWriter.stringify(change));
        } else {
            System.out.printf("Setting value in [%s] from %s to %s\n", path, oldValue, newValue);
        }
    }

    @Override
    public void delete(Object target, String path, String key, Object value) {
        if (path.equals(".members")) {
            write("leaveScrum", JWriter.stringify(value));
        } else if (path.equals(".voting")) {
            write("leaveVoting", JWriter.stringify(value));
        } else {
            System.out.printf("Removing value [%s] %s with key %s\n", path, key, value);
        }
    }

    @Override
    public void write(String target, String data) {
        this.write(target, null, data);
    }

    @Override
    public void write(String target, String event, String data) {
        if (!writers.isEmpty()) {
            if (writers.containsKey(target)) {
                for (String key : writers.get(target).keySet()) {
                    PrintWriter writer = writers.get(target).get(key);
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
