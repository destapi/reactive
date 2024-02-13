package com.akilisha.reactive.webzy.cdc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair {

    String key;
    Object value;

    public static Pair of(String key, Object value) {
        return new Pair(key, value);
    }
}
