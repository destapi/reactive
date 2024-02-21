package com.akilisha.reactive.webzy.todos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    String listId;
    String id;
    String task;
    boolean completed;
    LocalDateTime startTime;
}
