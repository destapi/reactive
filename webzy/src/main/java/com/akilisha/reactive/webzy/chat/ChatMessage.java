package com.akilisha.reactive.webzy.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    String chatId;
    String from;
    String to;
    String message;
    LocalDateTime timeSent;
}
