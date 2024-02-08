package com.akilisha.reactive;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class Reactive extends ClassLoader {

    public static String suffix = "0";
    public static Reactive loader = new Reactive();
    public static Lookup lookup = MethodHandles.publicLookup();

    private Reactive() {
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

    public static <T> T observe(Class<T> target, Observable observable) {
        String decoratedClass = target.getName() + suffix;
        try {
            Class<?> generated = Class.forName(decoratedClass, true, loader);
            Object data = generated.getConstructor().newInstance();
            MethodType mt = MethodType.methodType(void.class, Observable.class);
            MethodHandle setter = lookup.findVirtual(generated, "setObs", mt);
            setter.invoke(data, observable);
            return (T) data;
        } catch (Throwable e) {
            throw new RuntimeException("Problem generating new class " + decoratedClass, e);
        }
    }
}
