package com.jikexueyuan.cloudnote.ui.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;

import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.ui.activity.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * 设置的fragment
 */
public class ConfigFragment extends BaseFragment {

    //用户头像
    @BindView(R.id.ivUserAvatar)
    ImageView mIvUserAvatar;
    //退出按钮
    @BindView(R.id.btnInOut)
    Button mBtnInOut;

    @Override
    protected void init() {
        //存在用户则显示退出按钮
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            mBtnInOut.setText(R.string.login_out);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_config;
    }


    @OnClick(R.id.btnInOut)
    public void onClick() {
        //调用bmob api退出，清除缓存用户对象
        BmobUser.logOut();
        //显示登陆界面
        startActivity(new Intent(mContext, LoginActivity.class));
        getActivity().finish();
    }
}
