package com.jikexueyuan.mylocation;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Happiness on 2016/11/7.
 */
public class LocationEvent {
    //位置信息
    private LatLng mLatLng;
    //是提交服务器还是从服务器接收
    private boolean isPublish;

    public LocationEvent(LatLng latLng, boolean isPublish) {
        mLatLng = latLng;
        this.isPublish = isPublish;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }

    public boolean isPublish() {
        return isPublish;
    }

    public void setPublish(boolean publish) {
        isPublish = publish;
    }
}
