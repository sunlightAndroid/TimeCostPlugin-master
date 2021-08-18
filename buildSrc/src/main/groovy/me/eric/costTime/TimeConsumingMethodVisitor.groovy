package me.eric.costTime

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TimeConsumingMethodVisitor extends MethodVisitor {


    TimeConsumingMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv)
    }

    @Override
    void visitCode() {
        // 方法调用之前
        mv.visitCode()
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/timeCost/sample/TimeLogger", "start", "()V", false);
    }

    @Override
    void visitInsn(int opcode) {
        // 方法调用之后
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/timeCost/sample/TimeLogger", "end", "()V", false);
        }
        mv.visitInsn(opcode)
    }
}