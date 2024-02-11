package com.akilisha.reactive.json;

import jakarta.json.stream.JsonGenerator;

import java.util.Collection;
import java.util.Map;

public class JWriter {

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
