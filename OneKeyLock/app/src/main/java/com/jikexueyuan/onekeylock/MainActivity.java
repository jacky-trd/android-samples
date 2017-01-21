package com.jikexueyuan.onekeylock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 任务十一作业五：一键锁屏
 * 要求：一个应用进行锁屏配置，另一个应用可以进行一键锁屏功能，如果没有权限进入第一个应用进行配置
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //获取权限按钮，取消权限按钮，锁屏按钮
    private Button btnAdmin;
    private Button btnCancel;
    private Button btnLock;

    //设备管理器
    private DevicePolicyManager devicePolicyManager;

    //申请设备管理权限时的request code
    private static final int DEVICE_POLICY_MANAGER_REQUEST_CODE = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取按钮
        btnAdmin = (Button) findViewById(R.id.btnAdmin);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnLock = (Button) findViewById(R.id.btnLock);

        //监听按钮点击事件
        btnAdmin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnLock.setOnClickListener(this);

        //获取设备管理器，同时检查是否具有管理权限，根据当前权限设置按钮可见样式
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        if (devicePolicyManager.isAdminActive(new ComponentName(this, DeviceManagerBC.class))) {
            showAdminPage();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdmin:
                //获取权限
                Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(this, DeviceManagerBC.class));
                startActivityForResult(i, DEVICE_POLICY_MANAGER_REQUEST_CODE);
                break;
            case R.id.btnCancel:
                //取消权限
                devicePolicyManager.removeActiveAdmin(new ComponentName(this, DeviceManagerBC.class));
                //取消权限后，改变按钮可见样式
                showNoAdminPage();
                break;
            case R.id.btnLock:
                //锁屏
                devicePolicyManager.lockNow();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case RESULT_OK:
                //获取管理权限成功，改变按钮可见样式
                showAdminPage();
                break;
            case RESULT_CANCELED:
                break;
        }
    }

    /*拥有管理权限时的按钮可见样式*/
    public void showAdminPage() {
        btnAdmin.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        btnLock.setVisibility(View.VISIBLE);
    }

    /*没有管理权限时的按钮可见样式*/
    public void showNoAdminPage() {
        btnAdmin.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);
        btnLock.setVisibility(View.INVISIBLE);
    }
}
