package com.akilisha.reactive.webzy.scrum;

import com.akilisha.reactive.json.JClass;
import com.akilisha.reactive.json.JWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MockFolks {

    public static final Map<String, Member> members = new ConcurrentHashMap<>();

    static {
        members.put("jimmy", new Member(null, "jimmy", "jimmy@gmail.com", "chester", "WV"));
        members.put("kathy", new Member(null, "kathy", "kathy@gmail.com", "dearborn", "MI"));
        members.put("simba", new Member(null, "simba", "simba@gmail.com", "forthworth", "TX"));
        members.put("kadzo", new Member(null, "kadzo", "jimmy@gmail.com", "san pedro", "CA"));
        members.put("machi", new Member(null, "machi", "machi@gmail.com", "des moines", "IA"));
    }

    private MockFolks() {
    }

    public static Member get(String name) {
        return members.get(name);
    }


    public static boolean containsKey(String key) {
        return members.containsKey(key);
    }

    public static void main(String[] args) throws IOException {
        String scrumId = UUID.randomUUID().toString();
        Scrum scrum = new Scrum(
                scrumId,
                "The good scrum",
                members.get("jimmy"),
                LocalDateTime.now(),
                "What is the time now",
                Map.of("jimmy", new Member(scrumId, "jimmy", "jimmyb@gmail.com", "Madison", "WI")),
                Collections.emptyMap(),
                List.of("1", "2", "3", "5", "8", "13"));
        System.out.println(JWriter.stringify(JClass.nodify(scrum)));
    }
}
