package com.jikexueyuan.communicatewithinsideservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/*
* Service类，内部开启一个线程进行计数
*/
public class TimerService extends Service {

    //回调函数列表
    private RemoteCallbackList<TimerServiceCallback> callbackList = new RemoteCallbackList<>();
    //控制计数线程运行与停止的flag
    private boolean isRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        //返回由AIDL定义的binder
        return new IAppServiceRemoteBinder.Stub(){
            @Override
            public void registCallback(TimerServiceCallback callback) throws RemoteException {
                //注册回调函数
                callbackList.register(callback);
            }

            @Override
            public void unregistCallback(TimerServiceCallback callback) throws RemoteException {
                //反注册回调函数
                callbackList.unregister(callback);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();

        isRunning = true;
        //创建线程
        new Thread(){
            @Override
            public void run() {
                super.run();

                int timerCount = 0;
                while (isRunning) {
                    timerCount++;

                    //调用回调函数并传回TimerCount值
                    int count = callbackList.beginBroadcast();
                    while (count-- > 0) {
                        try {
                            callbackList.getBroadcastItem(count).onTimer(timerCount);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    callbackList.finishBroadcast();

                    //挂起一秒
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //终止线程
        isRunning = false;
    }
}