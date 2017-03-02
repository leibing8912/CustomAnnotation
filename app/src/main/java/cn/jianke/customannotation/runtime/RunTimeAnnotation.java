package cn.jianke.customannotation.runtime;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @className: RunTimeAnnotation
 * @classDescription: 运行时注解
 * @author: leibing
 * @createTime: 2017/3/2
 */
public class RunTimeAnnotation {

    // 运行时布局注解
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface ContontView {
        int value();
    }

    // 运行时控件注解
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface ViewInject{
        int value();
    }

    // 运行时自定义注解辅助Click事件
    @Target(ElementType.ANNOTATION_TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EventBase {
        Class listenerType();
        String listenerSetter();
        String methodName();
    }

    // 运行时点击事件注解
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @EventBase(listenerType = View.OnClickListener.class,
            listenerSetter = "setOnClickListener",
            methodName = "onClick")
    public @interface OnClick {
        int[] value();
    }
}
