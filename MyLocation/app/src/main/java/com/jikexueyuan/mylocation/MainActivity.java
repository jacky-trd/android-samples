package com.jikexueyuan.mylocation;

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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * 任务十一作业一：使用百度地图定位当前位置
 *
 * 说明：
 * 1, 签名文件位置：MyLocation\app\keystore\baidumap.jks;密码：123456
 * 2，申请的百度AK值为：n7K265l2FcWsuvo7siOCfztDzfYQIyLG
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

    //标记定位的图标
    BitmapDescriptor bitmap;

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

        //设置地图参数
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

        //注册定位结果监听器，并启动定位服务
        LocationService locService = new LocationService(getApplicationContext());
        locService.registerListener(listener);
        locService.start();
    }

    //定位结果监听器，在回调函数中处理定位结果
    BDLocationListener listener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null && (bdLocation.getLocType() == onlineSuccess || bdLocation.getLocType() == offlineSuccess)) {
                //利用Handler在UI上标出定位结果
                Message locMsg = locHander.obtainMessage();
                Bundle locData = new Bundle();
                locData.putParcelable("loc", bdLocation);
                locMsg.setData(locData);
                locHander.sendMessage(locMsg);
            }
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
                baiduMap.addOverlay(option);
                baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
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