package com.jikexueyuan.startanotheractivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * 本工程代码对应任务七作业三：编写一个应用要求特定权限才能启动
 *
 * 说明：通过配置以下权限即可启动另外一个activity
 * <uses-permission android:name="com.jikexueyuan.activitytobestarted.permission.ActivityToBeStarted"/>
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStartAnotherActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //启动另外一个activity
                    startActivity(new Intent(getString(R.string.start_activity_action)));
                }catch (SecurityException e){
                    //如果出错则提示没有权限
                    Toast.makeText(MainActivity.this,getString(R.string.error),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}