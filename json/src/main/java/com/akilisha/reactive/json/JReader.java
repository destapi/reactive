package com.akilisha.reactive.json;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Stack;

public interface JReader {

    static JNode parseJson(Reader reader) {
        return parseJson(Json.createParser(reader));
    }

    static JNode parseJson(InputStream is) {
        return parseJson(Json.createParser(is));
    }

    static JNode parseJson(String json) throws IOException {
        try (InputStream fis = JReader.class.getResourceAsStream(json)) {
            return parseJson(fis);
        }
    }

    static JNode parseJson(JsonParser jsonParser) {
        Stack<JNode> stack = new Stack<>();
        Stack<String> keys = new Stack<>();

        while (jsonParser.hasNext()) {
            JsonParser.Event event = jsonParser.next();
            switch (event) {
                case START_ARRAY: {
                    JArray array = new JArray();
                    if (!stack.isEmpty()) {
                        array.parent(stack.peek());
                        array.path(".".concat(keys.peek()));
                    }
                    stack.push(array);
                    break;
                }
                case START_OBJECT: {
                    JObject object = new JObject();
                    if (!stack.isEmpty()) {
                        object.parent(stack.peek());
                        if (stack.peek().isObject()) {
                            object.path(".".concat(keys.peek()));
                        }
                        if (stack.peek().isArray()) {
                            object.path("[]");
                        }
                    }
                    stack.push(object);
                    break;
                }
                case END_ARRAY:
                case END_OBJECT: {
                    JNode top = stack.pop();
                    if (stack.isEmpty()) {
                        return top;
                    } else {
                        JNode parent = stack.pop();
                        if (parent.isObject()) {
                            parent.putItem(keys.pop(), top);
                            stack.push(parent);
                        }
                        if (parent.isArray()) {
                            parent.addItem(top);
                            stack.push(parent);
                        }
                    }
                    break;
                }
                case KEY_NAME: {
                    String key = jsonParser.getString();
                    keys.push(key);
                    break;
                }
                case VALUE_STRING: {
                    String value = jsonParser.getString();
                    JNode top = stack.pop();
                    if (top.isArray()) {
                        top.addItem(value);
                    }
                    if (top.isObject()) {
                        top.putItem(keys.pop(), value);
                    }
                    stack.push(top);
                    break;
                }
                case VALUE_NUMBER: {
                    Number number = jsonParser.getBigDecimal();
                    JNode top = stack.pop();
                    if (top.isArray()) {
                        top.addItem(number);
                    }
                    if (top.isObject()) {
                        top.putItem(keys.pop(), number);
                    }
                    stack.push(top);
                    break;
                }
                case VALUE_FALSE: {
                    JNode top = stack.pop();
                    if (top.isArray()) {
                        top.addItem(false);
                    }
                    if (top.isObject()) {
                        top.putItem(keys.pop(), false);
                    }
                    stack.push(top);
                    break;
                }
                case VALUE_TRUE: {
                    JNode top = stack.pop();
                    if (top.isArray()) {
                        top.addItem(true);
                    }
                    if (top.isObject()) {
                        top.putItem(keys.pop(), true);
                    }
                    stack.push(top);
                    break;
                }
                case VALUE_NULL: {
                    JNode top = stack.pop();
                    if (top.isArray()) {
                        top.addItem(null);
                    }
                    if (top.isObject()) {
                        top.putItem(keys.pop(), null);
                    }
                    stack.push(top);
                    break;
                }
                default:
                    // we are not looking for other events
                    break;
            }
        }
        throw new RuntimeException("Looks like the parsing was not successful since the processing stack still contains values");
    }

    static JNode fromObject(Object root) {
        return null;
    }
}
