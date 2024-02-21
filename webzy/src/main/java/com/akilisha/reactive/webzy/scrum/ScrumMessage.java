package com.akilisha.reactive.webzy.scrum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrumMessage {

    String scrumId;
    String from;
    String vote;
    LocalDateTime timeVoted;
}
