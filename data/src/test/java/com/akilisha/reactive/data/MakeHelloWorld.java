package com.akilisha.reactive.data;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class MakeHelloWorld extends ClassLoader {

    final ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.endsWith("_Stub")) {
            byte[] b = serializeToBytes(name);
            return defineClass(name, b, 0, b.length);
        }
        return super.findClass(name);
    }

    public byte[] serializeToBytes(String outputClazzName) {
        cw.visit(V11, ACC_PUBLIC + ACC_SUPER, outputClazzName, null, "java/lang/Object", null);
        addStandardConstructor();
        addMainMethod();
        cw.visitEnd();
        return cw.toByteArray();
    }

    void addStandardConstructor() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    void addMainMethod() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Hello World!!");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }
}
