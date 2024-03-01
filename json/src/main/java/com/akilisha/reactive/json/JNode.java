package com.akilisha.reactive.json;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public interface JNode extends Serializable {

    default boolean isArray() {
        return false;
    }

    default boolean isObject() {
        return false;
    }

    int size();

    boolean isEmpty();

    String path();

    void path(String path);

    JNode parent();

    void parent(JNode node);

    Observer getObserver();

    void setObserver(Observer observer);

    default <E> void addItem(E obj) {
    }

    default <E> void putItem(String key, E obj) {
    }

    default <E> E getItem(int index) {
        return null;
    }

    default <E> E getItem(String key) {
        return null;
    }

    default <E> E replaceItem(int index, Consumer<JNode> consumer) {
        return null;
    }

    default <E> E replaceItem(Function<JNode, Boolean> predicate) {
        return null;
    }

    default <E> E replaceItem(String key, Consumer<JNode> consumer) {
        return null;
    }

    default void removeItem(int index) {
    }

    default void removeItem(String key) {
    }

    default void removeAny(Function<JNode, Boolean> predicate) {
    }

    default void removeFirst(Function<JNode, Boolean> predicate) {
    }

    default JNode root() {
        JNode stop = this;
        while (stop.parent() != null) {
            stop = stop.parent();
        }
        return stop;
    }

    default String tracePath() {
        String fullPath = Objects.requireNonNullElse(this.path(), "");
        JNode node = this.parent();
        if (node != null) {
            do {
                fullPath = String.format("%s%s", Objects.requireNonNullElse(node.path(), ""), fullPath);
                node = node.parent();
            } while (node != null);
        }
        return fullPath;
    }

    default void removeAll() {

    }
}
