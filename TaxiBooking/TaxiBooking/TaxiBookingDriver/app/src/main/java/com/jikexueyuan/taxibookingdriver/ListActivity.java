package com.jikexueyuan.taxibookingdriver;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约列表信息界面
 */
public class ListActivity extends AppCompatActivity implements BDLocationListener, ServiceConnection, AdapterView.OnItemClickListener, OrderFragment.OnFragmentInteractionListener{

    //百度地图相关
    private BDLocation myBDLocation;
    private LocationClient locationClient;
    private boolean isFirstSend = true;

    //预约列表相关
    private SimpleAdapter adapter;
    private ListView listView;

    //乘客list
    private List<Map<String, Object>> userList;

    //服务器连接服务
    private NetService.ServiceBinder binder;

    //生命周期方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //百度SDK初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_list);
        //app初始化
        init();
    }

    //app初始化
    public void init(){
        listView = (ListView) findViewById(R.id.listview);
        userList = new ArrayList<>();
        adapter = new SimpleAdapter(this,
                userList,
                R.layout.list_item,
                new String[]{"username", "distance", "origin", "destination"},
                new int[]{R.id.username_textview, R.id.distance_textview, R.id.origin_textview, R.id.destination_textview});
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        bindService(new Intent(this, NetService.class), this, BIND_AUTO_CREATE);
        listView.setOnItemClickListener(this);

        //百度地图相关设置
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(this);
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setOpenGps(true);
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setCoorType(BDLocation.BDLOCATION_GCJ02_TO_BD09LL);
        locationClientOption.setScanSpan(3000);
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setNeedDeviceDirect(true);
        locationClient.setLocOption(locationClientOption);
        locationClient.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        unbindService(this);
    }

    //百度地图的监听器
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        myBDLocation = bdLocation;
        if (isFirstSend && (binder!=null)){
            isFirstSend = false;
            Intent intent = getIntent();
            Map<String, Object> info = new HashMap<>();
            info.put("username", intent.getStringExtra("username"));
            info.put("phonenumber", intent.getStringExtra("phonenumber"));
            info.put("latitude", bdLocation.getLatitude());
            info.put("longitude", bdLocation.getLongitude());
            binder.sendInfo(info);
        }
    }

    //Service方法
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        //设置服务回调
        binder = (NetService.ServiceBinder) iBinder;
        binder.getService().setCallback(new NetService.ServiceCallback() {
            @Override
            public void onDataChange(String data) {
                try {
                    JSONObject root = new JSONObject(data);
                    String tag = root.getString("tag");
                    //通过分析接收到的信息，进行不同的操作
                    if (tag.equals("order")) {
                        //当接收到的信息为乘客订单时
                        if (root.getString("result").equals("true")) {
                            System.out.println("Starting orderfragment");
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .addToBackStack("order")
                                    .add(R.id.fragmentcontainer, OrderFragment.newInstance(root.getString("username"), root.getString("userphone")), "order")
                                    .commit();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.order_fail, Toast.LENGTH_SHORT).show();
                        }
                    } else if (tag.equals("orderlist")){
                        //当接收到的信息为订单列表时
                        JSONArray list = root.getJSONArray("list");
                        userList.clear();
                        for (int i=0;i<list.length();i++){
                            JSONObject item = list.getJSONObject(i);
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("username", item.getString("username"));
                            map.put("phonenumber", item.getString("phonenumber"));
                            LatLng userpoint = new LatLng(item.getDouble("latitude"), item.getDouble("longitude"));
                            double distance = DistanceUtil.getDistance(userpoint, new LatLng(myBDLocation.getLatitude(), myBDLocation.getLongitude()));
                            map.put("distance", (int)distance+"m");
                            map.put("origin", item.getString("origin"));
                            map.put("destination", item.getString("destination"));
                            userList.add(map);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        binder = null;
    }

    //列表项的点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        binder.sendOrder(userList.get(i).get("username").toString(), getIntent().getStringExtra("username").toString());
    }

    //OrderFragment的接口调用
    @Override
    public void onFragmentInteraction(String username) {
        binder.finish(username);
        isFirstSend = true;
    }
}
