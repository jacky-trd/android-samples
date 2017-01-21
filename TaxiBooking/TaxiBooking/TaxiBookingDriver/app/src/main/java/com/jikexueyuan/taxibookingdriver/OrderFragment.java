package com.jikexueyuan.taxibookingdriver;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 预约信息界面
 */
public class OrderFragment extends Fragment implements View.OnClickListener {

    //乘客姓名和乘客电话
    private String userName;
    private String userPhone;

    //乘客姓名和乘客电话的textview
    private TextView userNameTv;
    private TextView userPhoneTv;

    //乘客姓名和乘客电话的tag
    private static final String ARG_PARAM1 = "username";
    private static final String ARG_PARAM2 = "userphone";

    //打电话按钮
    private Button callBtn;

    //完成按钮
    private Button finishBtn;

    //该接口用于返回
    private OnFragmentInteractionListener mListener;

    public OrderFragment() {}

    //构造实例
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //获取乘客姓名和乘客电话
            userName = getArguments().getString(ARG_PARAM1);
            userPhone = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //视图初始化
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        userNameTv = (TextView) root.findViewById(R.id.username_textview);
        userPhoneTv = (TextView) root.findViewById(R.id.userphone_textview);
        userNameTv.setText(userName);
        userPhoneTv.setText(userPhone);
        callBtn = (Button) root.findViewById(R.id.call_button);
        finishBtn = (Button) root.findViewById(R.id.finish_button);
        callBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);

        return root;
    }

    //预约结束
    public void finish(String username) {
        System.out.println("OrderFragment.finish");
        if (mListener != null) {
            //返回乘客名称
            mListener.onFragmentInteraction(username);

        } else {
            System.out.println("mListener = " + mListener);;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("OrderFragment.onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            //绑定OnFragmentInterfaceListener
            mListener = (OnFragmentInteractionListener) context;
            System.out.println("mListener = " + mListener);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_button:
                //拨打电话
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+userPhone)));
                break;
            case R.id.finish_button:
                //完成预约
                System.out.println("OrderFragment.onClick");
                finish(userName);
                getFragmentManager().popBackStack();
                break;
            default:
                break;

        }
    }

    //FragmentListener接口
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String username);
    }
}
