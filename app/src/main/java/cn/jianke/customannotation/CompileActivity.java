package cn.jianke.customannotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import cn.jianke.customannotation.compile.CompileAnnotation;

/**
 * @className: CompileActivity
 * @classDescription: 编译时注解
 * @author: leibing
 * @createTime: 2017/3/2
 */
@CompileAnnotation.CompileContentView(R.id.activity_main)
public class CompileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile);
    }
}
