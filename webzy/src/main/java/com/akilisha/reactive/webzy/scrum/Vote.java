package com.akilisha.reactive.webzy.scrum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    String scrumId;
    String screenName;
    String choice;
}
