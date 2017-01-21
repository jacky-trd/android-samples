package com.jikexueyuan.mylocation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class NetworkService extends Service {

    //！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    //服务器IP地址，这是我本地wifi的局域网地址，老师可以修改这个地址
    private static final String SERVER_IP = "192.168.1.102";

    private Socket mSocket;
    private BufferedReader mBufferedReader;
    private BufferedWriter mBufferedWriter;

    public NetworkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册总线
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(SERVER_IP,8000);
                    mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (true){
                    String locationStr;
                    try {
                        while ((locationStr = mBufferedReader.readLine()) != null) {
                            String[] lat_lang = locationStr.trim().split(",");
                            LatLng latlng = new LatLng(Double.parseDouble(lat_lang[0]), Double.parseDouble(lat_lang[1]));
                            //通过总线向activity传递位置数据
                            EventBus.getDefault().post(new LocationEvent(latlng,false));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe
    public void onLocationEvent(LocationEvent event) {
        if (event.isPublish()) {
            //向服务器提交位置
            LatLng latLng = event.getLatLng();
            String latlngStr = latLng.latitude + "," + latLng.longitude + "\n";
            try {
                mBufferedWriter.write(latlngStr);
                mBufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
