package com.akilisha.reactive.json;

import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public class JWriter {

    public static final JsonGeneratorFactory factory = Json.createGeneratorFactory(
            //note that supported options are provider-specific except pretty printing
            Map.of(JsonGenerator.PRETTY_PRINTING, "\t")
    );

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
                    resolveAndWrite(generator, key, value);
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

    private void resolveAndWrite(JsonGenerator generator, String key, Object value) {
        if (int.class.isAssignableFrom(value.getClass())) {
            generator.write(key, (int) value);
        } else if (long.class.isAssignableFrom(value.getClass())) {
            generator.write(key, (long) value);
        } else if (double.class.isAssignableFrom(value.getClass())) {
            generator.write(key, (double) value);
        } else if (BigInteger.class.isAssignableFrom(value.getClass())) {
            generator.write(key, (BigInteger) value);
        } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
            generator.write(key, (BigDecimal) value);
        } else if (boolean.class.isAssignableFrom(value.getClass())) {
            generator.write(key, (boolean) value);
        } else if (String.class.isAssignableFrom(value.getClass())) {
            generator.write(key, (String) value);
        } else {
            System.out.printf("'%s' type is not yet handled", value.getClass());
        }
    }
}
