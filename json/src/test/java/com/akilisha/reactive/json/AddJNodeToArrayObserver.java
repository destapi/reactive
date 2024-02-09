package com.akilisha.reactive.json;

import lombok.RequiredArgsConstructor;

import java.io.PrintWriter;

@RequiredArgsConstructor
public class AddJNodeToArrayObserver implements Observer {

    final PrintWriter writer;

    @Override
    public void add(Object target, String path, Object value) {
        if (path.contains(".phones")) {
            writer.println(String.format("adding office phone %s", value.toString()));
        }
    }
}
