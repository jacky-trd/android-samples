package com.jikexueyuan.addcards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 本工程对应任务八作业一：用代码分4列5行添加20个数字
 *
 * 解体思路：通过自定义的CardsView类画4列5行20个数字
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CardsView(this));
    }
}