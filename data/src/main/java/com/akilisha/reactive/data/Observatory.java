package com.akilisha.reactive.data;

import org.objectweb.asm.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class Observatory extends ClassVisitor {

    final ClassWriter cw;
    final String suffix;
    final Class<?> superClass;
    final String baseClassName;
    final String baseClassInternal;
    final String superClassInternal;
    final Map<String, String> fields = new HashMap<>();

    public Observatory(Class<?> clazz, String suffix) throws IOException {
        super(ASM9);
        this.suffix = suffix;
        this.superClass = clazz;
        this.superClassInternal = Type.getInternalName(superClass);
        this.baseClassName = String.format("%s%s", superClass.getName(), suffix);
        this.baseClassInternal = baseClassName.replace(".", "/");

        ClassReader cr = new ClassReader(clazz.getName());
        this.cw = new ClassWriter(cr, COMPUTE_MAXS | COMPUTE_FRAMES);
        cr.accept(this, EXPAND_FRAMES);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cw.visit(V11, ACC_PUBLIC | ACC_SUPER, baseClassInternal, signature, superClassInternal, interfaces);
    }

    @Override
    public void visitSource(String source, String debug) {
        cw.visitSource(String.format("%s_%s.java", superClass.getSimpleName(), suffix), debug);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        if (name.startsWith("set")) {
            String property = name.replaceFirst("^set", "");
            String getter = descriptor.equals("(Z)V") ? "is" + property : "get" + property;
            String getterDescr = descriptor.replaceFirst("\\)V$", ")").replaceFirst("\\((.+?)\\)", "()$1");
            property = Character.toLowerCase(property.charAt(0)) + property.substring(1);

            if (fields.containsKey(property)) {
                MethodVisitor mv = cw.visitMethod(access, name, descriptor, signature, exceptions);
                return new SetterVisitor(mv, property, name, descriptor, getter, getterDescr, superClassInternal,
                        baseClassInternal);
            }
        }
        if (Arrays.stream(new String[]{"is", "get"}).anyMatch(p -> name.startsWith(p))) {
            String property = name.replaceFirst("^(get|is)", "");
            property = Character.toLowerCase(property.charAt(0)) + property.substring(1);

            if (fields.containsKey(property)) {
                MethodVisitor mv = cw.visitMethod(access, name, descriptor, signature, exceptions);
                return new GetterVisitor(mv, property, name, descriptor, superClassInternal, baseClassInternal);
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        this.fields.put(name, descriptor);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        addObservableField();
        addSetterForObservable();
        cw.visitEnd();
    }

    void addObservableField() {
        FieldVisitor fv = cw.visitField(ACC_PRIVATE, "obs", Type.getDescriptor(Observer.class), null, null);
        fv.visitEnd();
    }

    void addSetterForObservable() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "setObs", "(" + Type.getDescriptor(Observer.class) + ")V", null,
                null);
        mv.visitCode();

        // load 'this' to the stack
        mv.visitVarInsn(ALOAD, 0);
        // load observable to the stack
        mv.visitVarInsn(ALOAD, 1);
        // assign variable
        mv.visitFieldInsn(PUTFIELD, baseClassInternal, "obs", Type.getDescriptor(Observer.class));
        // return
        mv.visitInsn(RETURN);
        // end code section
        mv.visitMaxs(2, 2);

        mv.visitEnd();
    }
}
