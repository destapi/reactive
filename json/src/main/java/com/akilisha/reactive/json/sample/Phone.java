package com.akilisha.reactive.json.sample;

public class Phone {

    PhoneType type;
    PhoneNumber number;

    public enum PhoneType {

        HOME, OFFICE, CELL
    }

    public static class PhoneNumber {
        String code;
        String local;
    }
}
