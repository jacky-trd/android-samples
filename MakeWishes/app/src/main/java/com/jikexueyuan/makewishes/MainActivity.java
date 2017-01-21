package com.jikexueyuan.makewishes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*
* 工程代码对应任务五作业一：许下你的愿望
* 要求：启动一个Activity用于输入信息并获取其返回值和返回状态
*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //监听点击“许愿”按钮事件
        findViewById(R.id.btnRequestWishes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每次许愿前，先清空当前内容
                TextView tvDisplayWishes;
                tvDisplayWishes = (TextView) findViewById(R.id.tvDisplayWishes);
                tvDisplayWishes.setText(getString(R.string.emptyString));

                //启动另一个Activity
                Intent intent = new Intent(MainActivity.this,MessageInputActivity.class);
                startActivityForResult(intent,getResources().getInteger(R.integer.requestWishes));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //如果Request Code是"请求许愿"，则进行内容显示
        if(requestCode == getResources().getInteger(R.integer.requestWishes)){
            if(resultCode==getResources().getInteger(R.integer.cancel)){
                //用户取消了许愿
                Toast.makeText(this,getString(R.string.infoCancel),Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == getResources().getInteger(R.integer.makeWishes))
            {
                //显示许愿内容
                TextView tvDisplayWishes;
                tvDisplayWishes = (TextView) findViewById(R.id.tvDisplayWishes);
                tvDisplayWishes.setText(getString(R.string.yourWishesIs) + data.getStringExtra(getString(R.string.wishesTag)));
            }
        }
        else{
            Toast.makeText(this,getString(R.string.unknownRequest),Toast.LENGTH_SHORT).show();
        }
    }
}