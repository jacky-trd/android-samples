package com.jikexueyuan.simplecalculator;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

/*
* 本工程代码对应任务四作业二：实现一个简单的计算器
* 要求：
* 1，能够支持加减乘除运算
* 2，不用很复杂，只要能够支持整数运算即可
*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //计算器中的数字键
    private Button btn0;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;

    //计算器中的运算键
    private Button btnAdd;
    private Button btnSub;
    private Button btnMul;
    private Button btnDiv;
    private Button btnClear;
    private Button btnEqual;

    //显示数字的TextView
    private TextView textDisplay;

    //用于标识除法按钮和等号按钮是否被按下
    private boolean IsDivButtonClicked;
    private boolean IsEqualButtonClicked;

    //存储操作数和操作符，用于实现连加连减连乘连除
    Queue<String> dataQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化所有UI控件成员变量
        initUIControls();

        //监听所有按钮的click事件
        setClickEventListener();
    }

    /*
    * 该函数初始化所有UI控件成员变量
    */
    private void initUIControls(){
        btn0 = (Button) findViewById(R.id.btn0);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        btn9 = (Button) findViewById(R.id.btn9);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnMul = (Button) findViewById(R.id.btnMul);
        btnDiv = (Button) findViewById(R.id.btnDiv);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnEqual = (Button) findViewById(R.id.btnEqual);

        //计算器初始值显示为“0”
        textDisplay = (TextView) findViewById(R.id.textDisplay);
        textDisplay.setText(getString(R.string.number_zero));

        //创建队列对象
        dataQueue = new LinkedList<String>();

        IsDivButtonClicked = false;
        IsEqualButtonClicked = false;
    }

    /*
    * 该函数对所有按钮进行click监听
    */
    private void setClickEventListener(){
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnMul.setOnClickListener(this);
        btnDiv.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnEqual.setOnClickListener(this);
    }

    /*
    * 实现按钮的click事件响应
    */
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn0:
                //除数不可以为零
                if(IsDivButtonClicked){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle(getString(R.string.number_errTitle));
                    builder.setMessage(getString(R.string.number_errMessage));
                    builder.setPositiveButton(getString(R.string.number_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    builder.show();
                }
                showNumber(getString(R.string.number_zero));
                break;
            case R.id.btn1:
                showNumber(getString(R.string.number_one));
                break;
            case R.id.btn2:
                showNumber(getString(R.string.number_two));
                break;
            case R.id.btn3:
                showNumber(getString(R.string.number_three));
                break;
            case R.id.btn4:
                showNumber(getString(R.string.number_four));
                break;
            case R.id.btn5:
                showNumber(getString(R.string.number_five));
                break;
            case R.id.btn6:
                showNumber(getString(R.string.number_six));
                break;
            case R.id.btn7:
                showNumber(getString(R.string.number_seven));
                break;
            case R.id.btn8:
                showNumber(getString(R.string.number_eight));
                break;
            case R.id.btn9:
                showNumber(getString(R.string.number_nine));
                break;
            case R.id.btnAdd:
                handleOperatorButtion(getString(R.string.number_add));
                break;
            case R.id.btnSub:
                handleOperatorButtion(getString(R.string.number_sub));
                break;
            case R.id.btnMul:
                handleOperatorButtion(getString(R.string.number_mul));
                break;
            case R.id.btnDiv:
                IsDivButtonClicked = true;
                handleOperatorButtion(getString(R.string.number_div));
                break;
            case R.id.btnClear:
                dataQueue.clear();
                textDisplay.setText(getString(R.string.number_zero));
                IsDivButtonClicked =false;
                IsEqualButtonClicked =false;
                break;
            case R.id.btnEqual:
                IsEqualButtonClicked = true;
                IsDivButtonClicked = false;

                //最后一个操作数入队列
                String tempString = textDisplay.getText().toString();
                dataQueue.offer(tempString);
                //第一个操作数出队列
                String strResult = dataQueue.poll();
                int intResult = Integer.parseInt(strResult);

                while (!dataQueue.isEmpty()){
                    //取出下一个操作符
                    strResult = dataQueue.poll();

                    if (strResult.equals(getString(R.string.number_add))){
                        strResult = dataQueue.poll();
                        intResult += Integer.parseInt(strResult);
                    }
                    else if (strResult.equals(getString(R.string.number_sub))){
                        strResult = dataQueue.poll();
                        intResult -= Integer.parseInt(strResult);
                    }
                    else if (strResult.equals(getString(R.string.number_mul))){
                        strResult = dataQueue.poll();
                        intResult *= Integer.parseInt(strResult);
                    }
                    else if (strResult.equals(getString(R.string.number_div))){
                        //除数非零则计算，否则返回错误
                        if(getString(R.string.number_zero) != dataQueue.peek()) {
                            strResult = dataQueue.poll();
                            intResult /= Integer.parseInt(strResult);
                        }
                        else {
                            textDisplay.setText(getString(R.string.number_error));
                            return;
                        }
                    }
                }
                //显示结果
                strResult = String.valueOf(intResult);
                textDisplay.setText(strResult);
                //清空操作栈
                dataQueue.clear();
                break;

            default:
                break;
        }
    }

    /*
    * 该函数显示用户点击的数字
    */
    private void showNumber(String strNumber){
        String tempString = textDisplay.getText().toString();

        //如果当前是零，则用点击的数字替换零
        if(tempString.equals(getString(R.string.number_zero))) {
            tempString = strNumber;
        }
        //如果是二次计算，则用点击的数字替换当前数字
        else if (IsEqualButtonClicked){
            tempString = strNumber;
        }
        //否则在后面追加
        else{
            tempString += strNumber;
        }

        textDisplay.setText(tempString);
        IsDivButtonClicked = false;
        IsEqualButtonClicked = false;
    }

    /*
    * 该函数对用户点击运算符进行处理
    */
    private  void handleOperatorButtion(String strOperator)
    {
        String tempString = textDisplay.getText().toString();

        //操作数和运算符进队列
        dataQueue.offer(tempString);
        dataQueue.offer(strOperator);
        //计算器再次显示0
        textDisplay.setText(getString(R.string.number_zero));

        IsEqualButtonClicked = false;
    }
}
