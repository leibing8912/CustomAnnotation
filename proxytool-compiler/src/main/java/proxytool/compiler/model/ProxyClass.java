package proxytool.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @className: ProxyClass
 * @classDescription: 代理类
 * @author: leibing
 * @createTime: 2017/3/2
 */
public class ProxyClass {
    // proxytool.IProxy
    public static final ClassName IPROXY = ClassName.get("proxytool.api", "IProxy");
    // android.view.View
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    // android.view.View.OnClickListener
    public static final ClassName VIEW_ON_CLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    // 生成代理类的后缀名
    public static final String SUFFIX = "$$Proxy";
    // 类元素
    public TypeElement mTypeElement;
    // 元素相关的辅助类
    private Elements mElementUtils;
    // FieldViewBinding类型的集合
    private Set<FieldViewBinding> bindViews = new HashSet<>();
    // MethodViewBinding类型的集合
    public Set<MethodViewBinding> mMethods = new HashSet<>();

    /**
     * Constructor
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param mTypeElement 类元素
     * @param mElementUtils   元素相关的辅助类
     * @return
     */
    public ProxyClass(TypeElement mTypeElement, Elements mElementUtils) {
        this.mTypeElement = mTypeElement;
        this.mElementUtils = mElementUtils;
    }

    /**
     * 添加view绑定数据
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param bindView view绑定数据
     * @return
     */
    public void add(FieldViewBinding bindView) {
        bindViews.add(bindView);
    }

    /**
     * 添加方法绑定数据
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param methodViewBinding 方法绑定数据
     * @return
     */
    public void add(MethodViewBinding methodViewBinding) {
        mMethods.add(methodViewBinding);
    }

    /**
     * 用于生成代理类
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param
     * @return
     */
    public JavaFile generateProxy() {
        // 生成public void inject(final T target, View root)方法
        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(mTypeElement.asType()), "target", Modifier.FINAL)
                .addParameter(VIEW, "root");
        // 在inject方法中，添加我们的findViewById逻辑
        for (FieldViewBinding model : bindViews) {
            // find views
            injectMethodBuilder.addStatement("target.$N = ($T)(root.findViewById($L))", model.getVariableName(),
                    ClassName.get(model.getTypeMirror()), model.getResId());
        }

        if (mMethods.size() > 0) {
            injectMethodBuilder.addStatement("$T listener", VIEW_ON_CLICK_LISTENER);
        }

        for (MethodViewBinding method : mMethods) {
            // declare OnClickListener anonymous class
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(VIEW_ON_CLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(VIEW, "view")
                            .addStatement("target.$N($L)", method.getMethodName(), method.isParameterExit() ? method.getParameterName() : "")
                            .build())
                    .build();
            injectMethodBuilder.addStatement("listener = $L ", listener);
            for (int id : method.getIds()) {
                // set listeners
                injectMethodBuilder.addStatement("(root.findViewById($L)).setOnClickListener(listener)", id);
            }
        }

        // 添加以$$Proxy为后缀的类
        TypeSpec finderClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                //添加父接口
                .addSuperinterface(ParameterizedTypeName.get(IPROXY, TypeName.get(mTypeElement.asType())))
                .addMethod(injectMethodBuilder.build())
                .build();
        //添加包名
        String packageName = mElementUtils.getPackageOf(mTypeElement).getQualifiedName().toString();
        //生成file文件
        return JavaFile.builder(packageName, finderClass).build();
    }
}
