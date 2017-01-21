package com.jikexueyuan.guessnumber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

/*
* 本工程代码对应任务三作业二:实现一个猜数字游戏
* 要求：
* 1.程序生成0到100之间的整数
* 2.用户输入0到100之间的整数
* 3.程序验证用户输入是否正确，给予提示
*
* 思路：利用Math.random函数生成随机数
*/
public class MainActivity extends AppCompatActivity {

    //给予用户提示的文本框
    private TextView textHint;
    //用户输入数字的文本框
    private EditText editInput;
    //随机生成的介于0到100之间的一个整数
    private int randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化随机数字
        randomNumber = (int) (Math.random()*100);

        //获取文本框
        textHint = (TextView) findViewById(R.id.textHint);
        editInput = (EditText) findViewById(R.id.editInput);

        //监听"换数字重猜"事件
        findViewById(R.id.btnChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新生成随机数字
                randomNumber = (int) (Math.random()*100);
                //清空输入文本框
                editInput.setText(R.string.textEmpty);
                //清空提示文本框
                textHint.setText(R.string.textEmpty);
            }
        });

        //监听"提交"事件
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //获取用户输入
                String userInput = editInput.getText().toString();

                //判断是否是数字
                if(userInput.matches("[0-9]+"))
                {
                    //是数字则转换为数字
                    int convertedInput = Integer.parseInt(userInput);
                    //判断是否介于[0,100)之间
                    if((convertedInput < 0)||(convertedInput >= 100))
                    {
                        //提示用户输入非法
                        textHint.setText(R.string.textInvalidInput);
                    }
                    else
                    {
                        if(convertedInput > randomNumber)
                        {
                            //用户输入大了
                            textHint.setText(R.string.textLargerInput);
                        }
                        else if(convertedInput < randomNumber)
                        {
                            //用户输入小了
                            textHint.setText(R.string.textSmallerInput);
                        }
                        else
                        {
                            //输入正确
                            textHint.setText(R.string.textEqual);
                        }
                    }
                }
                else
                {
                    //提示用户输入非法
                    textHint.setText(R.string.textInvalidInput);
                }
            }
        });
    }
}
