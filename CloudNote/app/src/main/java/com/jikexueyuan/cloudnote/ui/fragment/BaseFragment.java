package com.jikexueyuan.cloudnote.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * fragment基类，定义并实现基本功能
 */
public abstract class BaseFragment extends Fragment {
    protected Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //获取布局id
        int layoutId = getLayoutId();
        if (layoutId == 0) {
            //无布局id则出错
            throw new IllegalStateException("Please specify root layout resource id for " + getClass().getSimpleName());
        } else {
            mContext = getActivity().getApplicationContext();
            //获取视图
            View parentView = inflater.inflate(layoutId, null);
            //绑定buterknife框架
            ButterKnife.bind(this, parentView);
            //初始化
            init();
            return parentView;
        }
    }

    protected abstract void init();

    protected abstract int getLayoutId();

    protected void showShortToast(String msg) {
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
}
