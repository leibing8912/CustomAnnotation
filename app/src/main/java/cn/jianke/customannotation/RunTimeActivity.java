package cn.jianke.customannotation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.jianke.customannotation.runtime.RunTimeAnnotation;
import cn.jianke.customannotation.runtime.ViewUtils;

/**
 * @className: RunTimeActivity
 * @classDescription: 运行时注解
 * @author: leibing
 * @createTime: 2017/3/2
 */
@RunTimeAnnotation.ContontView(R.layout.activity_run_time)
public class RunTimeActivity extends Activity {
    @RunTimeAnnotation.ViewInject(R.id.tv_run_time)
    private TextView runTimeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 运行时注解绑定
        ViewUtils.injectBind(this);
    }

    @RunTimeAnnotation.OnClick(R.id.btn_run_time)
    public void runTimeOnClick(View view){
        runTimeTv.setText("测试运行时OnClick注解");
    }
}
