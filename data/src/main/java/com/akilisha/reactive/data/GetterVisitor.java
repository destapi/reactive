package com.akilisha.reactive.data;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class GetterVisitor extends MethodVisitor {

    private String superClassInternal;
    private String name;
    private String descriptor;
    private String baseClassInternal;
    private Object property;

    protected GetterVisitor(MethodVisitor methodVisitor, String property, String name, String descriptor, String superClassInternal, String baseClassInternal) {
        super(ASM9, methodVisitor);
        this.name = name;
        this.descriptor = descriptor;
        this.property = property;
        this.superClassInternal = superClassInternal;
        this.baseClassInternal = baseClassInternal;
    }

    @Override
    public void visitCode() {
        // call super
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClassInternal, name, descriptor, false);
        mv.visitVarInsn(ASTORE, 1);

        // call observer if not null
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, baseClassInternal, "obs", Type.getDescriptor(Observer.class));

        Label ifNull = new Label();
        mv.visitJumpInsn(IFNULL, ifNull);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, baseClassInternal, "obs", Type.getDescriptor(Observer.class));
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(property);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Observer.class), "get",
                "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", true);
        mv.visitLabel(ifNull);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(4, 2);

        mv.visitEnd();
    }
}
