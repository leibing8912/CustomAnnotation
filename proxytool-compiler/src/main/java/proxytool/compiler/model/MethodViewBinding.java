package proxytool.compiler.model;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import proxytool.OnClick;

/**
 * @className: MethodViewBinding
 * @classDescription: 方法绑定
 * @author: leibing
 * @createTime: 2017/3/2
 */
public class MethodViewBinding {
    // 注解元素
    private ExecutableElement mElement;
    // 方法名
    private String mMethodName;
    // id集合
    private int[] mIds;
    // 参数名
    private String mParameterName;

    private boolean mParameterExit;

    /**
     * Constructor
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param element 注解元素
     * @return
     */
    public MethodViewBinding(Element element) throws IllegalArgumentException {
        // 注解元素
        mElement = (ExecutableElement) element;
        // 点击事件注解
        OnClick click = element.getAnnotation(OnClick.class);
        // id集合
        mIds = click.value();
        // 方法名
        mMethodName = element.getSimpleName().toString();
        // 参数
        List<? extends VariableElement> parameters = mElement.getParameters();
        if (parameters.size() > 1) {
            // 参数不能超过1个
            throw new IllegalArgumentException(
                    String.format("The method annotated with @%s must less two parameters", OnClick.class.getSimpleName()));
        }

        if (parameters.size() == 1) {
            // 如果有参数必须是View类型
            VariableElement variableElement = parameters.get(0);
            if (!variableElement.asType().toString().equals(ProxyClass.VIEW.toString())) {
                throw new IllegalArgumentException(
                        String.format("The method parameter must be %s type", ProxyClass.VIEW.toString()));
            }
            mParameterExit = true;
            mParameterName = variableElement.getSimpleName().toString();
        }
    }
    /**----------------------------- get and set-------------------------------------------------
     ------------------------------------------------------------------------------------------*/
    public int[] getIds() {
        return mIds;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public boolean isParameterExit() {
        return mParameterExit;
    }

    public String getParameterName() {
        return mParameterName;
    }
}
