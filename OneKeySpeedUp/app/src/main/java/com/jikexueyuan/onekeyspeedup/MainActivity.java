package com.jikexueyuan.onekeyspeedup;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

/**
 * 任务十一作业四：完成一键加速
 *
 * 本工程的一键加速只对android 5.0以下可用。原因是，谷歌在5.0之后关闭了ActivityManager的getRunningAppProcesses方法。
 * 该方法在5.0之后只能取到自己的进程，而取不到其它进程。所以很多专业工具的一键加速功能在5.0之后也不可用了。
 * 在和助教张浩老师讨论之后，确认本作业只要实现android 5.0以下即可。
 * 如果批改老师有好的方法可以在5.0之后实现该功能，非常感谢可以帮我指出一个方向，谢谢！
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //加速之前的系统内存
        long preAvaiMemory = getAvailMemory(MainActivity.this);

        Context context = MainActivity.this;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取后台运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();

        //获取本项目的进程
        String currentProcess = context.getApplicationInfo().processName;

        //对系统中所有正在运行的进程进行迭代，如果进程名不是当前进程，则Kill掉
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {

            String processName = appProcessInfo.processName;
            //取得各个进程的包
            String[] pkgList = appProcessInfo.pkgList;
            if (!processName.equals(currentProcess)) {
                //检查进程的重要程度
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE){
                    for (int i = 0; i < pkgList.length; i++) {
                        activityManager.killBackgroundProcesses(pkgList[i]);
                    }
                }
            }
        }

        //加速之后的系统内存
        long newAvaiMemory = getAvailMemory(MainActivity.this);
        Toast.makeText(getApplicationContext(), getString(R.string.saves) + (newAvaiMemory - preAvaiMemory) + getString(R.string.memory_m_unit), Toast.LENGTH_SHORT).show();

        //加速后关闭
        finish();
    }

    //获取可用内存大小
    private long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //返回值以 M 为单位
        return mi.availMem / (1024 * 1024);
    }
}