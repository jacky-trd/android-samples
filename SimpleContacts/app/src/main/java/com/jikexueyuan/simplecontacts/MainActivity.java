package com.jikexueyuan.simplecontacts;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
* 本工程代码对应任务五作业二：实现一个通信录程序
* 要求：
* 1，实现简单通讯录，可以读取显示系统联系人，可以添加联系人到系统通讯录
* 2，可以启动发信息和打电话
* 3，实现多语言显示
*
* 代码中涉及的类：
* 1，ContactAdpter： 配合ListView控件使用： listView.setAdapter(adapter);
* 2，MainActivity：主类/启动类，包含各个Button及ListView的点击响应事件
* 3，PersonalPhoneInfo: 封装了通讯录中每一条记录的姓名和电话号码
* 4，PhoneInfoDBHelper: 工具类，用于查询和插入通讯录
* 5，代码只支持简体中文，日语，韩语，美式英语；其它语言均显示美式英语。
* 6, SDK版本为：22
*/
public class MainActivity extends AppCompatActivity {
    //主界面中用于显示通讯录
    private ListView listView;
    //添加新记录的对话框
    AlertDialog newContactDialog;
    //选择打电话还是发短信的对话框
    AlertDialog selectionDialog;
    //在打电话或者发短信时，用户选择的电话号码
    String selectedPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //读取电话簿并显示
        refreshViewByPhoneInfo();

        //监听点击“添加”按钮事件
        handleAddNewButtonClickEvent();

        //监听点击某一条通讯录记录事件
        handleContactItemClickEvent();
    }

    /*
    * 读取电话簿并显示
    */
    private void refreshViewByPhoneInfo(){
        ContactsAdpter adapter = new ContactsAdpter(PhoneInfoDBHelper.getPhoneInfo(this), this);
        listView =(ListView) findViewById(R.id.lvContacts);
        listView.setAdapter(adapter);
    }

    /*
    * 监听点击“添加”按钮事件
    */
    private void handleAddNewButtonClickEvent(){
        findViewById(R.id.btnAddContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //显示添加新记录对话框
                showNewContactDialog();

                //监听点击对话框“确认”按钮事件，插入新记录
                newContactDialog.getWindow().findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //插入新记录
                        insertNewRecord();
                    }
                });

                //监听点击对话框“取消”按钮事件，关闭对话框
                newContactDialog.getWindow().findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.this.newContactDialog.dismiss();
                    }
                });
            }
        });
    }

    /*
    * 显示添加新记录对话框
    */
    private void showNewContactDialog(){
        newContactDialog = new AlertDialog.Builder(MainActivity.this).create();
        newContactDialog.show();
        newContactDialog.getWindow().setGravity(Gravity.CENTER);
        newContactDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        newContactDialog.getWindow().setContentView(R.layout.adding_contact);
    }

    /*
    * 插入新记录
    */
    private void insertNewRecord(){
        //获取名字和电话号码
        EditText etName = (EditText)(newContactDialog.getWindow().findViewById(R.id.etInputName));
        EditText etNumber = (EditText)(newContactDialog.getWindow().findViewById(R.id.etInputNumber));

        String strName = etName.getText().toString();
        String strNumber = etNumber.getText().toString();

        //插入新记录
        PhoneInfoDBHelper.insertPhoneInfo(MainActivity.this,strName,strNumber);

        //刷新界面
        refreshViewByPhoneInfo();

        //关闭对话框
        MainActivity.this.newContactDialog.dismiss();
    }

    /*
    * 监听点击某一条通讯录记录事件
    */
    private void handleContactItemClickEvent(){

        listView = (ListView) findViewById(R.id.lvContacts);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取当前的电话号码
                TextView tvSeletedItem = (TextView) view.findViewById(R.id.tvNumber);
                selectedPhoneNumber = tvSeletedItem.getText().toString();

                //显示打电话还是发短信的对话框
                showCallOrMessageDialog();

                //监听点击“打电话”按钮事件
                selectionDialog.getWindow().findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //启动打电话的Activity
                        startAnotherActivity(Intent.ACTION_DIAL, getString(R.string.uriTel));
                        //关闭窗口
                        MainActivity.this.selectionDialog.dismiss();
                    }
                });

                //监听点击“发短信”按钮事件
                selectionDialog.getWindow().findViewById(R.id.btnMessage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //启动发短信的Activity
                        startAnotherActivity(Intent.ACTION_SENDTO, getString(R.string.uriMessage));
                        //关闭窗口
                        MainActivity.this.selectionDialog.dismiss();
                    }
                });

                //监听点击“取消”按钮事件
                selectionDialog.getWindow().findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //关闭窗口
                        MainActivity.this.selectionDialog.dismiss();
                    }
                });
            }
        });
    }

    /*
    * 显示打电话还是发短信的对话框
    */
    private void showCallOrMessageDialog(){
        selectionDialog = new AlertDialog.Builder(MainActivity.this).create();
        selectionDialog.show();
        selectionDialog.getWindow().setGravity(Gravity.CENTER);
        selectionDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        selectionDialog.getWindow().setContentView(R.layout.call_or_message);
    }

    /*
    * 启动打电话或者发短信的Activity
    */
    private void startAnotherActivity(String action, String uri){
        Intent intent = new Intent(action);
        intent.setData(Uri.parse(uri + selectedPhoneNumber));
        startActivity(intent);
    }
}
