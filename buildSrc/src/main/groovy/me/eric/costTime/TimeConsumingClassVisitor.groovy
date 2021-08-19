package me.eric.costTime


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TimeConsumingClassVisitor extends ClassVisitor {


    TimeConsumingClassVisitor(ClassWriter cv) {
        super(Opcodes.ASM5, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces)
        System.out.println("》》》》》 TimeConsumingClassVisitor:" + "  name:" + name + " signature:" + signature + " superName:" + superName)
        // 》》》》》 TimeConsumingClassVisitor:  name:me/eric/timeCost/MainActivity signature:null superName:androidx/appcompat/app/AppCompatActivity
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("》》》》》 TimeConsumingClassVisitor:" + "  MethodName:" + name + " desc:" + desc + " signature:" + signature)
        // 》》》》》 TimeConsumingClassVisitor:  MethodName:testTime desc:()V signature:null

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions)
        if ("<init>" != name && "testTime" == name && mv != null) {
            mv = new TimeConsumingMethodVisitor(mv)
        }
        return mv
    }
}