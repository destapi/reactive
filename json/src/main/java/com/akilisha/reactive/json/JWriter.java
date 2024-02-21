package com.akilisha.reactive.json;

import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.akilisha.reactive.json.JClass.isJavaType;

public interface JWriter {

    JsonGeneratorFactory factory = Json.createGeneratorFactory(
            //note that supported options are provider-specific except pretty printing
            Map.of(JsonGenerator.PRETTY_PRINTING, "\t")
    );
    SimpleDateFormat UTC_DATE_TIME_FORMATTER = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ssZ");
    DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    static String stringify(Object node) {
        StringWriter rt = new StringWriter();
        JsonGenerator generator = factory.createGenerator(rt);
        stringify(generator, node, null);
        generator.close();
        return rt.toString().trim();
    }

    static void stringify(JsonGenerator generator, Object node, String name) {
        Class<?> nodeClass = node.getClass();
        if (!nodeClass.isArray() && !isJavaType(nodeClass) && !JNode.class.isAssignableFrom(nodeClass)) {
            try {
                JNode objNode = JClass.nodify(node);
                stringify(generator, Objects.requireNonNull(objNode), name);
            } catch (IOException e) {
                throw new RuntimeException("error parsing property " + name, e);
            }
        } else if (node.getClass().isArray()) {
            int len = Array.getLength(node);
            generator.writeStartArray(name);
            for (int i = 0; i < len; i++) {
                Object element = Array.get(node, i);
                if (JNode.class.isAssignableFrom(element.getClass())) {
                    stringify(generator, element, null);
                } else if (element.getClass().isArray()) {
                    stringify(generator, element, null);
                } else {
                    resolveAndWrite(generator, null, element);
                }
                stringify(generator, Array.get(node, i), null);
            }
            generator.writeEnd();
        } else if (((JNode) node).isObject()) {
            if (name != null && !name.trim().isEmpty()) {
                generator.writeStartObject(name);
            } else {
                generator.writeStartObject();
            }
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) node).entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    if (JNode.class.isAssignableFrom(value.getClass())) {
                        stringify(generator, value, key);
                    } else if (value.getClass().isArray()) {
                        stringify(generator, value, key);
                    } else {
                        resolveAndWrite(generator, key, value);
                    }
                } else {
                    generator.writeNull(key);
                }
            }
            generator.writeEnd();
        } else if (((JNode) node).isArray()) {
            if (name != null && !name.trim().isEmpty()) {
                generator.writeStartArray(name);
            } else {
                generator.writeStartArray();
            }
            for (Object child : (Collection<?>) node) {
                if (JNode.class.isAssignableFrom(child.getClass())) {
                    stringify(generator, child, null);
                } else if (child.getClass().isArray()) {
                    stringify(generator, child, null);
                } else {
                    resolveAndWrite(generator, null, child);
                }
            }
            generator.writeEnd();
        }
    }

    static void resolveAndWrite(JsonGenerator generator, String key, Object value) {
        Class<?> valueType = value.getClass();
        if (int.class.isAssignableFrom(valueType) || Integer.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (int) value);
            else generator.write((int) value);
        } else if (float.class.isAssignableFrom(valueType) || Float.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (float) value);
            else generator.write((float) value);
        } else if (long.class.isAssignableFrom(valueType) || Long.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (long) value);
            else generator.write((long) value);
        } else if (double.class.isAssignableFrom(valueType) || Double.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (double) value);
            else generator.write((double) value);
        } else if (boolean.class.isAssignableFrom(valueType) || Boolean.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (boolean) value);
            else generator.write((boolean) value);
        } else if (BigInteger.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (BigInteger) value);
            else generator.write((BigInteger) value);
        } else if (BigDecimal.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (BigDecimal) value);
            else generator.write((BigDecimal) value);
        } else if (String.class.isAssignableFrom(valueType)) {
            if (key != null) generator.write(key, (String) value);
            else generator.write((String) value);
        } else if (Date.class.isAssignableFrom(valueType)) {
            String formatted = UTC_DATE_TIME_FORMATTER.format((Date) value);
            if (key != null) generator.write(key, formatted);
            else generator.write(formatted);
        } else if (Temporal.class.isAssignableFrom(valueType)) {
            String formatted = ISO_DATE_TIME_FORMATTER.format((TemporalAccessor) value);
            if (key != null) generator.write(key, formatted);
            else generator.write(formatted);
        } else {
            System.out.printf("Assuming the default string representation for type '%s'\n", valueType);
            if (key != null) generator.write(key, value.toString());
            else generator.write(value.toString());
        }
    }
}
