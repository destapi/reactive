package com.akilisha.reactive.json;

import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class JWriter {

    public static final JsonGeneratorFactory factory = Json.createGeneratorFactory(Collections.emptyMap());

    public String generate(Object node) {
        StringWriter rt = new StringWriter();
        JsonGenerator generator = factory.createGenerator(rt);
        generate(generator, node, null);
        generator.close();
        return rt.toString();
    }

    public void generate(JsonGenerator generator, Object node, String name) {
        if (((JNode) node).isObject()) {
            if (name != null && !name.trim().isEmpty()) {
                generator.writeStartObject(name);
            } else {
                generator.writeStartObject();
            }
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) node).entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (JNode.class.isAssignableFrom(value.getClass())) {
                    generate(generator, value, key);
                } else {
                    generator.write(key, value.toString());
                }
            }
            generator.writeEnd();
        }
        if (((JNode) node).isArray()) {
            if (name != null && !name.trim().isEmpty()) {
                generator.writeStartArray(name);
            } else {
                generator.writeStartArray();
            }
            for (Object child : ((Collection<?>) node)) {
                if (JNode.class.isAssignableFrom(child.getClass())) {
                    generate(generator, child, null);
                }
            }
            generator.writeEnd();
        }
    }
}
