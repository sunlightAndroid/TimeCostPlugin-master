package me.eric.costTime

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class TimeConsumingByteCodeGenerator {

    static void get(File file) {

        FileInputStream fileInputStream = new FileInputStream(file.absolutePath)

        ClassReader cr = new ClassReader(fileInputStream)

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)

        ClassVisitor cv = new TimeConsumingClassVisitor(cw)

        cr.accept(cv, ClassReader.SKIP_DEBUG)

        byte[] data = cw.toByteArray()

        //写回原来这个类所在的路径
        FileOutputStream fos = new FileOutputStream(file.getParentFile().getAbsolutePath() + File.separator + file.name)
        System.out.println("生成的文件：" + file.getParentFile().getAbsolutePath() + File.separator + file.name)
        fos.write(data)
        fos.flush()
        fos.close()
    }


    static void get(String filePath) {

        File file = new File(filePath)

        FileInputStream fileInputStream = new FileInputStream(file.absolutePath)

        ClassReader cr = new ClassReader(fileInputStream)

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)

        ClassVisitor cv = new TimeConsumingClassVisitor(cw)

        cr.accept(cv, ClassReader.SKIP_DEBUG)

        byte[] data = cw.toByteArray()

        //写回原来这个类所在的路径
        FileOutputStream fos = new FileOutputStream(file.getParentFile().getAbsolutePath() + File.separator + file.name)
        System.out.println("生成的文件：" + file.getParentFile().getAbsolutePath() + File.separator + file.name)
        fos.write(data)
        fos.flush()
        fos.close()
    }

}











