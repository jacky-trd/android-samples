package com.jikexueyuan.activitytobestarted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 本工程代码对应任务七作业三：编写一个应用要求特定权限才能启动
 *
 * 说明：没有代码编写，仅仅在manifest中对ToBeStartedActivity做了权限配置
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}