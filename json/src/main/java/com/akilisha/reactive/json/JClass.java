package com.akilisha.reactive.json;

import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;

import static java.lang.reflect.Modifier.STATIC;

public class JClass extends ClassVisitor {

    private final Set<String> fieldNames = new HashSet<>();
    private final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    Object source;
    JNode parent;
    String key;

    protected JClass(Object source, JNode parent, String key) {
        super(Opcodes.ASM9);
        this.source = source;
        this.parent = parent;
        this.key = key;
    }

    public static JNode nodify(Object target) throws IOException {
        Class<?> targetType = target.getClass();

        if (!isJavaType(targetType)) {
            JNode nodeValue;
            if (Collection.class.isAssignableFrom(targetType)) {
                nodeValue = new JArray();
                for (Object o : (Collection<?>) target) {
                    Object node = nodify(o);
                    nodeValue.addItem(Objects.requireNonNullElse(node, o));
                }
            } else if (Map.class.isAssignableFrom(targetType)) {
                nodeValue = new JObject();
                for (Map.Entry<?, ?> e : ((Map<?, ?>) target).entrySet()) {
                    Object k = e.getKey();
                    Object v = e.getValue();
                    Object node = nodify(v);
                    nodeValue.putItem((String) k, Objects.requireNonNullElse(node, v));
                }
            } else {
                nodeValue = new JObject();
                JClass jClass = new JClass(target, nodeValue, null);
                ClassReader cr = new ClassReader(target.getClass().getName());
                cr.accept(jClass, 0);
            }
            return nodeValue;
        }
        return null;    //means target is a java type, so it should not be introspected
    }

    public static boolean isJavaType(Class<?> type) {
        return (type.isPrimitive() || Arrays.stream(new String[]{"java.", "javax.", "sun.", "com.sun."}).anyMatch(e -> type.getName().startsWith(e)))
                &&
                !(Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (superName.equals(Type.getInternalName(Object.class))) {
            super.visit(version, access, name, signature, superName, interfaces);
        } else {
            try {
                String superClassName = Type.getObjectType(superName).getClassName();
                Class<?> superClass = Class.forName(superClassName);
                if (Collection.class.isAssignableFrom(superClass)) {
                    for (Object item : (Collection<?>) source) {
                        JNode nodeValue = JClass.nodify(item);
                        parent.addItem(Objects.requireNonNullElse(nodeValue, item));
                    }
                } else if (Map.class.isAssignableFrom(superClass)) {
                    for (Map.Entry<?, ?> e : ((Map<?, ?>) source).entrySet()) {
                        Object mapKey = e.getKey();
                        Object mapVal = e.getValue();

                        JNode nodeValue = JClass.nodify(mapVal);
                        parent.putItem((String) mapKey, Objects.requireNonNullElse(nodeValue, mapVal));
                    }
                } else {
                    JClass jClass = new JClass(source, parent, key);
                    ClassReader cr = new ClassReader(superClassName);
                    cr.accept(jClass, 0);
                }
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (access != STATIC) {
            this.fieldNames.add(name);
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (Arrays.stream(new String[]{"get", "is"}).anyMatch(name::startsWith)) {
            String potentialFieldName = name.startsWith("get") ? name.substring(3) : name.substring(2);
            String fieldName = Character.toLowerCase(potentialFieldName.charAt(0)) + potentialFieldName.substring(1);
            if (this.fieldNames.contains(fieldName)) {
                try {
                    MethodType mt = MethodType.methodType(Class.forName(Type.getReturnType(descriptor).getClassName()));
                    MethodHandle handle = lookup.findVirtual(this.source.getClass(), name, mt);
                    Object fieldValue = handle.invoke(this.source);
                    if (fieldValue != null) {
                        // This null check will get rid of null properties all together. Is this the right behavior to uphold?
                        if (isJavaType(fieldValue.getClass())) {
                            this.parent.putItem(fieldName, fieldValue);
                        } else {
                            JNode nodeValue = JClass.nodify(fieldValue);
                            this.parent.putItem(fieldName, nodeValue);
                        }
                    }
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
