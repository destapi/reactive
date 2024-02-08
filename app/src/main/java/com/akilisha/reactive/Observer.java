package com.akilisha.reactive;

public interface Observer {
    
    void set(Object source, String field, Object oldValue, Object newValue);

    void get(Object source, String field, Object value);
}
