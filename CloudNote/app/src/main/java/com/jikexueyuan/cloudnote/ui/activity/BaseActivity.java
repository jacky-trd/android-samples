package com.jikexueyuan.cloudnote.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * 所有activity的基类，实现并定义基本功能
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    //进度对话框
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;

        //获取布局id
        int layout = getLayoutId();
        if (layout == 0) {
            //无布局id则报错
            throw new IllegalStateException("Please specify root layout resource id for " + getClass().getSimpleName());
        } else {
            //设置layout
            setContentView(layout);
            //ButterKnife框架绑定
            ButterKnife.bind(this);
            //初始化数据
            initData();
            //初始化视图
            initView();
            //初始化监听器
            setListeners();

            //初始化进度对话框
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mProgressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                mProgressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            }

            mProgressDialog.setMessage("请稍等...");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //显示Toast
    protected void showShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

   //显示进度条
    public void showProgress() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    //关闭进度条
    public void closeProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    //视图初始化的抽象函数
    protected abstract void initView();

    //数据初始化的抽象函数
    protected abstract void initData();

    //监听器初始化的抽象函数
    protected abstract void setListeners();

    //获取布局id的抽象函数
    protected abstract int getLayoutId();
}
