package com.jikexueyuan.simpleimagebrowser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 本工程代码对应任务七作业二：编写一个图片浏览器
 *
 * 代码说明：
 * 1，对ImageBrowserActivity的配置如下：
 * <activity android:name=".ImageBrowserActivity">
 *    <intent-filter>
 *       <category android:name="android.intent.category.DEFAULT"/>
 *       <action android:name="android.intent.action.VIEW"/>
 *       <data android:mimeType="image/*"/>
 *    </intent-filter>
 * </activity>
 *
 * 2，Android新版本中对“危险”的permission增加了“runtime permission”的新特性。即使这些permission在manifest
 * 中声明了，在第一次运行的时候，也必须向用户请求权限允许。由于新老Android的行为不一致，本代码中统一
 * 向用户进行权限请求。以下是google中搜索的结果：
 * The new Android has a new feature which is called "runtime permission". For those "dangerous" action,
 * You need to request permissions from users even they have already been in your manifest.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //询问用户，获取读写SD卡的权限
        requestUserPermission();

        //监听点击“Open Image”按钮事件
        findViewById(R.id.btnOpenImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOpenImageClick();
            }
        });
    }

    //询问用户，获取读写SD卡的权限
    private void requestUserPermission(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    getResources().getInteger(R.integer.request_permission));
        }
    }

    //监听点击“Open Image”按钮事件
    private void handleOpenImageClick(){

        //将资源中的图片存储到本地SD卡中
        Bitmap localImage = BitmapFactory.decodeResource(getResources(),R.drawable.little_rat);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory,getString(R.string.target_image));
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            localImage.compress(Bitmap.CompressFormat.JPEG,getResources().getInteger(R.integer.image_quality),outStream);
            try {
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        };

        //利用隐式Intent启动ImageBrowserActivity
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),getString(R.string.intent_type));
        startActivity(intent);
    }
}