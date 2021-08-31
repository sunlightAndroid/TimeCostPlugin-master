package me.eric.costTime

import org.gradle.internal.FileUtils
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

// me/eric/biz/video/AudioActivity.class
    static byte[] writeJar(BufferedInputStream inputStream) {

        try {
            ClassReader cr = new ClassReader(inputStream)
            ClassWriter cw = new ClassWriter(cr, 0)
            ClassVisitor cv = new TimeConsumingClassVisitor(cw)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            return cw.toByteArray()
        } catch (Exception e) {
            throw new RuntimeException("Error while writeJar ", e)
        }
    }
}











