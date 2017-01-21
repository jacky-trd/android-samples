package com.jikexueyuan.checkanswer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

/*
* 本工程代码对应任务四作业一：单选题与多选题结果输出
*/
public class MainActivity extends AppCompatActivity {

    //单选按钮
    private RadioButton rdMale;
    private RadioButton rdFemale;

    //多选按钮
    private CheckBox cbPacific;
    private CheckBox cbAtlantic;
    private CheckBox cbSummer;
    private CheckBox cbIndia;
    private CheckBox cbArctic;
    private CheckBox cbBohai;

    //提交按钮
    private Button btnSubmit;

    //显示结果
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化所有按钮
        initUIControls();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strResult = getString(R.string.choice);

                //判断单选结果
                if(rdMale.isChecked()){
                    strResult += getString(R.string.maleAnswer);
                }
                else if(rdFemale.isChecked()){
                    strResult += getString(R.string.femaleAnswer);
                }
                else
                {
                    //请选择性别
                    strResult = getString(R.string.error);
                    tvResult.setText(strResult);
                    return;
                }

                //判断多选结果
                if((!cbPacific.isChecked())&&(!cbAtlantic.isChecked())&&(cbSummer.isChecked())
                        &&(!cbIndia.isChecked())&&(!cbArctic.isChecked())&&(cbBohai.isChecked())){
                    strResult += getString(R.string.correctChoice);
                }
                else{
                    strResult += getString(R.string.incorrectChoice);
                }

                //显示结果
                tvResult.setText(strResult);
            }
        });
    }

    /*
    * 初始化所有按钮
    */
    private void initUIControls(){
        //获取单选按钮
        rdMale = (RadioButton) findViewById(R.id.radioMale);
        rdFemale = (RadioButton)findViewById(R.id.radioFemale);
        //获取多选按钮
        cbPacific = (CheckBox) findViewById(R.id.cbPacific);
        cbAtlantic = (CheckBox) findViewById(R.id.cbAtlantic);
        cbSummer = (CheckBox) findViewById(R.id.cbSummer);
        cbIndia = (CheckBox) findViewById(R.id.cbIndia);
        cbArctic = (CheckBox) findViewById(R.id.cbArctic);
        cbBohai = (CheckBox) findViewById(R.id.cbBohai);
        //获取提交按钮
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        //获取显示结果的控件
        tvResult = (TextView) findViewById(R.id.tvResult);
    }
}
