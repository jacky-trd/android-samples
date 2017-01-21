package com.jikexueyuan.onekeylock;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 直接锁屏的activity，如果失败则启动另一个申请权限的Activity
 */
public class OneKeyLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        try {
            //锁屏
            devicePolicyManager.lockNow();
            finish();
        } catch (Exception e) {
            //如果没有权限，则启动申请权限的Activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}