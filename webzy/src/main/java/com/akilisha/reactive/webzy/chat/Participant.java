package com.akilisha.reactive.webzy.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    String chatId;
    String screenName;
    String email;
    String city;
    String state;
}
