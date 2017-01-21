package com.jikexueyuan.callandroidapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
*任务十五作业一：用C++使用Android Log API输出日志
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //输出日志
        JniBridge.showLog();
    }
}
