package me.eric.cost_processor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import me.eric.cost_annotation.TimeCost;


/**
 * <pre>
 *     author : eric
 *     time   : 2021/08/07
 *     desc   : 注解处理器
 *     version:
 * </pre>
 */
@AutoService(Processor.class)
public class TimeCostProcessor extends AbstractProcessor {

    private static final String TAG = "TimeCostProcessor";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 避免多次调用 process
        if (roundEnvironment.processingOver()) {
            return false;
        }
        System.out.println(TAG + " >>> process start ...");

        // 获取kapt传递过来的参数
        String root_project_dir = processingEnv.getOptions().get("root_project_dir");
        System.out.println(TAG + " root_project_dir>>> " + root_project_dir);


        // 生成类映射关系文件
        String packageName = "me.eric.timeCost.sample";
        String className = "CostMapping_" + System.currentTimeMillis() ;
        StringBuilder builder = new StringBuilder();
        builder.append("package " + packageName + ";").append("\n\n");
        builder.append("import java.util.HashMap;").append("\n");
        builder.append("import java.util.Map;").append("\n\n");
        builder.append("public class ").append(className).append("{").append("\n\n");
        builder.append("    public static Map<String, String> get() {").append("\n\n");
        builder.append("        Map<String, String> map = new HashMap<>();").append("\n\n");

        // 生成json文件
        JsonArray jsonArray = new JsonArray();

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(TimeCost.class);
        
        if (elements.size() < 1) return false;

        System.out.println(TAG + "被标记的注解有：" + elements.size());
        for (Element element : elements) {
            TypeElement parentElement = (TypeElement) element.getEnclosingElement();
            String parentClassName = parentElement.getSimpleName().toString();
            String parentFullPath = parentElement.getQualifiedName().toString();
            TimeCost annotation = element.getAnnotation(TimeCost.class);
            String description = annotation.description();
            String methodName = element.getSimpleName().toString();
            System.out.println(TAG + " 注解解析>>> " + "   " + parentFullPath);
            System.out.println(TAG + " 注解解析>>> " + "   " + parentClassName);
            System.out.println(TAG + " 注解解析>>> " + "   " + methodName);
            System.out.println(TAG + " 注解解析>>> " + "   " + description);

            // 生成类文件
            builder.append("        map.put(\"" + parentFullPath + "\", \"" + methodName + "\");").append("\n");

            // 生成json文件
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("path", parentFullPath);
            jsonObject.addProperty("name", methodName);
            jsonObject.addProperty("description", description);
            jsonArray.add(jsonObject);
        }
        builder.append("        return map;").append("\n");
        builder.append("    }").append("\n");
        builder.append("}");
        System.out.println(TAG + " 生成的类文件>>> " + "   " + builder.toString());

        // 将类文件写入本地
        try {
            String fullClassName = packageName + "." + className;
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(fullClassName);
            Writer writer = sourceFile.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println(TAG + " 生成的json文件>>> " + "   " + jsonArray.toString());

        File dirFile = new File(root_project_dir, "jsonFile");
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File itemFile = new File(dirFile, System.currentTimeMillis() + ".json");
        try {
            FileWriter writer = new FileWriter(itemFile);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(jsonArray.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException("Error while create json file", e);
        }

        // 生成一个TimeLogger类
//        StringBuilder logBuilder = new StringBuilder();
//        logBuilder.append("package me.eric.timeCost.log;").append("\n");
//        logBuilder.append("public class TimeLogger {").append("\n");
//        logBuilder.append("    public static long time = 0;").append("\n");
//        logBuilder.append("    public static void start(String name) {").append("\n");
//        logBuilder.append("        System.out.println(\"<<<<<<<<<<<<<<<<<<<<<start\");").append("\n");
//        logBuilder.append("        time = System.currentTimeMillis();").append("\n");
//        logBuilder.append("        System.out.println(\"方法名：\" + name);").append("\n");
//        logBuilder.append("    }").append("\n\n");
//
//        logBuilder.append("    public static void end() {").append("\n");
//        logBuilder.append("        time = System.currentTimeMillis() - time;").append("\n");
//        logBuilder.append("        System.out.println(\"方法耗时：\" + time + \" ms\");").append("\n");
//        logBuilder.append("        System.out.println(\">>>>>>>>>>>>>>>>>>>>>>>end\");").append("\n");
//        logBuilder.append("    }").append("\n");
//        logBuilder.append("}").append("\n");
//        System.out.println("logBuilder:" + logBuilder.toString());
//
//        // 生成日志类文件到本地
//        try {
//            System.out.println("=================执行了几次================");
//            String fullLogClassName = "me.eric.timeCost.log.TimeLogger";
//            JavaFileObject logFile = processingEnv.getFiler()
//                    .createSourceFile(fullLogClassName);
//
//            Writer writer = logFile.openWriter();
//            writer.write(logBuilder.toString());
//            writer.flush();
//            writer.close();
//        } catch (Throwable ex) {
//            throw new RuntimeException("Error while create TimeLogger file", ex);
//        }
        return false;
    }

    /**
     * 告诉编译器，当前处理器支持的注解类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(
                TimeCost.class.getCanonicalName()
        );
    }
}
