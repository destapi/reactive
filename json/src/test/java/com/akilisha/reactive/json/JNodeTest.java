package com.akilisha.reactive.json;

import com.akilisha.reactive.json.sample.Phone;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class JNodeTest {

    public static JNode sampleNode() {
        JObject user = new JObject();

        JObject name = new JObject();
        name.putItem("first", "James");
        name.putItem("last", "kijana");
        user.putItem("name", name);

        user.putItem("joined", LocalDate.now());

        JArray numbers = new JArray();
        numbers.addItem(1.2);
        numbers.addItem(2.3);
        user.putItem("luckyNums", numbers);

        JArray phones = new JArray();
        JObject cell = new JObject();
        JObject cellNum = new JObject();
        cellNum.putItem("area", "145");
        cellNum.putItem("local", "345-6543");
        cell.putItem("type", Phone.PhoneType.CELL.name());
        cell.putItem("number", cellNum);
        JObject home = new JObject();
        JObject homeNum = new JObject();
        homeNum.putItem("area", "567");
        homeNum.putItem("local", "343-6767");
        home.putItem("type", Phone.PhoneType.HOME.name());
        home.putItem("number", homeNum);
        phones.putItem("cell", cell);
        phones.putItem("home", home);
        user.putItem("phones", phones);

        JObject todos = new JObject();
        JObject read = new JObject();
        read.putItem("title", "read book");
        read.putItem("done", false);
        todos.putItem("1", read);
        user.putItem("todos", todos);

        JObject hobbies = new JObject();
        JObject skydive = new JObject();
        skydive.putItem("name", "skydive");
        skydive.putItem("rating", 8);
        todos.putItem("skydive", skydive);
        user.putItem("hobbies", hobbies);

        return user;
    }

    @Test
    void verify_parsing_json_into_JNode() throws IOException {
        JNode user = sampleNode();

        JNode userNode = JReader.parseJson("/user-data.json");
        assertThat(userNode).isNotNull();
        assertThat(user.size()).isEqualTo(userNode.size());

        //check out paths
        assertThat(userNode.tracePath()).isEqualTo("");
        assertThat(((JNode) userNode.getItem("name")).tracePath()).isEqualTo(".name");
        assertThat(((JNode) userNode.getItem("luckyNums")).tracePath()).isEqualTo(".luckyNums");
        assertThat(((JNode) ((JNode) userNode.getItem("phones")).getItem(0)).tracePath()).isEqualTo(".phones[]");
        assertThat(((JNode) ((JNode) ((JNode) userNode.getItem("phones")).getItem(0)).getItem("number")).tracePath()).isEqualTo(".phones[].number");
        assertThat(((JNode) ((JNode) userNode.getItem("todos")).getItem("1")).tracePath()).isEqualTo(".todos.1");
    }
}