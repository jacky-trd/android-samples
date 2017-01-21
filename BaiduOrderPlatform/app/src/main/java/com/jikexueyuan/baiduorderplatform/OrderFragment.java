package com.jikexueyuan.baiduorderplatform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 该类对应百度外卖中的“订单”页
 */
public class OrderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取自定义的fragment_order布局并返回
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        return rootView;
    }
}