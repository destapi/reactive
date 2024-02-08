package com.akilisha.reactive;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MakeObservable {

    public static void main(String[] args) throws IOException {
        // generateToFile();
        generateRuntime();
    }

    public static void generateRuntime() throws IOException {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Data newData = Reactive.observe(Data.class, watch);
            newData.setName("Stephano_" + i);
            newData.setVerified(true);
            list.add(newData);
        }
        list.forEach(a -> System.out.printf("Is %s verified? %s", a.getName(), a.isVerified()));
    }

    public static void generateToFile() throws IOException {
        Class<?> clazz = Data.class;
        Observatory obs = new Observatory(clazz, String.valueOf(LocalTime.now().getSecond()));
        WriteBytes.toFile(obs.cw.toByteArray(), obs.baseClassInternal);
    }

    public static Observable watch = new Observable() {

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
    };
}
