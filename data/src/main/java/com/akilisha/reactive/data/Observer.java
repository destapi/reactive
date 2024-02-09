package com.akilisha.reactive.data;

public interface Observer {

    void set(Object source, String field, Object oldValue, Object newValue);

    void get(Object source, String field, Object value);
}
