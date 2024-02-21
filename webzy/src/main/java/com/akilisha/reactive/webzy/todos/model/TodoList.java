package com.akilisha.reactive.webzy.todos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoList {

    String listId;
    String listName;
    Member listOwner;
    LocalDateTime dateCreated;
    Map<String, Member> members = new LinkedHashMap<>();
    List<Todo> todos = new LinkedList<>();
}
