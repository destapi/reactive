package com.akilisha.reactive.webzy.chat;

import com.akilisha.reactive.json.JClass;
import com.akilisha.reactive.json.JWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyMap;

public class MockPeople {

    public static final Map<String, Participant> ps = new ConcurrentHashMap<>();

    static {
        ps.put("jimmy", new Participant(null, "jimmy", "jimmy@gmail.com", "chester", "WV"));
        ps.put("kathy", new Participant(null, "kathy", "kathy@gmail.com", "dearborn", "MI"));
        ps.put("simba", new Participant(null, "simba", "simba@gmail.com", "forthworth", "TX"));
        ps.put("kadzo", new Participant(null, "kadzo", "jimmy@gmail.com", "san pedro", "CA"));
        ps.put("machi", new Participant(null, "machi", "machi@gmail.com", "des moines", "IA"));
    }

    private MockPeople() {
    }

    public static Participant get(String name) {
        return ps.get(name);
    }


    public static boolean containsKey(String key) {
        return ps.containsKey(key);
    }

    public static void main(String[] args) throws IOException {
        String chatId = UUID.randomUUID().toString();
        Chat chat = new Chat(chatId,
                "The good chat",
                ps.get("jimmy"),
                LocalDateTime.now(),
                emptyMap(),
                Map.of("kathy", ps.get("kathy"), "simba", ps.get("simba")),
                Map.of("jimmy", new ChatMessage(chatId, "jimmy", "", "Let's give it one more minute", LocalDateTime.now())));
        System.out.println(JWriter.stringify(JClass.nodify(chat)));
    }
}
