package com.akilisha.reactive.data;

import java.io.IOException;

public class Data {

    private String name;
    private boolean verified;

    public Data() {
    }

    public Data(String name) {
        this.name = name;
    }

    public static Data observe() {
        Data0 data = new Data0();
        data.setObs(new Observer() {

            @Override
            public void set(Object source, String field, Object oldValue, Object newValue) {
                System.out.printf("Setting value of property '%s' in class '%s' from %s to %s\n", field,
                        source.getClass().getName(), oldValue, newValue);
            }

            @Override
            public void get(Object source, String field, Object value) {
                System.out.printf("getting value of property '%s' in class '%s' with value %s\n", field,
                        source.getClass().getName(), value);
            }
        });
        return data;
    }

    public static void main(String[] args) throws IOException {
        Data data = observe();
        data.setName("Steve");
        System.out.println("Name: " + data.getName());
        data.setVerified(true);
        System.out.println("is verified? " + data.isVerified());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
