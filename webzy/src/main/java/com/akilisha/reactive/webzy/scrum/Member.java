package com.akilisha.reactive.webzy.scrum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    String scrumId;
    String screenName;
    String email;
    String city;
    String state;
}
