package com.jikexueyuan.remindernotebook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * 该类负责添加新事件，插入SQLiteDatabase;同时将新事件注册到系统的AlarmManager中
 */
public class AddEventActivity extends AppCompatActivity {

    //事件提醒时间和内容的EditText
    private EditText etEventTime;
    private EditText etEventContent;

    //事件对应的闹钟Id，用于注销闹钟时使用
    //该值同事件提醒时间和内容一起存入数据库
    private static int AlarmId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //获取事件提醒时间和内容的EditText
        etEventTime = (EditText) findViewById(R.id.etEventTime);
        etEventContent = (EditText) findViewById(R.id.etEventContent);

        //初始化新的闹铃Id,值为当前数据库中存在的最大的id+1
        AddEventActivity.AlarmId = findExistingBiggestId()+1;

        //监听点击“保存”按钮事件
        handleSaveClickEvent();
    }

    //查询当前数据库中最大的Id
    private int findExistingBiggestId(){

        //读取数据库，取回全部events
        DBOperationHelper helper = new DBOperationHelper(this);
        List<RemindEventInfo> list = helper.getEvents();

        //循环注册闹钟
        int biggestId = 0;
        for(RemindEventInfo event:list){
            int id = event.getAlarmID();

            if(biggestId < id){
                biggestId = id;
            }
        }
        return biggestId;
    }

    //监听点击“保存”按钮事件
    private void handleSaveClickEvent(){
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //如果用户输入为空，提示用户输入内容
                String strTime = etEventTime.getText().toString();
                String strContent = etEventContent.getText().toString();
                if((strTime.equals(getString(R.string.empty_string))) ||(strContent.equals(getString(R.string.empty_string)))){
                    Toast.makeText(AddEventActivity.this,getString(R.string.input_empty),Toast.LENGTH_SHORT).show();
                    return;
                }

                //如果用户输入的不是0到24之间的一个数字，则提示错误并返回
                int time = Integer.parseInt(strTime);
                if((time < getResources().getInteger(R.integer.zeroTime))||(time > getResources().getInteger(R.integer.twentyfourTime))){
                    Toast.makeText(AddEventActivity.this,getString(R.string.input_error),Toast.LENGTH_SHORT).show();
                    return;
                }

                //保存新事件
                saveEvent();
                //注册闹钟
                registerAlarm();
                //提示成功并返回
                Toast.makeText(AddEventActivity.this,getString(R.string.add_success),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(getResources().getInteger(R.integer.addEventResultSuccess),intent);
                finish();
            }
        });
    }

    //保存新事件
    private void saveEvent(){
        //闹钟Id加一，确保不重复（这里暂时不考虑软件第二次重启时的情况。。。）
        AddEventActivity.AlarmId++;

        RemindEventInfo event = new RemindEventInfo(Integer.parseInt(etEventTime.getText().toString()),etEventContent.getText().toString(),AlarmId);
        DBOperationHelper helper = new DBOperationHelper(this);
        helper.insertEvent(event);
    }

    //注册闹钟
    private void registerAlarm(){
        /*为了方便测试，我将计算时间的代码注释掉了，将原来的整点提醒（间隔一天）变成了，整秒提醒，间隔10秒。题目要求的代码在注释里。
        long firstTime = SystemClock.elapsedRealtime();
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 设置时区
        calendar.setTimeZone(TimeZone.getTimeZone(getString(R.string.GMT_8_Time_Zone)));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(etEventTime.getText().toString()));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }

        long time = selectTime - systemTime;
        firstTime += time;

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(getString(R.string.notification_content_tag),etEventContent.getText().toString());
        PendingIntent sender = PendingIntent.getBroadcast(this,AlarmId,intent, 0);
        // 进行闹铃注册
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 24*60*60*1000, sender);*/

        //以下代码将小时变成了秒，将间隔从一天变成了10秒,方便老师检查和测试，题目要求的代码在上方注释中
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(getString(R.string.notification_content_tag),etEventContent.getText().toString());
        PendingIntent sender = PendingIntent.getBroadcast(this,AlarmId,intent, 0);
        // 进行闹铃注册
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int time = Integer.parseInt(etEventTime.getText().toString());
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, 10*1000, sender);
    }
}