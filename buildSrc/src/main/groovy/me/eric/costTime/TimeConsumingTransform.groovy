package me.eric.costTime

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 *  统计耗时时间的
 */
class TimeConsumingTransform extends Transform {

    /**
     * 当前 Transform 的名称
     * @return
     */
    @Override
    String getName() {
        return "TimeConsumingTransform"
    }

    /**
     * 返回告知编译器，当前Transform需要消费的输入类型
     * 在这里是CLASS类型
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 告知编译器，当前Transform需要收集的范围
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否支持增量
     * 通常返回False
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 所有的class收集好以后，会被打包传入此方法
     * @param transformInvocation
     * @throws TransformException* @throws InterruptedException* @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        // 1. 遍历所有的Input
        // 2. 对Input进行二次处理
        // 3. 将Input拷贝到目标目录

        TimeConsumingCollector collector = new TimeConsumingCollector()

        // 遍历所有的输入
        transformInvocation.inputs.each {

            // 把 文件夹 类型的输入，拷贝到目标目录
            it.directoryInputs.each { directoryInput ->
                def destDir = transformInvocation.outputProvider
                        .getContentLocation(
                                directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY)
                collector.collect(directoryInput.file)
                handleFile(directoryInput.file)
                FileUtils.copyDirectory(directoryInput.file, destDir)
            }

            // 把 JAR 类型的输入，拷贝到目标目录
            it.jarInputs.each { jarInput ->
                def dest = transformInvocation.outputProvider
                        .getContentLocation(
                                jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes, Format.JAR)
                collector.collectFromJarFile(jarInput.file)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }


        collector.getMappingClassName().each { file ->

           // def data = TimeConsumingByteCodeGenerator.get(file)

//            File jarFile = transformInvocation.outputProvider.
//                    getContentLocation(
//                            "consumingTime",
//                            getOutputTypes(),
//                            getScopes(),
//                            Format.JAR)
//
//            if (jarFile.getParentFile().exists()) {
//                jarFile.getParentFile().mkdirs()
//            }
//            if (jarFile.exists()) {
//                jarFile.delete()
//            }
//
//            // 将生成的字节码，写入本地文件
//            FileOutputStream fos = new FileOutputStream(jarFile)
//            JarOutputStream jarOutputStream = new JarOutputStream(fos)
//            ZipEntry zipEntry =
//                    new ZipEntry(TimeConsumingByteCodeGenerator.CLASS_NAME + "New"+file.name)
//
//            System.out.println("zipEntry name:" + TimeConsumingByteCodeGenerator.CLASS_NAME + "New"+file.name)
//
//            jarOutputStream.putNextEntry(zipEntry)
//
//
////            println("${getName()}  consumingTimeJarFileBytes = ${TimeConsumingByteCodeGenerator.get()}")
//
//            jarOutputStream.write(
//                    data)
//            jarOutputStream.closeEntry()
//            jarOutputStream.close()
//            fos.close()
        }

    }

    private static final String PACKAGE_NAME = 'me/eric/timeCost'
    private static final String CLASS_NAME_PREFIX = 'MainActivity'
    private static final String CLASS_FILE_SUFFIX = '.class'

    void handleFile(File classFile){
        if (classFile == null || !classFile.exists()) return
        if (classFile.isFile()) {
            if (classFile.absolutePath.contains(PACKAGE_NAME)
                    && classFile.name.startsWith(CLASS_NAME_PREFIX)
                    && classFile.name.endsWith(CLASS_FILE_SUFFIX)) {
                 def data = TimeConsumingByteCodeGenerator.get(classFile)
            }
        } else {
            classFile.listFiles().each {
                handleFile(it)
            }
        }
    }

    void handleJarFile(){

    }

}
