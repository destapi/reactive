package com.akilisha.reactive.json;

import lombok.RequiredArgsConstructor;

import java.io.PrintWriter;

@RequiredArgsConstructor
public class UpdateObjectPropertyObserver implements Observer {

    final PrintWriter writer;

    @Override
    public void set(Object target, String path, String key, Object oldValue, Object newValue) {
        if (path.contains(".phones[].number")) {
            writer.println(String.format("Phone number's '%s' updated from %s to %s", key, oldValue.toString(), newValue.toString()));
        }
    }
}
