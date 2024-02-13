package com.akilisha.reactive.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class JWriterTest {


    String sample = "{\n" +
            "   \"firstName\": \"John\", \"lastName\": \"Smith\", \"age\": 25,\n" +
            "   \"address\" : {\n" +
            "       \"streetAddress\": \"21 2nd Street\",\n" +
            "       \"city\": \"New York\",\n" +
            "       \"state\": \"NY\",\n" +
            "       \"postalCode\": \"10021\"\n" +
            "   },\n" +
            "   \"phoneNumber\": [\n" +
            "       {\"type\": \"home\", \"number\": \"212 555-1234\"},\n" +
            "       {\"type\": \"fax\", \"number\": \"646 555-4567\"}\n" +
            "    ]\n" +
            " }";

    @Test
    void verify_generated_json_matched_sample_json() throws IOException {
        JNode node = JReader.parseJson(new StringReader(sample));
        String json = JWriter.stringify(node);
        System.out.println(json);
        JNode node2 = JReader.parseJson(new StringReader(json));
        assertThat(node).isEqualTo(node2);
    }
}