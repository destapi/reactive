package com.akilisha.reactive.json;

import lombok.RequiredArgsConstructor;

import java.io.PrintWriter;

@RequiredArgsConstructor
public class AddLiteralToArrayObserver implements Observer {

    final PrintWriter writer;

    @Override
    public void add(Object target, String path, Object value) {
        if (path.contains(".luckyNums")) {
            writer.println(String.format("Adding lucky number %s", value.toString()));
        }
    }
}
