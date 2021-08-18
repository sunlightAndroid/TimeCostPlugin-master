package me.eric.cost_processor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

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

        // 生成json文件
        // 生成类映射关系文件
        JsonArray jsonArray = new JsonArray();

        Set<? extends Element> elements= roundEnvironment.getElementsAnnotatedWith(TimeCost.class);
        System.out.println(TAG + "被标记的注解有：" + elements.size());
        for (Element element : elements){
            TypeElement parentElement = (TypeElement) element.getEnclosingElement();
            String  parentClassName = parentElement.getSimpleName().toString();
            String  parentFullPath = parentElement.getQualifiedName().toString();
            TimeCost annotation = element.getAnnotation(TimeCost.class);
            String description = annotation.description();
            String methodName = element.getSimpleName().toString();
            System.out.println(TAG + " 注解解析>>> " +   "   " + parentFullPath);
            System.out.println(TAG + " 注解解析>>> " +   "   " + parentClassName);
            System.out.println(TAG + " 注解解析>>> " +   "   " + methodName);
            System.out.println(TAG + " 注解解析>>> " +   "   " + description);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("path",parentFullPath);
            jsonObject.addProperty("name",methodName);
            jsonObject.addProperty("description",description);
            jsonArray.add(jsonObject);
        }

        System.out.println(TAG + " 生成的json文件>>> " +   "   " + jsonArray.toString());

        File dirFile = new File(root_project_dir,"jsonFile");
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }

        File itemFile = new File(dirFile,System.currentTimeMillis() + ".json");
        try {
            FileWriter writer = new FileWriter(itemFile);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(jsonArray.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
           throw new  RuntimeException("Error while create json file",e);
        }



        // 写入自动生成的类到本地文件中
//        try {
//            String mappingFullClassName = "me.eric.router." + className;
//
//            System.out.println(TAG + " className  ： " + className);
//            System.out.println(TAG + " package name ： " + mappingFullClassName);
//
//            JavaFileObject source = processingEnv.getFiler()
//                    .createSourceFile(mappingFullClassName);
//            Writer writer = source.openWriter();
//            writer.write(builder.toString());
//            writer.flush();
//            writer.close();
//        } catch (Exception ex) {
//            throw new RuntimeException("Error while create file", ex);
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
