package com.jikexueyuan.taxibookingclient;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登陆界面Activity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //查询权限的request code
    private static final int PERMISION_REQUEST_CODE = 103 ;
    //电话号码和用户名的EditText
    private EditText phoneEtv, nameEtv;
    //登陆按钮
    private Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Android SDK 23之后，google增加了“runtime permission”，
        //对于“危险”操作，即使在maifests里声明，在首次运行的时候也必须向用户申请权限
        //此处检查各项权限，如果没有权限需要向用户申请
        requestPermition();
        //初始化
        init();
    }

    //此处检查各项权限，如果没有权限需要向用户申请
    private void requestPermition(){
        //如果SDK版本小于23则没有runtime permission
        if (Build.VERSION.SDK_INT >= 23){
            if ((this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    || (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    || (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    || (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    || (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    || (this.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)){
                //向用户申请权限
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CALL_PHONE},
                        PERMISION_REQUEST_CODE);
            }
        }
    }

    //申请权限的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISION_REQUEST_CODE) {
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    //用户不同意
                    System.err.println(">>>>>>>>>>>>"+permissions[i]+" not granted "+ grantResults[i]);
                    Toast.makeText(MainActivity.this, R.string.permission_hint, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //用户同意
                    System.out.println(">>>>>>>>>>>>"+permissions[i]+" granted "+ grantResults[i]);
                }
            }
        }
    }

    //初始化
    private void init(){
        //控件初始化
        phoneEtv = (EditText) findViewById(R.id.phonenumber_edittext);
        nameEtv = (EditText) findViewById(R.id.nikename_edittext);
        loginbtn = (Button) findViewById(R.id.login_button);
        loginbtn.setOnClickListener(this);

        //获取默认的电话号码和昵称
        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        String phonenumber = info.getString("phonenumber", "");
        String username = info.getString("username", "");
        phoneEtv.setText(phonenumber);
        nameEtv.setText(username);
    }

    //监听响应函数
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        if (!TextUtils.isEmpty(phoneEtv.getText().toString())) {
            if (!TextUtils.isEmpty(nameEtv.getText().toString())) {
                //将当前电话号码和昵称存为默认
                SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor edit = info.edit();
                edit.putString("phonenumber", phoneEtv.getText().toString());
                edit.putString("username", nameEtv.getText().toString());

                //启动地图预约界面
                intent.putExtra("phonenumber", phoneEtv.getText().toString());
                intent.putExtra("username", nameEtv.getText().toString());
                startActivity(intent);
            } else {
                //没有用户名
                Toast.makeText(MainActivity.this, R.string.nousername, Toast.LENGTH_SHORT).show();
            }
        } else {
            //没有电话号码
            Toast.makeText(MainActivity.this, R.string.nophonenumber, Toast.LENGTH_SHORT).show();
        }
    }
}
