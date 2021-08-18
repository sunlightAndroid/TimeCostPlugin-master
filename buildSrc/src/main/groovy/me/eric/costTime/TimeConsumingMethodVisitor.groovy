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

//        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/router/timeConsuming/TimeLogger", "start", "()V", false);
//        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");


        mv.visitLdcInsn("TAG")
        mv.visitLdcInsn("===== This is just a test message =====")
        mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "android/util/Log",
                "e",
                "(Ljava/lang/String;Ljava/lang/String;)I",
                false
        )
        mv.visitInsn(Opcodes.POP)

    }

    @Override
    void visitInsn(int opcode) {
        // 方法调用之后
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/router/timeConsuming/TimeLogger", "end", "()V", false);
        }
        mv.visitInsn(opcode)
    }
}