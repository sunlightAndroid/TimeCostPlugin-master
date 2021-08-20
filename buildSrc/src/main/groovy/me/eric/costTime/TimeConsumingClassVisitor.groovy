package me.eric.costTime

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter


class TimeConsumingClassVisitor extends ClassVisitor {

    TimeConsumingCollector collector = new TimeConsumingCollector();

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
        mv = new AdviceAdapter(Opcodes.ASM5,mv,access,name,signature){
            def inject = false;
            @Override
            AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (descriptor == "TimeCost") {
                    inject = true
                }
                return super.visitAnnotation(descriptor, visible)
            }

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter()
                if(inject){
                      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/timeCost/sample/TimeLogger", "start", "()V", false);
                }


            }

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)
                if(inject){
                      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "me/eric/timeCost/sample/TimeLogger", "end", "()V", false);
                }
            }
        }

//        if ("<init>" != name && "testTime" == name && mv != null) {
//            mv = new TimeConsumingMethodVisitor(mv)
//        }
        return mv
    }
}