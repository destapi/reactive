package com.akilisha.reactive.json.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    Names name;
    LocalDate joined;
    List<Phone> phones;
    Hobby[] hobbies;
    Map<String, Task> todos;
    List<Float> luckNums;
}
