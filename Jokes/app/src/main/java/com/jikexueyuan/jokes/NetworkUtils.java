package com.jikexueyuan.jokes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类，用于检查网络状况
 */
public class NetworkUtils {
    /*检查网络是否连接*/
    public static boolean isNetworkConnected(Context context){
        boolean isConnected = false;
        if(context != null){
            NetworkInfo localNetworkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if(localNetworkInfo != null){
                isConnected = localNetworkInfo.isAvailable();
            }
        }
        return isConnected;
    }

    /*检查是否是WIFI*/
    public static boolean isWifi(Context context){
        boolean isWifiAvailable = false;
        NetworkInfo localNetworkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if((localNetworkInfo != null)&&(localNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)){
            isWifiAvailable = true;
        }
        return isWifiAvailable;
    }
}
