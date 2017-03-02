package proxytool.api;

import android.view.View;

/**
 * @className: IProxy
 * @classDescription: 代理接口
 * @author: leibing
 * @createTime: 2017/3/2
 */
public interface IProxy<T> {
    /**
     * 注入
     * @param target 所在的类
     * @param root 查找 View 的地方
     */
    void inject(final T target, View root);
}
