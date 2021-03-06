package me.eric.costTime

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TimeConsumingMethodVisitor extends MethodVisitor {

    private boolean inject = false
    private String className
    private String methodName

    TimeConsumingMethodVisitor(MethodVisitor mv, String className, String methodName) {
        super(Opcodes.ASM5, mv)
        this.className = className
        this.methodName = methodName
    }

    @Override
    void visitCode() {
        // 方法调用之前
        mv.visitCode()
        if (inject) {
            className = className.replace("/", ".")
            mv.visitLdcInsn(className + " ----> " + methodName)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/util/TimeLogger", "start", "(Ljava/lang/String;)V", false);
        }
        System.out.println(">>>>>TimeConsumingMethodVisitor visitCode:")
    }

    @Override
    void visitInsn(int opcode) {
        // 方法调用之后
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN && inject) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/util/TimeLogger", "end", "()V", false);
        }
        mv.visitInsn(opcode)
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        // Lme/eric/cost_annotation/TimeCost;
        System.out.println(">>>>>TimeConsumingMethodVisitor visitAnnotation:" + desc)

        if (desc.contains("TimeCost")) {
            inject = true
        }
        return super.visitAnnotation(desc, visible)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        System.out.println(">>>>>TimeConsumingMethodVisitor visitEnd:")
    }


}