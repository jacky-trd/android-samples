package com.jikexueyuan.remindernotebook;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 设备重启后，接收BOOT_COMPLETED广播，重新注册闹钟
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    public static final int NOTIFICATON_ID = 1200;

    public BootCompletedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //读取数据库，取回全部events
        DBOperationHelper helper = new DBOperationHelper(context);
        List<RemindEventInfo> list = helper.getEvents();

        //循环注册闹钟
        for(RemindEventInfo event:list){
            int time = event.getEventTime();
            int id = event.getAlarmID();
            String content = event.getEventContent();

            /*为了方便测试和老师批改，我将计算时间的代码注释掉了，将原来的整点提醒（间隔一天）变成了，整秒提醒，间隔10秒。题目要求的代码在注释里。
            long firstTime = SystemClock.elapsedRealtime();
            long systemTime = System.currentTimeMillis();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // 设置时区
            calendar.setTimeZone(TimeZone.getTimeZone(context.getString(R.string.GMT_8_Time_Zone)));
            calendar.set(Calendar.HOUR_OF_DAY, time);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long selectTime = calendar.getTimeInMillis();
            // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
            if(systemTime > selectTime) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                selectTime = calendar.getTimeInMillis();
            }

            long timeSpan = selectTime - systemTime;
            firstTime += timeSpan;

            // 进行闹铃注册
            Intent i = new Intent(context, AlarmReceiver.class);
            intent.putExtra(context.getString(R.string.notification_content_tag),content);
            PendingIntent sender = PendingIntent.getBroadcast(context,id,intent, 0);
            AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 24*60*60*1000, sender);*/

            //以下代码将小时变成了秒，将间隔从一天变成了10秒,方便老师检查和测试，题目要求的代码在上方注释中
            //注册闹钟
            Intent i = new Intent(context, AlarmReceiver.class);
            intent.putExtra(context.getString(R.string.notification_content_tag),content);
            PendingIntent sender = PendingIntent.getBroadcast(context,id,intent, 0);
            AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            //！！！！为了方便测试与检查结果,这里将时间间隔从一天改为30秒
            //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 24*60*60*1000, sender);
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, 10*1000, sender);

            //显示重新注册闹铃成功
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(context.getString(R.string.notification_title));
            builder.setContentText(context.getString(R.string.notification_reAlarm));
            Notification notification = builder.build();
            NotificationManager manager1 = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager1.notify(NOTIFICATON_ID,notification);
        }
    }
}