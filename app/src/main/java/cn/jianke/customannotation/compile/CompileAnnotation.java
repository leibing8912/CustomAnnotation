package cn.jianke.customannotation.compile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @className: CompileAnnotation
 * @classDescription: 编译时注解
 * @author: leibing
 * @createTime: 2017/3/2
 */
public class CompileAnnotation {

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE})
    public @interface CompileContentView{
        int value();
    }
}
