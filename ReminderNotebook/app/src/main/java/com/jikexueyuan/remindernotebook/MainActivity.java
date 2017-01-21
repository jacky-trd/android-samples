package com.jikexueyuan.remindernotebook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 本工程代码对应任务六作业一：定时提醒记事本
 * 要求：可以实现整点事件提醒，开机后即可检测时间。
 *
 * 代码说明：
 * 1，MainActivity:主类/启动类，主要负责事件列表ListView的显示与删除操作
 * 2，AddEventActiviy:负责添加新事件，插入SQLiteDatabase;同时将新事件注册到系统的AlarmManager中
 * 3，AlarmReceiver:该类为Broadcase Receiver,负责响应闹钟事件
 * 4, BootCompletedReceiver:设备重启后，接收BOOT_COMPLETED广播，重新注册闹钟
 * 5，DBOperationHelper：该类为工具类，负责数据库SQLiteDatabase相关操作
 * 6，EventAdapter: 该类为自定义的adapter类，配合ListView使用
 * 7，RemindEventInfo:该类对新建的事件进行了封装，包括事件提醒时间和事件内容
 */
public class MainActivity extends AppCompatActivity {

    //用于显示事件列表的ListView
    private ListView listView;
    //询问用户是否要删除事件的对话框
    AlertDialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //读取数据库，加载界面
        refreshViewByEventInfo();
        //监听ListView长按删除事件
        handleEventItemLongClickEvent();
    }

    //读取数据库，加载界面
    private void refreshViewByEventInfo(){
        DBOperationHelper helper = new DBOperationHelper(this);
        EventAdapter adpter = new EventAdapter(helper.getEvents(),this);
        listView =(ListView) findViewById(R.id.lvReminderList);
        listView.setAdapter(adpter);
    }

    //监听ListView长按删除事件
    private void handleEventItemLongClickEvent(){
        listView = (ListView) findViewById(R.id.lvReminderList);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //询问是否要删除待提醒事件
                showConfirmDialog();
                //用户选择不要删除
                confirmDialog.getWindow().findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.this.confirmDialog.dismiss();
                    }
                });
                //用户确认删除
                confirmDialog.getWindow().findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除
                        DBOperationHelper helper = new DBOperationHelper(MainActivity.this);
                        int alarmId = helper.deleteEvent(i);
                        //从系统AlarmManager中注销该提醒
                        cancelAlarm(alarmId);
                        //重新加载界面
                        refreshViewByEventInfo();
                        //提示删除成功
                        Toast.makeText(MainActivity.this,getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
                        MainActivity.this.confirmDialog.dismiss();
                    }
                });
                return true;
            }
        });
    }

    //从系统AlarmManager中注销该提醒
    private void cancelAlarm(int id){
        Intent intent = new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    //显示对话框，询问是否真的要删除
    private void showConfirmDialog(){
        confirmDialog = new AlertDialog.Builder(MainActivity.this).create();
        confirmDialog.show();
        confirmDialog.getWindow().setGravity(Gravity.CENTER);
        confirmDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        confirmDialog.getWindow().setContentView(R.layout.delete_item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addEvent) {
            //启动添加事件的Activity
            Intent intent = new Intent(MainActivity.this,AddEventActivity.class);
            startActivityForResult(intent,getResources().getInteger(R.integer.addEventRequest));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果返回结果为添加新事件成功，则重新加载界面
        if(requestCode == getResources().getInteger(R.integer.addEventRequest)){
            if (resultCode == getResources().getInteger(R.integer.addEventResultSuccess))
            {
                refreshViewByEventInfo();
            }
        }
    }
}