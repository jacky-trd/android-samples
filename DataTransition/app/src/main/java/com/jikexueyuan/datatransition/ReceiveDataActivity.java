package com.jikexueyuan.datatransition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/*
* 此为第二个接收并显示数据的Activity
*/
public class ReceiveDataActivity extends AppCompatActivity {

    //显示姓名和年龄的文本框
    private TextView txtName;
    private TextView txtAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_data);

        //获取Intent和bundle对象
        Intent i = getIntent();
        Bundle data = i.getExtras();

        //获取显示姓名和年龄的文本框
        txtName = (TextView) findViewById(R.id.textReceiveName);
        txtAge = (TextView) findViewById(R.id.textReceiveAge);

        //显示数据
        txtName.setText("您的姓名是：" + data.getString("name"));
        txtAge.setText("您的年龄是：" + data.getString("age"));
    }
}
