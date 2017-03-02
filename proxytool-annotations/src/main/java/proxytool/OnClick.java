package proxytool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @interfaceName: OnClick
 * @interfaceDescription: 点击事件注解
 * @author: leibing
 * @createTime: 2017/3/2
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OnClick {
   int[] value();
}
