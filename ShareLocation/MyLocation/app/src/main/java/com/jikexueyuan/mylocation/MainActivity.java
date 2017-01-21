package com.jikexueyuan.mylocation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 任务十四作业一：实现一个实时共享位置的应用
 *
 * 签名说明：
 * 1, 签名文件位置：MyLocation\app\keystore\baidumap.jks;密码：123456
 * 2，申请的百度AK值为：n7K265l2FcWsuvo7siOCfztDzfYQIyLG
 *
 * 代码说明：
 * 本代码基于之前的百度地图作业继续完成：
 * 1，LocationEvent：EventBus传递时的参数
 * 2，LocationService：百度定位服务
 * 3，MainActivity：主界面，用于显示地图和位置
 * 4，NetworkService：与服务器连接通信的服务
 */
public class MainActivity extends AppCompatActivity {
    //百度地图控件及对象
    private MapView mapView = null;
    private BaiduMap baiduMap;

    //百度的定位服务
    private LocationService locService;

    //百度定位结果代码：66为离线定位成功，161为在线定位成功
    private final int offlineSuccess = 66;
    private final int onlineSuccess = 161;

    //标记自己定位的图标
    private BitmapDescriptor bitmap;
    //标记别人定位的图标
    private BitmapDescriptor bitmapOthers;

    private Overlay myOverlay = null;
    private Overlay otherOverlay = null;

    //用于测试的代码，改变自己的位置
    //private float a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //获取百度地图对象
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();

        //获取百度定位图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.location);
        bitmapOthers = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

        //设置地图参数
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

        //注册定位结果监听器，并启动定位服务
        locService = new LocationService(getApplicationContext());
        locService.registerListener(listener);
        locService.start();

        //启动网络服务
        startService(new Intent(this,NetworkService.class));

        //注册总线
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onLocationEvent(LocationEvent event) {
        //如果是从服务器接收的事件则添加标记
        if (!event.isPublish()) {
            LatLng point = event.getLatLng();
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmapOthers);
            if(otherOverlay != null){
                otherOverlay.remove();
            }
            otherOverlay = baiduMap.addOverlay(option);
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
        }
    }

    //定位结果监听器，在回调函数中处理定位结果
    BDLocationListener listener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //根据老师建议，此处判断删掉，从而可以在模拟器上测试
            //if (bdLocation != null && (bdLocation.getLocType() == onlineSuccess || bdLocation.getLocType() == offlineSuccess)) {
                //利用Handler在UI上标出定位结果
                Message locMsg = locHander.obtainMessage();
                Bundle locData = new Bundle();
                locData.putParcelable("loc", bdLocation);
                locMsg.setData(locData);
                locHander.sendMessage(locMsg);
            //}
        }
    };

    //修改UI，标出定位结果
    private Handler locHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //获取定位结果
            BDLocation location = msg.getData().getParcelable("loc");
            //如果不为空则标记在地图上
            if (location != null) {
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                //baiduMap.clear();
                if(myOverlay != null){
                    myOverlay.remove();
                }
                myOverlay = baiduMap.addOverlay(option);
                baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

                //用于测试的代码，改变自己的位置
                //point = new LatLng(location.getLatitude() + (a+=0.1), location.getLongitude());

                //向服务器发送自身位置
                EventBus.getDefault().post(new LocationEvent(point, true));
            }
        }
    };

    //地图生命周期管理
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locService.unregisterListener(listener);
        locService.stop();
        mapView.onDestroy();
        //注销总线
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}