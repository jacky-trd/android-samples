package com.jikexueyuan.onekeylock;

import android.app.admin.DeviceAdminReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Broadcast Receiver,监听管理权限enabled和disabled广播
 */
public class DeviceManagerBC extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);

        //获得管理权限
        Toast.makeText(context, R.string.got_admin, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);

        //取消管理权限
        Toast.makeText(context, R.string.cancel_admin, Toast.LENGTH_SHORT).show();
    }
}