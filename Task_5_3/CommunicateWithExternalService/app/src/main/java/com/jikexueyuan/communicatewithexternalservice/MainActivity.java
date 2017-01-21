package com.jikexueyuan.communicatewithexternalservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jikexueyuan.communicatewithinsideservice.IAppServiceRemoteBinder;
import com.jikexueyuan.communicatewithinsideservice.TimerServiceCallback;

/**
* 代码说明：本工程对应作业要求中的外部类，通过AIDL绑定另一个进程中的Service，并显示返回结果
*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    //由AIDL定义的binder
    private IAppServiceRemoteBinder binder = null;
    //显示计数结果的TextView
    TextView tvShowInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //监听"Bind"和"Unbing"的按钮点击事件
        findViewById(R.id.btnBindService).setOnClickListener(this);
        findViewById(R.id.btnUnbindService).setOnClickListener(this);

        tvShowInfo = (TextView) findViewById(R.id.tvShowInfo);
    }

    @Override
    public void onClick(View view) {

        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName(getString(R.string.packageFullPath),getString(R.string.classFullPath)));

        switch (view.getId()){
            //Bind按钮点击响应
            case R.id.btnBindService:
                bindService(serviceIntent,this, Context.BIND_AUTO_CREATE);
                break;
            //Unbind按钮点击响应
            case R.id.btnUnbindService:
                //Bug Fix：用户在没有绑定的情况下点击unbind会引起程序崩溃
                if(null != binder) {
                    unbindService(this);
                    callUnregistBinder();
                    binder = null;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = IAppServiceRemoteBinder.Stub.asInterface(iBinder);
        try {
            //注册回调函数
            binder.registCallback(onServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        //反注册回调函数
        callUnregistBinder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册回调函数
        callUnregistBinder();
    }

    //反注册回调函数具体实现
    private void callUnregistBinder(){
        try {
            binder.unregistCallback(onServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //回调函数具体实现
    private TimerServiceCallback.Stub onServiceCallback = new TimerServiceCallback.Stub() {
        @Override
        public void onTimer(int numberIndex) throws RemoteException {
            Message msg = new Message();
            msg.obj = MainActivity.this;
            msg.arg1 = numberIndex;
            handler.sendMessage(msg);
        }
    };

    //自定义Hander用于处理Service返回的计数值，修改UI线程控件
    private final MyHandler handler = new MyHandler();
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int index = msg.arg1;
            MainActivity _this = (MainActivity) msg.obj;
            _this.tvShowInfo.setText(getString(R.string.currentValueIs) + String.valueOf(index));
        }
    }
}