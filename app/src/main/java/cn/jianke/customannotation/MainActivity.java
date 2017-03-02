package cn.jianke.customannotation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @className: MainActivity
 * @classDescription: 首页
 * @author: leibing
 * @createTime: 2017/3/2
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // onClick
        findViewById(R.id.btn_compile).setOnClickListener(this);
        findViewById(R.id.btn_runtime).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_compile:
                // 编译时注解
                turnToNewPage(CompileActivity.class);
                break;
            case R.id.btn_runtime:
                // 运行时注解
                turnToNewPage(RunTimeActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到新页面
     * @author leibing
     * @createTime 2017/3/2
     * @lastModify 2017/3/2
     * @param newPageCls 新页面类
     * @return
     */
    private void turnToNewPage(Class newPageCls){
        Intent intent = new Intent();
        intent.setClass(this, newPageCls);
        startActivity(intent);
    }
}
