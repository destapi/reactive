package com.akilisha.reactive.json;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public <E> E replaceItem(int index, Consumer<JNode> consumer) {
        Object value = get(index);
        consumer.accept((JNode) value);
        Observer rootObserver = this.root().getObserver();
        if (rootObserver != null) {
            rootObserver.replace(this, this.tracePath(), index, value);
        }
        return (E) value;
    }

    @Override
    public <E> E replaceItem(Function<JNode, Boolean> predicate) {
        for (Iterator<Object> iter = this.iterator(); iter.hasNext(); ) {
            JNode node = (JNode) iter.next();
            if (predicate.apply(node)) {
                Observer rootObserver = this.root().getObserver();
                if (rootObserver != null) {
                    rootObserver.replace(this, this.tracePath(), node);
                }
                return (E) node;
            }
        }
        return null;
    }

    @Override
    public void removeItem(int index) {
        Object value = remove(index);
        Observer rootObserver = this.root().getObserver();
        if (rootObserver != null) {
            rootObserver.delete(this, this.tracePath(), index, value);
        }
    }

    @Override
    public void removeAll() {
        for (int i = 0; i < size(); i++) {
            // this should trigger 'remove' event for all items in the array
            removeItem(i);
        }
    }

    @Override
    public void removeAny(Function<JNode, Boolean> predicate) {
        for (Iterator<Object> iter = this.iterator(); iter.hasNext(); ) {
            JNode taskNode = (JNode) iter.next();
            if (predicate.apply(taskNode)) {
                iter.remove();
                Observer rootObserver = this.root().getObserver();
                if (rootObserver != null) {
                    rootObserver.delete(this, this.tracePath(), taskNode);
                }
            }
        }
    }

    @Override
    public void removeFirst(Function<JNode, Boolean> predicate) {
        for (Iterator<Object> iter = this.iterator(); iter.hasNext(); ) {
            JNode taskNode = (JNode) iter.next();
            if (predicate.apply(taskNode)) {
                iter.remove();
                Observer rootObserver = this.root().getObserver();
                if (rootObserver != null) {
                    rootObserver.delete(this, this.tracePath(), taskNode);
                }
                break;
            }
        }
    }
}
