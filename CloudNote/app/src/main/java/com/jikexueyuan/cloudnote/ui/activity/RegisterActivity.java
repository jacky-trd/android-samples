package com.jikexueyuan.cloudnote.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.utils.NetUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 注册界面activity
 */
public class RegisterActivity extends BaseActivity {

    //用户名
    @BindView(R.id.etRegisterUsername)
    EditText mEtRegisterUsername;
    //密码
    @BindView(R.id.etRegisterPassword)
    EditText mEtRegisterPassword;
    //注册按钮
    @BindView(R.id.btnRegister)
    Button mBtnRegister;
    //返回按钮
    @BindView(R.id.ivBack)
    ImageView mIvBack;

    //用户名和密码字符串
    private String mRegisterUsername;
    private String mRegisterPassword;

    //重写父类视图初始化，无逻辑
    @Override
    protected void initView() {}

    //重写父类数据初始化，无逻辑
    @Override
    protected void initData() {}

    //重写父类监听器初始化，无逻辑
    @Override
    protected void setListeners() {}

    //重写父类获取布局id
    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    //监听器响应函数
    @OnClick({R.id.ivBack, R.id.btnRegister})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                //返回
                finish();
                break;
            case R.id.btnRegister:
                mRegisterUsername = mEtRegisterUsername.getText().toString().trim();
                mRegisterPassword = mEtRegisterPassword.getText().toString().trim();

                if (!NetUtils.isNetworkConnected(mContext)) {
                    //没有网路
                    showShortToast(getString(R.string.no_connect));
                } else if (TextUtils.isEmpty(mRegisterUsername)) {
                    //没有用户名
                    mEtRegisterUsername.requestFocus();
                    mEtRegisterUsername.setError(getString(R.string.username_nonblank));
                    return;
                } else if (TextUtils.isEmpty(mRegisterPassword)) {
                    //没有密码
                    mEtRegisterPassword.requestFocus();
                    mEtRegisterPassword.setError(getString(R.string.password_nonblank));
                    return;
                } else {
                    showProgress();
                    //调用bmob api进行注册
                    BmobUser bmobUser = new BmobUser();
                    bmobUser.setUsername(mRegisterUsername);
                    bmobUser.setPassword(mRegisterPassword);
                    bmobUser.signUp(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser s, BmobException e) {
                            closeProgress();
                            if (e == null) {
                                //注册成功显示主界面
                                showShortToast(getString(R.string.register_success));
                                startActivity(new Intent(mContext, MainActivity.class));
                                RegisterActivity.this.finish();
                            } else {
                                showShortToast(getString(R.string.register_err));
                            }
                        }
                    });
                }
                break;
        }
    }
}
