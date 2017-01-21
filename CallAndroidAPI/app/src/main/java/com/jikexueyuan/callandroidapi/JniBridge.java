package com.jikexueyuan.callandroidapi;

public class JniBridge {
    //本地静态函数
    public native static void showLog();

    static {
        //加载静态库
        System.loadLibrary("callandroidapi");
    }
}
