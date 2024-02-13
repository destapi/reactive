package com.akilisha.reactive.webzy.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    String id;
    String chatName;
    Participant organizer;
    LocalDateTime startTime;
    Map<String, Chat> privateChats;
    Map<String, Participant> participants = new HashMap<>();
    Map<String, ChatMessage> chatMessages = new HashMap<>();
}
