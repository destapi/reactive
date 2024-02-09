package com.akilisha.reactive.data;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class SetterVisitor extends MethodVisitor {

    private String superClassInternal;
    private String name;
    private String getter;
    private String getterDescr;
    private String descriptor;
    private String baseClassInternal;
    private Object property;

    protected SetterVisitor(MethodVisitor methodVisitor, String property, String name, String descriptor, String getter, String getterDescr, String superClassInternal, String baseClassInternal) {
        super(ASM9, methodVisitor);
        this.name = name;
        this.getter = getter;
        this.getterDescr = getterDescr;
        this.descriptor = descriptor;
        this.property = property;
        this.superClassInternal = superClassInternal;
        this.baseClassInternal = baseClassInternal;
    }

    @Override
    public void visitCode() {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClassInternal, getter, getterDescr, false);
        mv.visitVarInsn(ASTORE, 2);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, baseClassInternal, "obs", Type.getDescriptor(Observer.class));

        Label ifNull = new Label();
        mv.visitJumpInsn(IFNULL, ifNull);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, baseClassInternal, "obs",
                Type.getDescriptor(Observer.class));
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(property);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Observer.class),
                "set",
                "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
                true);
        mv.visitLabel(ifNull);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, superClassInternal, name, descriptor,
                false);

        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 3);

        mv.visitEnd();
    }
}
