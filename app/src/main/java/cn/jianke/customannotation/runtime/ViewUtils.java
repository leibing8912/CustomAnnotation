package cn.jianke.customannotation.runtime;

import android.app.Activity;
import android.view.View;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @className: ViewUtils
 * @classDescription: 运行时View注解工具
 * @author: leibing
 * @createTime: 2017/3/2
 */
public class ViewUtils {

    /**
     * 注解
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param activity 页面实例
     * @return
     */
    public static void injectBind(Activity activity) {
        // 获取activity类
        Class cls = activity.getClass();
        // 页面注解
        if (cls.isAnnotationPresent(RunTimeAnnotation.ContontView.class)) {
            // 获取activity contentView注解接口
            RunTimeAnnotation.ContontView
                    contentView = (RunTimeAnnotation.ContontView)
                    cls.getAnnotation(RunTimeAnnotation.ContontView.class);
            // 得到注解的值
            int layoutId = contentView.value();
            System.out.println("dddddddddddddd layoutId = " +layoutId);
            // 使用反射调用setContentView
            try {
                Method method = cls.getMethod("setContentView", int.class);
                System.out.println("dddddddddddddd method = " +method);
                method.setAccessible(true);
                System.out.println("dddddddddddddd xxxxx");
                method.invoke(activity, layoutId);
                System.out.println("dddddddddddddd yyyyy");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                System.out.println("dddddddddddddd e1 = " +e.getMessage());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("dddddddddddddd e2 = " +e.getMessage());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                System.out.println("dddddddddddddd e3 = " + e.getCause().getMessage());
            }
        }
        // 控件注解
        // 得到activity所有字段
        Field[] fields = cls.getDeclaredFields();
        // 得到被ViewInject注解的字段
        for (Field field : fields) {
            if (field.isAnnotationPresent(RunTimeAnnotation.ViewInject.class)) {
                // 得到字段的ViewInject注解
                RunTimeAnnotation.ViewInject viewInject
                        = field.getAnnotation(RunTimeAnnotation.ViewInject.class);
                // 得到注解的值
                int viewId = viewInject.value();
                // 使用反射调用findViewById，并为字段设置值
                try {
                    Method method = cls.getMethod("findViewById", int.class);
                    method.setAccessible(true);
                    Object resView = method.invoke(activity, viewId);
                    field.setAccessible(true);
                    field.set(activity, resView);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // 点击事件注解
        // 得到Activity的所有方法
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            // 得到被OnClick注解的方法
            if (method.isAnnotationPresent(RunTimeAnnotation.OnClick.class)) {
                // 得到该方法的OnClick注解
                RunTimeAnnotation.OnClick onClick = method.getAnnotation(RunTimeAnnotation.OnClick.class);
                // 得到OnClick注解的值
                int[] viewIds = onClick.value();
                System.out.println("dddddddddddddd 22222222222");
                // 得到OnClick注解上的EventBase注解
                RunTimeAnnotation.EventBase eventBase = onClick.annotationType().getAnnotation(RunTimeAnnotation.EventBase.class);
                System.out.println("dddddddddddddd 33333333333 method = " + method);
                // 得到EventBase注解的值
                String listenerSetter = eventBase.listenerSetter();
                Class<?> listenerType = eventBase.listenerType();
                String methodName = eventBase.methodName();
                System.out.println("dddddddddddddd 44444444444 listenerType = " + listenerType);
                // 使用动态代理
                DynamicHandler handler = new DynamicHandler(activity);
                Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[] { listenerType }, handler);
                handler.addMethod(methodName, method);
                System.out.println("dddddddddddddd 55555555555");
                // 为每个view设置点击事件
                for (int viewId : viewIds) {
                    try {
                        System.out.println("dddddddddddddd 999999999 viewId = " + viewId);
                        Method findViewByIdMethod = cls.getMethod("findViewById", int.class);
                        findViewByIdMethod.setAccessible(true);
                        View view  = (View) findViewByIdMethod.invoke(activity, viewId);
                        Method setEventListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                        setEventListenerMethod.setAccessible(true);
                        setEventListenerMethod.invoke(view, listener);
                    } catch (NoSuchMethodException e) {
                        System.out.println("dddddddddddddd 6666666666 e = " + e.getCause().getMessage());
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.out.println("dddddddddddddd 7777777777 e = " + e.getCause().getMessage());
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        System.out.println("dddddddddddddd 8888888888 e = " + e.getCause().getMessage());
                        e.printStackTrace();
                    }
                }

            }

        }
    }
}
