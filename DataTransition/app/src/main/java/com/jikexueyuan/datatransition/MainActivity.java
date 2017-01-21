package com.jikexueyuan.datatransition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/*
* 本工程代码对应任务三作业一：将第一个Activity中输入的信息传给第二个Activity
*
* 思路：通过一个Bundle实例传递用户输入的姓名和年龄
* 说明：因为本列子中仅仅为了演示在Activity间传递数据，所以代码中没有对用户的输入（如年龄）
* 做校验和错误处理
*/
public class MainActivity extends AppCompatActivity {

    //输入姓名和年龄的文本框
    private EditText editName;
    private EditText editAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取输入姓名和年龄的文本框
        editName = (EditText) findViewById(R.id.editName);
        editAge = (EditText) findViewById(R.id.editAge);

        //获取按钮并监听点击事件
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //通过一个Bundle传递姓名和年龄
                Bundle data = new Bundle();
                data.putString("name",editName.getText().toString());
                data.putString("age",editAge.getText().toString());

                //实例化一个Intent对象
                Intent i = new Intent(MainActivity.this,ReceiveDataActivity.class);
                i.putExtras(data);

                //启动第二个Activity并传递数据
                startActivity(i);
            }
        });
    }
}