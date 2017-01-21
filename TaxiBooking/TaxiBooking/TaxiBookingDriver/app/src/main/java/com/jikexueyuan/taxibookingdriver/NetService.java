package com.jikexueyuan.taxibookingdriver;

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
        Log.i("jikexueyuan", "Service onBind");

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
                        //初始化连接信息
                        socket = new Socket(ip, port);
                        socket.setSoTimeout(3000);

                        //使用DataInputStream和DataOutputStream来进行数据的传输
                        isr = new InputStreamReader(socket.getInputStream(), "utf-8");
                        osw = new OutputStreamWriter(socket.getOutputStream(), "utf-8");
                        reader = new BufferedReader(isr);
                        writer = new BufferedWriter(osw);

                        if (socket.isConnected()) {
                            Log.i("jikexueyuan", "Socket is connected");

                            //开始接收服务器端数据
                            String data = null;
                            running = true;
                            while (running) {
                                if (reader.ready()){
                                    data = reader.readLine();
                                    //将接收的数据发布出去
                                    publishProgress(data);
                                }
                            }
                            Log.i("jikexueyuan", "Socket closed");
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
                    //将接收到的信息发送给ListActivity
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
        Log.i("jikexueyuan", "Service onDestroy");
        super.onDestroy();
        running = false;
        socketDisConnect();
        socketTask.cancel(false);
    }

    //关闭socket连接，释放资源
    private void socketDisConnect(){
        Log.i("jikexueyuan", "socketDistConnect()");

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

    //ServiceBinder类和ServiceCallback接口，用于和ListActivity的通讯
    public class ServiceBinder extends Binder {
        //发送位置信息
        public void sendInfo(Map<String, Object> infoMap){
            socketSend(getJson(infoMap));
        }

        //发送预约信息
        public void sendOrder(String username, String drivername){
            socketSend(getJson(username, drivername));
        }

        //发送预约完成信息
        public void finish(String username){
            JSONObject root = new JSONObject();
            try {
                root.put("type", "finish");
                root.put("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socketSend(root.toString());
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
        Log.i("jikexueyuan", "socketSend()");

        if (writer != null){
            try {
                //发送数据
                writer.write(data);
                writer.flush();
                Log.i("jikexueyuan", "发送了数据"+data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("jikexueyuan", "socketSend: Write is null");
        }
    }

    //将司机信息转换为Json格式
    private String getJson(Map<String, Object> infoMap){
        JSONObject root = new JSONObject();
        JSONObject info = new JSONObject();

        try {
            info.put("latitude", infoMap.get("latitude"));
            info.put("longitude", infoMap.get("longitude"));
            info.put("username", infoMap.get("username"));
            info.put("phonenumber", infoMap.get("phonenumber"));

            root.put("info", info);
            root.put("type", "driver");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root.toString();
    }

    //将订单信息转换为Json格式
    private String getJson(String username, String drivername){
        JSONObject root = new JSONObject();
        JSONObject driverorder = new JSONObject();

        try {
            driverorder.put("username", username);
            driverorder.put("drivername", drivername);
            root.put("driverorder", driverorder);
            root.put("type", "getorder");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root.toString();
    }
}
