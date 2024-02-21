package com.akilisha.reactive.json;

public interface Observer {

    // jarray
    default void add(Object target, String path, Object value) {
    }

    // jobject
    default void set(Object target, String path, String key, Object oldValue, Object newValue) {
    }

    // jarray
    default void get(Object target, String path, int index, Object value) {
    }

    // jobject
    default void get(Object target, String path, String key, Object value) {
    }

    // jarray
    default void delete(Object target, String path, int index, Object value) {
    }

    // jobject
    default void delete(Object target, String path, String key, Object value) {
    }

    // jarray - remove using predicate function
    default void delete(Object target, String path, Object value) {
    }

    default void write(String target, String data) {
    }

    default void write(String target, String event, String data) {
    }
}
