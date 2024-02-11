package com.akilisha.reactive.json;

import java.util.LinkedList;

public class JArray extends LinkedList<Object> implements JNode {

    private Observer observer;
    private JNode parent;
    private String path;

    @Override
    public boolean isArray() {
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
    public <E> void addItem(E obj) {
        boolean added = add(obj);
        if (JNode.class.isAssignableFrom(obj.getClass())) {
            ((JNode) obj).parent(this);
        }
        Observer rootObserver = this.root().getObserver();
        if (added && rootObserver != null) {
            rootObserver.add(this, this.tracePath(), obj);
        }
    }

    @Override
    public <E> E getItem(int index) {
        Object value = get(index);
        Observer rootObserver = this.root().getObserver();
        if (rootObserver != null) {
            rootObserver.get(this, this.tracePath(), index, value);
        }
        return (E) value;
    }

    @Override
    public <E> E removeItem(int index) {
        Object value = remove(index);
        Observer rootObserver = this.root().getObserver();
        if (rootObserver != null) {
            rootObserver.delete(this, this.tracePath(), index, value);
        }
        return (E) value;
    }
}
