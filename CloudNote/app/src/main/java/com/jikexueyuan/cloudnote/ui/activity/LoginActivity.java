package com.jikexueyuan.cloudnote.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.utils.NetUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 登陆界面activity
 */
public class LoginActivity extends BaseActivity {

    //用户名
    @BindView(R.id.etLoginUsername)
    EditText mEtLoginUsername;
    //密码
    @BindView(R.id.etLoginPassword)
    EditText mEtLoginPassword;
    //登陆按钮
    @BindView(R.id.btnLogin)
    Button mBtnLogin;
    //注册按钮
    @BindView(R.id.tvRegister)
    TextView mTvRegister;
    //退出程序按钮
    @BindView(R.id.ivBack)
    ImageView mIvBack;

    //用户名字符串
    private String mLoginUsername;
    //密码字符串
    private String mLoginPassword;

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
        return R.layout.activity_login;
    }

    //监听器响应函数
    @OnClick({R.id.btnLogin, R.id.tvRegister, R.id.ivBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                //获取用户名和密码
                mLoginUsername = mEtLoginUsername.getText().toString().trim();
                mLoginPassword = mEtLoginPassword.getText().toString().trim();

                //都不是空
                if (!NetUtils.isNetworkConnected(mContext)) {
                    //没有网络连接
                    showShortToast(getString(R.string.no_connect));
                } else if (TextUtils.isEmpty(mLoginUsername)) {
                    //没有用户名
                    mEtLoginUsername.requestFocus();
                    mEtLoginUsername.setError(getString(R.string.username_nonblank));
                    return;
                } else if (TextUtils.isEmpty(mLoginPassword)) {
                    //没有密码
                    mEtLoginPassword.requestFocus();
                    mEtLoginPassword.setError(getString(R.string.password_nonblank));
                    return;
                } else {
                    showProgress();
                    //调用bmob api进行用户验证
                    BmobUser bmobUser = new BmobUser();
                    bmobUser.setUsername(mLoginUsername);
                    bmobUser.setPassword(mLoginPassword);
                    bmobUser.login(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            closeProgress();
                            if (e == null) {
                                //进入主页面
                                startActivity(new Intent(mContext, MainActivity.class));
                                LoginActivity.this.finish();
                            } else {
                                showShortToast(getString(R.string.user_pass_err));
                            }
                        }
                    });
                }
                break;
            case R.id.tvRegister:
                //显示注册界面
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
            case R.id.ivBack:
                //关闭程序
                finish();
                break;
        }
    }
}
