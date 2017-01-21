package com.jikexueyuan.makewishes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MessageInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_input);

        //用户取消许愿则发回cancel code
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(getResources().getInteger(R.integer.cancel));
                finish();
            }
        });

        //用户许愿则发回许愿内容和make wishes code
        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText etMakeWishes = (EditText) findViewById(R.id.etMakeWishes);
                String strWishes = etMakeWishes.getText().toString();

                if(strWishes.isEmpty()){
                    //提示用户当前许愿内容为空
                    Toast.makeText(MessageInputActivity.this,getString(R.string.requestWishes),Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.wishesTag),etMakeWishes.getText().toString());
                    setResult(getResources().getInteger(R.integer.makeWishes),intent);
                    finish();
                }
            }
        });
    }
}
