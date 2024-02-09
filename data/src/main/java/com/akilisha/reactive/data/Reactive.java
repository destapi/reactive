package com.akilisha.reactive.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.nio.file.Paths;

public class Reactive extends ClassLoader {

    public static String suffix = "0";
    public static String defaultBuildPath = "build/classes/java/main";
    public static Reactive loader = new Reactive();
    public static Lookup lookup = MethodHandles.publicLookup();

    private Reactive() {
    }

    public static <T> T observe(Class<T> target, Observer observer) {
        String decoratedClass = target.getName() + suffix;
        try {
            Class<?> generated = Class.forName(decoratedClass, true, loader);
            Object data = generated.getConstructor().newInstance();
            MethodType mt = MethodType.methodType(void.class, Observer.class);
            MethodHandle setter = lookup.findVirtual(generated, "setObs", mt);
            setter.invoke(data, observer);
            return (T) data;
        } catch (Throwable e) {
            throw new RuntimeException("Problem generating new class " + decoratedClass, e);
        }
    }

    public static void generate(Class<?> target, String buildPath, String discriminator) {
        String filePath = String.format("%s%s%s", target.getName(), suffix, discriminator).replace(".", "/");
        File file = Paths.get(System.getProperty("user.dir"), String.format("%s/%s.class", buildPath, filePath)).toFile();
        try {
            Observatory obs = new Observatory(target, suffix);
            byte[] bytes = obs.cw.toByteArray();
            file.createNewFile();
            // try with resources
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.endsWith(suffix)) {
            Class<?> clazz = Class.forName(name.substring(0, name.length() - 1));
            try {
                Observatory obs = new Observatory(clazz, suffix);
                byte[] b = obs.cw.toByteArray();
                return defineClass(name, b, 0, b.length);
            } catch (Exception e) {
                throw new RuntimeException("Problem loading new class " + name, e);
            }
        }
        return super.findClass(name);
    }
}
