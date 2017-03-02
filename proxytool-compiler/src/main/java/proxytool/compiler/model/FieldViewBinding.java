package proxytool.compiler.model;


import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import proxytool.BindView;

/**
 * @className: FieldViewBinding
 * @classDescription: view绑定
 * @author: leibing
 * @createTime: 2017/3/2
 */
public class FieldViewBinding {
    // 注解元素
    private VariableElement mElement;
    // 资源id
    private int mResId;
    // 变量名
    private String mVariableName;
    // 变量类型
    private TypeMirror mTypeMirror;

    /**
     * Constructor
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param element 注解元素
     * @return
     */
    public FieldViewBinding(Element element) {
        mElement = (VariableElement) element;
        BindView bindView = element.getAnnotation(BindView.class);
        // 资源id
        mResId = bindView.value();
        // 变量名
        mVariableName = element.getSimpleName().toString();
        // 变量类型
        mTypeMirror = element.asType();
    }

    /**----------------------------- get and set-------------------------------------------------
       ------------------------------------------------------------------------------------------*/
    public VariableElement getElement() {
        return mElement;
    }

    public int getResId() {
        return mResId;
    }

    public String getVariableName() {
        return mVariableName;
    }

    public TypeMirror getTypeMirror() {
        return mTypeMirror;
    }
}
