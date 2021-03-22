package cn.xdf.libnavcompiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import cn.xdf.libnavannotation.ActivityDestination;
import cn.xdf.libnavannotation.FragmentDestination;

/**
 * author:fumm
 * Date : 2021/ 03/ 18 4:46 PM
 * Dec : 自定义注解处理器
 * APP页面导航信息收集注解处理器
 * <p>
 * AutoService注解：就这么一标记，annotationProcessor  project()应用一下,编译时就能自动执行该类了。
 * <p>
 * SupportedSourceVersion注解:声明我们所支持的jdk版本
 * <p>
 * SupportedAnnotationTypes:声明该注解处理器想要处理那些注解
 **/

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"cn.xdf.libnavannotation.ActivityDestination", "cn.xdf.libnavannotation.FragmentDestination"})
public class NavProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    // 保存文件名称
    private static final String OUTPUT_FILE_NAME = "destination.json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 日志打印,在java环境下不能使用android.util.log.e()
        messager = processingEnvironment.getMessager();
        // 文件处理工具
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //通过处理器环境上下文roundEnv分别获取 项目中标记的FragmentDestination.class 和ActivityDestination.class注解。
        //此目的就是为了收集项目中哪些类 被注解标记了
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);
        Set<? extends Element> fragmentElements = roundEnvironment.getElementsAnnotatedWith(FragmentDestination.class);

        if (!activityElements.isEmpty() || !fragmentElements.isEmpty()) {

            // 此方法就是解析 注解，获取注解元素存放在destMap中
            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(activityElements, ActivityDestination.class, destMap);
            handleDestination(fragmentElements, FragmentDestination.class, destMap);
            saveDestMapAsJsonFile(destMap);
        }


        return true;
    }

    /**
     * 将destMap 中的数据，作为json文件保存到 assets 目录下
     * //app/src/main/assets
     */
    private void saveDestMapAsJsonFile(HashMap<String, JSONObject> destMap) {
        //app/src/main/assets
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;


        String assetsPath = getAssetsPath();
        try {
            File file = new File(assetsPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            // 开始写入
            File outputFile = new File(file, OUTPUT_FILE_NAME);
            if (outputFile.exists()) {
                outputFile.delete();
            }

            outputFile.createNewFile();

            // 利用fastjson 把收集到的所有的页面信息，转换长json格式。并输出到文件中
            String content = JSON.toJSONString(destMap);
            fos = new FileOutputStream(outputFile);
            writer = new OutputStreamWriter(fos, "UTF-8");
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关流
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 获取 /app/src/main/assets/的路径
    private String getAssetsPath() {
        //filer.createResource()意思是创建源文件
        //我们可以指定为class文件输出的地方，
        //StandardLocation.CLASS_OUTPUT：java文件生成class文件的位置，/app/build/intermediates/javac/debug/classes/目录下
        //StandardLocation.SOURCE_OUTPUT：java文件的位置，一般在/ppjoke/app/build/generated/source/apt/目录下
        //StandardLocation.CLASS_PATH 和 StandardLocation.SOURCE_PATH用的不多，指的了这个参数，就要指定生成文件的pkg包名了
        FileObject resource = null;
        String appPath = "";
        try {
            resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
            String resourcePath = resource.toUri().getPath();
            messager.printMessage(Diagnostic.Kind.NOTE, "resourcePath:" + resourcePath);

            //由于我们想要把json文件生成在app/src/main/assets/目录下,所以这里可以对字符串做一个截取，
            //以此便能准确获取项目在每个电脑上的 /app/src/main/assets/的路径
            appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String assetsPath = appPath + "src/main/assets";
        return assetsPath;
    }

    /**
     * 此方法就是解析 注解，获取注解元素存放在destMap中
     *
     * @param elements       elements
     * @param annotationClaz annotationClaz
     * @param destMap        destMap
     */
    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotationClaz,
                                   HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            //TypeElement是Element的一种。
            //如果我们的注解标记在了类名上。所以可以直接强转一下。使用它得到全类名
            TypeElement typeElement = (TypeElement) element;

            // 全类名com.mooc.ppjoke.home
            String clazName = typeElement.getQualifiedName().toString();
            messager.printMessage(Diagnostic.Kind.OTHER, "clazName:" + clazName);

            // 页面的id.此处不能重复,使用页面的类名做hascode即可
            int id = Math.abs(clazName.hashCode());

            // 页面的pageUrl相当于隐士跳转意图中的host://schem/path格式
            String pageUrl = null;
            // 是否需要登录
            boolean needLogin = false;
            // 是否作为首页的第一个展示页面
            boolean asStarter = false;
            // 标记该页面fragment 是 activity
            boolean isFragment = false;
            Annotation annotation = element.getAnnotation(annotationClaz);

            if (annotation instanceof FragmentDestination) {
                FragmentDestination dest = (FragmentDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragment = true;
            } else if (annotation instanceof ActivityDestination) {
                ActivityDestination dest = (ActivityDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragment = false;
            }

            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl：" + clazName);
            } else {
                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("needLogin", needLogin);
                object.put("asStarter", asStarter);
                object.put("pageUrl", pageUrl);
                object.put("className", clazName);
                object.put("isFragment", isFragment);
                destMap.put(pageUrl, object);
            }
        }

    }

}
