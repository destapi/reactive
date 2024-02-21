package com.akilisha.reactive.webzy.todos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoEvent {

    String listId;
    String from;
    String message;
}
