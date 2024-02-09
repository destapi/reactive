package com.akilisha.reactive.data;

import java.io.IOException;
import java.time.LocalTime;

public class GenObserver {

    public static void main(String[] args) throws IOException {
        generateToFile(Reactive.defaultBuildPath, String.valueOf(LocalTime.now().getSecond()));
    }

    public static void generateToFile(String buildPath, String discriminator) {
        Reactive.generate(Data.class, "app/" + buildPath, discriminator);
    }
}
