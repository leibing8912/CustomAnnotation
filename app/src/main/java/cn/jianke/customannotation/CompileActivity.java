package cn.jianke.customannotation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import proxytool.BindView;
import proxytool.OnClick;
import proxytool.api.ProxyTool;

/**
 * @className: CompileActivity
 * @classDescription: 编译时注解
 * @author: leibing
 * @createTime: 2017/3/2
 */

public class CompileActivity extends Activity {
    @BindView(R.id.tv_compile)
    TextView compileTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile);
        ProxyTool.bind(this);
    }

    @OnClick(R.id.btn_compile)
    void compileOnClick(){
        compileTv.setText("测试编译时OnClick注解");
    }
}
