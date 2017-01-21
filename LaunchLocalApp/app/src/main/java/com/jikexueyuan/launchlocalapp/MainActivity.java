package com.jikexueyuan.launchlocalapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * 本工程代码对应任务七作业一：通过浏览器页面打开本机应用
 * 要求：通过浏览器页面打开本机应用
 * 思路：通过隐式Intent
 *
 * 解决方案：
 * 在AndroidManifest.xml中对LocalAppActivity进行如下配置，从而达到题目要求。
 * <activity android:name=".LocalAppActivity">
 *     <intent-filter>
 *        <category android:name="android.intent.category.BROWSABLE"/>
 *        <category android:name="android.intent.category.DEFAULT"/>
 *        <action android:name="android.intent.action.VIEW"/>
 *        <data android:scheme="local"/>
 *     </intent-filter>
 * </activity>
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStartAnotherAty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //添加try/catch防止无法启动activity时程序崩溃
                try{
                    //Action:"android.intent.action.VIEW"
                    //Uri:"local://example.app"
                    startActivity(new Intent(LocalAppActivity.ACTION, Uri.parse(getString(R.string.uri))));
                    //startActivity(new Intent(LocalAppActivity.ACTION));
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,getString(R.string.error),Toast.LENGTH_SHORT);
                }
            }
        });
    }
}