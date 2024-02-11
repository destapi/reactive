package com.akilisha.reactive.json;

import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;

class JWriterTest {

    JsonGeneratorFactory factory = Json.createGeneratorFactory(Collections.emptyMap());
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
        JNode node = JReader.fromJson(new StringReader(sample));
        StringWriter rt = new StringWriter();
        JsonGenerator generator = factory.createGenerator(rt);
        new JWriter().generate(generator, node, null);
        generator.close();
        System.out.println(rt);
    }
}