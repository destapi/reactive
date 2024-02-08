package com.akilisha.reactive;

import java.io.IOException;
import java.time.LocalTime;

public class GenObservable {

    public static void main(String[] args) throws IOException {
        generateToFile(Reactive.defaultBuildPath, String.valueOf(LocalTime.now().getSecond()));
    }

    public static void generateToFile(String buildPath, String discriminator) {
        Reactive.generate(Data.class, "app/" + buildPath, discriminator);
    }
}
