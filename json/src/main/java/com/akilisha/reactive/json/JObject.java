package com.akilisha.reactive.json;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class JObject extends LinkedHashMap<String, Object> implements JNode {

    private Observer observer;
    private JNode parent;
    private String path;

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public void path(String path) {
        this.path = path;
    }

    @Override
    public JNode parent() {
        return this.parent;
    }

    @Override
    public void parent(JNode node) {
        this.parent = node;
    }

    @Override
    public Observer getObserver() {
        return this.observer;
    }

    @Override
    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    @Override
    public <E> void putItem(String key, E obj) {
        Object oldValue = get(key);
        if (obj != null) {
            if (JNode.class.isAssignableFrom(obj.getClass())) {
                ((JNode) obj).parent(this);
                ((JNode)obj).path(".".concat(key));
            }
        }

        Observer rootObserver = this.root().getObserver();
        if (rootObserver != null) {
            rootObserver.set(this, this.tracePath(), key, oldValue, obj);
        }

        put(key, obj);
    }

    @Override
    public <E> E getItem(String key) {
        Object value = get(key);
        Observer rootObserver = this.root().getObserver();
        if (rootObserver != null) {
            rootObserver.get(this, this.tracePath(), key, value);
        }
        return (E) value;
    }

    @Override
    public void removeItem(String key) {
        Object value = remove(key);
        Observer rootObserver = this.root().getObserver();
        if (rootObserver != null) {
            rootObserver.delete(this, this.tracePath(), key, value);
        }
    }

    @Override
    public void removeAll() {
        Set<String> keys = new HashSet<>(keySet());
        for (String key : keys) {
            //this should trigger 'remove' event for all items in the map
            removeItem(key);
        }
    }
}
