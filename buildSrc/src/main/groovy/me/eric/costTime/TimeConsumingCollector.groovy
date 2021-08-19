package me.eric.costTime

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 *  收集需要统计方法耗时的类文件
 *  package me.eric.timeCost.sample;
 *  CostMapping_1629366994781
 */
class TimeConsumingCollector {

    private static final String PACKAGE_NAME = 'me/eric/timeCost/sample'
    private static final String CLASS_NAME_PREFIX = 'CostMapping_'
    private static final String CLASS_FILE_SUFFIX = '.class'

    private final Set<File> consumingClassFiles = new HashSet<>()
    private final Set<String> consumingClassNames = new HashSet<>()

    /**
     * 获取收集好的映射表类名
     * @return
     */
    Set<File> getMappingClassFiles() {
        return consumingClassFiles
    }

    Set<File> getMappingClassNames() {
        return consumingClassNames
    }



    /**
     * 收集class文件或者class文件目录中的映射表类。
     * @param classFile
     */
    void collect(File classFile) {
        if (classFile == null || !classFile.exists()) return
        if (classFile.isFile()) {
            if (classFile.absolutePath.contains(PACKAGE_NAME)
                    && classFile.name.startsWith(CLASS_NAME_PREFIX)
                    && classFile.name.endsWith(CLASS_FILE_SUFFIX)) {
                System.out.println("我看看类名1：" + classFile.absolutePath)
                System.out.println("我看看类名1：" + classFile.name)
                // CostMapping_1629390609235.class

                String className =
                        classFile.name.replace(CLASS_FILE_SUFFIX, "")


                consumingClassNames.add(className)
                consumingClassFiles.add(classFile)
            }
        } else {
            classFile.listFiles().each {
                collect(it)
            }
        }
    }

    /**
     * 收集JAR包中的目标类
     * @param jarFile
     */
    void collectFromJarFile(File jarFile) {

        Enumeration enumeration = new JarFile(jarFile).entries()

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
//            mapping/RouterMapping_1628848305294

            if (entryName.contains(PACKAGE_NAME)
                    && entryName.contains(CLASS_NAME_PREFIX)
                    && entryName.contains(CLASS_FILE_SUFFIX)) {

//                me/eric/router/mapping/RouterMapping_1628851816208.class
                String className = entryName
                        .replace(PACKAGE_NAME, "")
                        .replace("/", "")
                        .replace(CLASS_FILE_SUFFIX, "")

                consumingClassNames.add(className)
                consumingClassFiles.add(jarFile)
            }
        }

    }
}






