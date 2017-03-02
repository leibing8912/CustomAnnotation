package proxytool.compiler;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import proxytool.OnClick;
import proxytool.BindView;
import proxytool.compiler.model.FieldViewBinding;
import proxytool.compiler.model.MethodViewBinding;
import proxytool.compiler.model.ProxyClass;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;

/**
 * @className: ProxyToolProcessor
 * @classDescription: 注解处理器
 * @author: leibing
 * @createTime: 2017/3/2
 */
@AutoService(Processor.class)
public class ProxyToolProcessor extends AbstractProcessor {
    // 文件相关的辅助类
    private Filer mFiler;
    // 元素相关的辅助类
    private Elements mElementUtils;
    // 日志相关的辅助类
    private Messager mMessager;
    private Map<String, ProxyClass> mProxyClassMap = new HashMap<>();

    /**
     * 处理器的初始化方法，可以获取相关的工具类
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param processingEnv 文件相关的辅助类
     * @return
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    /**
     * 处理器的主方法，用于扫描处理注解，生成java文件
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 处理被ViewById注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
           if (!isValid(BindView.class, "fields", element)) {
                return true;
            }
            parseViewById(element);
        }
        // 处理被Click注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)) {
            if (!isValid(OnClick.class, "methods", element)) {
                return true;
            }
            try {
                parseClick(element);
            } catch (IllegalArgumentException e) {
                error(element, e.getMessage());
                return true;
            }
        }
        // 为每个宿主类生成所对应的代理类
        for (ProxyClass proxyClass_ : mProxyClassMap.values()) {
            try {
                proxyClass_.generateProxy().writeTo(mFiler);
            } catch (IOException e) {
                error(null, e.getMessage());
            }
        }
        mProxyClassMap.clear();
        return true;
    }

  /**
   * 处理BindView注解
   * @author leibing
   * @createTime 2017/3/2
   * @lastModify 2017/3/2
   * @param element 元素
   * @return
   */
    private void parseViewById(Element element) {
        ProxyClass proxyClass = getProxyClass(element);
        // 把被注解的view对象封装成一个model，放入代理类的集合中
        FieldViewBinding bindView = new FieldViewBinding(element);
        proxyClass.add(bindView);
    }

    /**
     * 处理OnClick注解
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param element 元素
     * @return
     */
    private void parseClick(Element element) throws IllegalArgumentException {
        ProxyClass proxyClass = getProxyClass(element);
        // 把被注解的view对象封装成一个model，放入代理类的集合中
        MethodViewBinding bindView = new MethodViewBinding(element);
        proxyClass.add(bindView);
    }

    /**
     * 生成或获取注解元素所对应的ProxyClass类
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param element 元素
     * @return
     */
    private ProxyClass getProxyClass(Element element) {
        // 被注解的变量所在的类
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = classElement.getQualifiedName().toString();
        ProxyClass proxyClass = mProxyClassMap.get(qualifiedName);
        if (proxyClass == null) {
            // 生成每个宿主类所对应的代理类，后面用于生产java文件
            proxyClass = new ProxyClass(classElement, mElementUtils);
            mProxyClassMap.put(qualifiedName, proxyClass);
        }
        return proxyClass;
    }

    /**
     * 注解是否有效
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param annotationClass
     * @param targetThing
     * @param element 元素
     * @return
     */
    private boolean isValid(Class<? extends Annotation> annotationClass, String targetThing, Element element) {
        boolean isVaild = true;
        // 获取变量的所在的父元素，肯能是类、接口、枚举
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        // 父元素的全限定名
        String qualifiedName = enclosingElement.getQualifiedName().toString();
        // 所在的类不能是private或static修饰
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }
        // 父元素必须是类，而不能是接口或枚举
        if (enclosingElement.getKind() != ElementKind.CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }
        // 不能在Android框架层注解
        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }
        // 不能在java框架层注解
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }

        return isVaild;
    }

    /**
     * 指定哪些注解应该被注解处理器注册
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getName());
        types.add(OnClick.class.getName());
        return types;
    }

    /**
     * 用来指定你使用的 java 版本
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 错误打印
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param e
     * @param msg
     * @param args
     * @return
     */
    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
