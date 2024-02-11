package com.akilisha.reactive.webzy;

import com.akilisha.reactive.json.Observer;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintWriter;

@Setter
@Getter
public class DataEvents implements Observer {

    PrintWriter out;

    @Override
    public void set(Object target, String path, String key, Object oldValue, Object newValue) {
        write("set", String.format("updated value of %s.%s from %s to %s", path, key, oldValue, newValue));
    }

    @Override
    public void get(Object target, String path, String key, Object value) {
        write("get", String.format("retrieved value '%s' from %s.%s", value, path, key));
    }

    @Override
    public void delete(Object target, String path, String key, Object value) {
        write("delete", String.format("deleted value '%s' from %s.%s", value, path, key));
    }

    @Override
    public void write(String event, String data) {
        out.write(String.format("event: %s\n", event));
        out.write(String.format("data: %s\n\n", data));
        out.flush();
        System.out.println("writing data event to - " + String.format("%s: %s (%d)", event, data, System.identityHashCode(out)));
    }
}
