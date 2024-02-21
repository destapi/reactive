package com.akilisha.reactive.webzy.scrum;

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
public class Scrum {

    String scrumId;
    String title;
    Member member;
    LocalDateTime startTime;
    String question;
    Map<String, Member> members = new LinkedHashMap<>();
    Map<String, Vote> voting = new LinkedHashMap<>();
    List<String> choices = new LinkedList<>();

    public enum MemberType {
        organizer, participant
    }
}
