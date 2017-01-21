package com.jikexueyuan.taxibookingclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 地图和预约界面Activity
 */
public class MapActivity extends AppCompatActivity implements BDLocationListener, View.OnClickListener, AddressFragment.OnFragmentInterfaceListener, OrderFragment.OnFragmentInteractionListener, ServiceConnection, CompoundButton.OnCheckedChangeListener {

    //百度地图相关
    private TextureMapView textureMapView;
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private MyLocationData myLocationData;
    private BDLocation myBDLocation;
    private boolean isFirstLoc = true;
    private String myCity;

    //起始地，目的地，预约按钮
    private Button originBtn;
    private Button destinationBtn;
    private ToggleButton setBtn;

    //网络服务
    private NetService.ServiceBinder binder;

    //电话号码和用户名字符串
    private String phoneNumber;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //百度地图SDK初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        //App自身初始化
        init();
    }

    //App自身初始化
    private void init(){
        //百度地图相关设置
        textureMapView = (TextureMapView) findViewById(R.id.baidu_map);
        baiduMap = textureMapView.getMap();
        baiduMap.setMyLocationEnabled(true);
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
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //其他UI控件设置
        originBtn = (Button) findViewById(R.id.origin_button);
        originBtn.setOnClickListener(this);
        destinationBtn = (Button) findViewById(R.id.destination_button);
        destinationBtn.setOnClickListener(this);
        setBtn = (ToggleButton) findViewById(R.id.send_button);
        setBtn.setOnCheckedChangeListener(this);

        //获取传递参数
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phonenumber");
        username = intent.getStringExtra("username");

        //启动服务
        bindService(new Intent(this, NetService.class), this, BIND_AUTO_CREATE);
    }

    //百度定位响应函数
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        //获取百度定位，并提取所在城市，默认北京
        myBDLocation = bdLocation;
        myCity = ((bdLocation.getCity() != null)?bdLocation.getCity() : "beijing");

        if (bdLocation == null || textureMapView == null){
            return;
        } else {
            //构建自身定位信息
            myLocationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(100)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            //设置地图
            baiduMap.setMyLocationData(myLocationData);

            //如果是首次定位，则设置地图经纬度，地图尺寸信息
            if (isFirstLoc){
                isFirstLoc = false;
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
                baiduMap.animateMapStatus(mapStatusUpdate);
            }
        }
    }

    //起始地和目的地按钮响应函数
    @Override
    public void onClick(View view) {

        //启动起始地和目的地的fragment，并将transaction加入到back stack，从而按回退键的时候可以返回主地图
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit);

        switch (view.getId()) {
            case R.id.origin_button:
                transaction
                        .addToBackStack("origin")
                        .add(R.id.fragment_container, AddressFragment.newInstance(myCity), "origin");
                break;
            case R.id.destination_button:
                transaction
                        .addToBackStack("destination")
                        .add(R.id.fragment_container, AddressFragment.newInstance(myCity), "destination");
                break;
            default:
                break;
        }
        transaction.commit();
    }

    //预约按钮响应函数
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b){
            //当ToggleButton处于选中状态时，发送订单信息
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("origin", originBtn.getText().toString());
            orderMap.put("destination", destinationBtn.getText().toString());
            orderMap.put("phonenumber", phoneNumber);
            orderMap.put("username", username);
            orderMap.put("latitude", myBDLocation.getLatitude());
            orderMap.put("longitude", myBDLocation.getLongitude());

            binder.sendOrder(orderMap);
        } else {
            //当ToggleButton处于未选中状态时，取消订单
            //通过重置Socket的连接状态来取消订单
            unbindService(this);
            bindService(new Intent(this, NetService.class), this, BIND_AUTO_CREATE);
        }
    }

    //继承AddressFragment内部的接口，获取AddressFragment返回值
    @Override
    public void onFragmentInterface(String data, String tag) {
        switch (tag) {
            case "origin":
                //修改起始地按钮text
                originBtn.setText(data);
                break;
            case "destination":
                //修改目的地按钮text
                destinationBtn.setText(data);
                break;
            default:
                break;
        }
    }

    //继承OrderFragment的内部接口，当OrderFragment返回时，修改预约按钮状态
    @Override
    public void onFragmentInteraction() {
        setBtn.setChecked(false);
    }

    //服务连接响应函数
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        //当服务连接后，开始获取百度定位信息
        locationClient.requestLocation();

        //设置服务回调函数
        binder = (NetService.ServiceBinder) iBinder;
        binder.getService().setCallback(new NetService.ServiceCallback() {
            @Override
            public void onDataChange(String data) {
                try {
                    //获取服务器端发来的信息类型
                    JSONObject root = new JSONObject(data);
                    String tag = root.getString("tag");
                    System.out.println("tag is"+tag);

                    //根据接收到的信息类型进行不同的操作
                    if (tag.equals("order")) {
                        //当司机接单时，启动订单fragment
                        startFragment(OrderFragment.newInstance(root.getString("drivername"),root.getString("phonenumber")),"orderfragment");
                    } else if (tag.equals("driverlist")) {
                        //当接受的信息为司机列表时，在地图上添加司机位置
                        JSONArray list = root.getJSONArray("list");
                        baiduMap.clear();
                        for (int i=0;i<list.length();i++) {
                            JSONObject item = list.getJSONObject(i);
                            addDriverPosition(item.getDouble("latitude"), item.getDouble("longitude"));
                        }
                    } else if (tag.equals("finish")){
                        //当接收到订单完成时，修改按钮状态
                        setBtn.setChecked(false);
                        originBtn.setText(R.string.origin_button);
                        destinationBtn.setText(R.string.destination_button);
                        getSupportFragmentManager().popBackStack();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //服务断开响应函数
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        binder = null;
    }

    //启动fragment
    private void startFragment(Fragment fragment, String backStackTag){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
                .addToBackStack(backStackTag)
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    //在地图上标记司机位置
    private void addDriverPosition(double latitude, double longitude){
        OverlayOptions overlayOptions = new DotOptions()
                .center(new LatLng(latitude, longitude))
                .radius((int)getResources().getDimension(R.dimen.mark_radius))
                .color(getResources().getColor(R.color.mark_color));
        baiduMap.addOverlay(overlayOptions);
    }

    //其它生命周期函数
    @Override
    protected void onPause() {
        super.onPause();
        textureMapView.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textureMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        textureMapView.onDestroy();
        textureMapView = null;
        unbindService(this);
    }
}
