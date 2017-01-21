package com.jikexueyuan.taxibookingclient;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

/**
 * 服务器连接服务
 */
public class NetService extends Service {

    //Service变量
    private ServiceCallback callback;
    private boolean running = false;

    //连接信息
    private String ipAddress = "192.168.1.105";
    private int port = 8000;

    //Socket相关变量
    private AsyncTask<Void,String,Void> socketTask;
    private Socket socket;
    private InputStreamReader isr;
    private OutputStreamWriter osw;
    private BufferedReader reader;
    private BufferedWriter writer;

    public NetService() {
    }

    //绑定响应函数，创建socket连接
    @Override
    public IBinder onBind(Intent intent) {
        socketConnect(ipAddress, port);
        return new ServiceBinder();
    }

    //创建socket连接
    private void socketConnect(final String ip, final int port){
        //如果socket不为null，说明socket连接已经存在，不做任何操作
        if (socket == null){
            //使用AsyncTask执行后台的网络操作
            socketTask = new AsyncTask<Void, String, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Log.i("jikexueyuan", "Socket connecting");

                        //初始化连接信息
                        socket = new Socket(ip, port);
                        socket.setSoTimeout(3000);

                        //使用DataInputStream和DataOutputStream来进行数据的传输
                        isr = new InputStreamReader(socket.getInputStream(), "utf-8");
                        osw = new OutputStreamWriter(socket.getOutputStream(), "utf-8");
                        reader = new BufferedReader(isr);
                        writer = new BufferedWriter(osw);

                        if (socket.isConnected()){
                            Log.i("jikexueyuan", "Socket is connected");

                            //开始接收服务器端数据
                            String data = null;
                            running = true;
                            while (running){
                                if (reader.ready()){
                                    data = reader.readLine();
                                    //将接收的数据发布出去
                                    publishProgress(data);
                                }
                            }
                        } else {
                            Log.i("jikexueyuan", "Socket does not connect");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(String... values) {
                    super.onProgressUpdate(values);
                    //将数据传给MapActivity
                    callback.onDataChange(values[0]);
                    Log.i("jikexueyuan", "接收到数据:"+values[0]);
                }
            };
            socketTask.execute();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketDisConnect();
    }

    //关闭socket连接，释放资源
    private void socketDisConnect(){
        if (socket != null){
            try {
                running = false;
                socket.close();
                writer.close();
                reader.close();
                socket = null;
                writer = null;
                reader = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //ServiceBinder类和ServiceCallback接口，用于和MapActivity的通讯
    public class ServiceBinder extends Binder {

        //发送预约信息
        public void sendOrder(Map<String, Object> orderMap){
            socketSendDelay(getJson(orderMap));
            Log.i("jiekexueyuan", "Massage send");
        }
        public NetService getService(){
            return NetService.this;
        }
    }

    //回调接口，用于回传数据
    public interface ServiceCallback{
        void onDataChange(String data);
    }

    public void setCallback(ServiceCallback callback) {
        this.callback = callback;
    }

    //发送数据给服务器
    private void socketSend(String data){
        if (writer != null){
            try {
                //发送预约数据
                writer.write(data);
                writer.flush();
                Log.i("jiekexueyuan", "发送信息"+data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //延迟500ms发送数据
    private void socketSendDelay(final String data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    if (writer != null){
                        //发送预约数据
                        writer.write(data);
                        writer.flush();
                        Log.i("jiekexueyuan", "发送信息"+data);
                    } else {
                        Log.i("jiekexueyuan", "Writer is null");

                    }
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //将订单信息转换为Json格式
    private String getJson(Map<String, Object> orderMap){
        JSONObject root = new JSONObject();
        JSONObject order = new JSONObject();

        try {
            order.put("latitude", orderMap.get("latitude"));
            order.put("longitude", orderMap.get("longitude"));
            order.put("phonenumber", orderMap.get("phonenumber"));
            order.put("username", orderMap.get("username"));
            order.put("origin", orderMap.get("origin"));
            order.put("destination", orderMap.get("destination"));

            root.put("order", order);
            root.put("type", "user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root.toString();
    }
}
