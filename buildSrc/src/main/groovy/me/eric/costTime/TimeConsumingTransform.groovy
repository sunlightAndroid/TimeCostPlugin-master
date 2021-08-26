package me.eric.costTime

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

import java.lang.reflect.Method
import java.nio.file.attribute.FileTime
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


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
                handleFile(directoryInput.file, collector)
                FileUtils.copyDirectory(directoryInput.file, destDir)
            }

            // 把 JAR 类型的输入，拷贝到目标目录
            it.jarInputs.each { jarInput ->
//                def dest = transformInvocation.outputProvider
//                        .getContentLocation(
//                                jarInput.name,
//                                jarInput.contentTypes,
//                                jarInput.scopes, Format.JAR)
//                collector.collectFromJarFile(jarInput.file)
//                handleJarFile(jarInput.file)
//                FileUtils.copyFile(jarInput.file, dest)


                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
//                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                def md5Name = "_md5name"
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
//                FileUtils.copyFile(jarInput.file, dest)
                weaveJar(jarInput.getFile().getAbsolutePath(), dest.getAbsolutePath())
            }
        }
    }


    void handleFile(File classFile, TimeConsumingCollector collector) {
        if (classFile == null || !classFile.exists()) return
        if (classFile.isFile()) {
            TimeConsumingByteCodeGenerator.get(classFile)
        } else {
            classFile.listFiles().each {
                handleFile(it, collector)
            }
        }
    }

    void handleJarFile(File jarFile) {
        Enumeration enumeration = new JarFile(jarFile).entries()

        // 输出的jar路径
        def outJarFile = new File(jarFile.getParent(), "temp_" + jarFile.name)
        if (outJarFile.exists()) {
            outJarFile.delete()
        }

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()

            if (entryName.endsWith(".class")
                    && !entryName.endsWith("TimeLogger.class")
                    && !entryName.endsWith("TimeCost.class")
                    && entryName.endsWith("AudioActivity.class")
            ) {
                System.out.println(">>>>>>>>>>>>>>jar " + jarFile.getAbsolutePath())
                System.out.println(">>>>>>>>>>>>>>jar " + entryName)
                System.out.println(">>>>>>>>>>>>>>jar " + jarFile.getAbsolutePath() + File.separator + entryName)
                //  def file = new File(jarFile.getAbsolutePath()+ File.separator + entryName)
                def path = "/Users/gechuanguang/Android/giteeWork/TimeCostPlugin-master/biz_video/build/intermediates/runtime_library_classes/debug/me/eric/biz/video/AudioActivity.class";
                def file = new File(path)
                System.out.println(">>>>>>>>>>>>>>jar " + file.exists())

                // 写到一个新的jar文件里面
                //   TimeConsumingByteCodeGenerator.writeJar(entryName,outJarFile)
            }
//                me/eric/router/mapping/RouterMapping_1628851816208.class
        }
    }


    void weaveJar(String inputPath, String outputPath) throws IOException {
        def ZERO = FileTime.fromMillis(0)
        File outputJar = new File(outputPath)
        if (outputJar.exists()) {
            outputJar.delete();
        }

        ZipFile inputZip = new ZipFile(new File(inputPath));
        ZipOutputStream outputZip = new ZipOutputStream(new BufferedOutputStream(java.nio.file.Files.newOutputStream(outputJar.toPath())));
        Enumeration<? extends ZipEntry> inEntries = inputZip.entries();


        while (inEntries.hasMoreElements()) {
            ZipEntry entry = inEntries.nextElement();
            InputStream originalFile = new BufferedInputStream(inputZip.getInputStream(entry));
            ZipEntry outEntry = new ZipEntry(entry.getName());
            byte[] newEntryContent;
            // seperator of entry name is always '/', even in windows
            String className = outEntry.getName().replace("/", ".");

            if (!isWeavableClass(className)) {
                newEntryContent = org.apache.commons.io.IOUtils.toByteArray(originalFile);
            } else {
                newEntryContent = TimeConsumingByteCodeGenerator.writeJar(originalFile)
            }

            System.out.println(">>>>>>>>>>>>>>jar字节大小 " + newEntryContent.length)
            if (newEntryContent == null || newEntryContent.length <= 0) {
                continue
            }

            CRC32 crc32 = new CRC32()
            crc32.update(newEntryContent)
            outEntry.setCrc(crc32.getValue())
            outEntry.setMethod(ZipEntry.STORED)
            outEntry.setSize(newEntryContent.length)
            outEntry.setCompressedSize(newEntryContent.length)
            outEntry.setLastAccessTime(ZERO)
            outEntry.setLastModifiedTime(ZERO)
            outEntry.setCreationTime(ZERO)
            outputZip.putNextEntry(outEntry)
            outputZip.write(newEntryContent)
            outputZip.closeEntry()
        }
        outputZip.flush()
        outputZip.close()
        inputZip.close()
    }

    static boolean isWeavableClass(String fullQualifiedClassName) {
        return fullQualifiedClassName.endsWith(".class") &&!fullQualifiedClassName.contains("R\$")  && !fullQualifiedClassName.contains("R.class") && !fullQualifiedClassName.contains("BuildConfig.class")
    }

}

