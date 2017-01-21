package com.jikexueyuan.taxibookingclient;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 司机接受预约界面
 */
public class OrderFragment extends Fragment {

    //司机姓名和司机电话
    private String driverName;
    private String driverPhone;

    //司机姓名和司机电话的textview
    private TextView driverNameTv;
    private TextView driverPhoneTv;

    //司机姓名和司机电话的tag
    private static final String ARG_PARAM1 = "drivername";
    private static final String ARG_PARAM2 = "driverphone";

    //该接口用于返回
    private OnFragmentInteractionListener mListener;

    public OrderFragment() {}

    //构造实例
    public static OrderFragment newInstance(String driverName, String driverPhone) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, driverName);
        args.putString(ARG_PARAM2, driverPhone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //获取司机姓名和司机电话
            driverName = getArguments().getString(ARG_PARAM1);
            driverPhone = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //在视图上设置司机姓名和司机电话
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        driverNameTv = (TextView) root.findViewById(R.id.drivername_textview);
        driverPhoneTv = (TextView) root.findViewById(R.id.driverphone_textview);
        driverNameTv.setText(driverName);
        driverPhoneTv.setText(driverPhone);
        return root;
    }

    public void onExit(boolean status) {
        if (mListener != null) {
            //无返回数据
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            //绑定OnFragmentInterfaceListener
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //FragmentListener接口
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
