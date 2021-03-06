package me.eric.costTime

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class CostTimePlugin implements Plugin<Project> {

    // 实现apply方法，注入插件的逻辑
    void apply(Project project) {

        // 将transform注册到app工程中
        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            // 页面方法耗时统计
            appExtension.registerTransform(new TimeConsumingTransform())
        }

        // 将app的根目录传递过来
        if (project.extensions.findByName("kapt") != null) {
            project.extensions.findByName("kapt").arguments {
                arg("root_project_dir", project.getRootDir().absolutePath)
            }
        }

        // 清理产物
        project.clean.doFirst{
            // 删除上一次的mapping目录
            File file  = new File(project.rootProject.rootDir,"jsonFile")
            if(file.exists()){
                file.deleteDir()
            }
        }

        if (!project.plugins.hasPlugin(AppPlugin)) {
            return
        }

        println("I am from costPlugin, apply from ${project.name}")
        // 将CostExtension注册进来
        project.getExtensions().create("costTime", CostExtension)

    }
}
