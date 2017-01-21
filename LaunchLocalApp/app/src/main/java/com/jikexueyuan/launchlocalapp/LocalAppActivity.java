package com.jikexueyuan.launchlocalapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LocalAppActivity extends AppCompatActivity {

    //用于启动该Activity的Action
    public static final String ACTION = "android.intent.action.VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_app);
    }
}