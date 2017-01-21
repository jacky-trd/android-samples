package com.jikexueyuan.remindernotebook;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * 该类为Broadcase Receiver,负责响应闹钟事件
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final int NOTIFICATON_ID = 1200;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //显示Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.notification_title));
        builder.setContentText(intent.getStringExtra(context.getString(R.string.notification_content_tag)));
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATON_ID,notification);
    }
}
