package com.jikexueyuan.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * 作业十一任务二：实现一个指南针
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //显示指南针的ImageView
    private ImageView compassView;

    //传感器
    private SensorManager sensorManager;
    private Sensor oriSensor;

    //指南针前后角度
    private float degree, preDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取传感器管理器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //获取显示指南针的ImageView
        compassView = (ImageView) findViewById(R.id.compassView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //注册传感器
        oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, oriSensor, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //反注册传感器
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //仅监听方向传感器的事件
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION:
                degree = -sensorEvent.values[0];
                //在屏幕上显示，若变化大于5度就显示变化
                if (Math.abs(degree - preDegree) > 5) {
                    RotateAnimation ra = new RotateAnimation(preDegree, degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ra.setDuration(200);
                    ra.setFillAfter(true);
                    compassView.startAnimation(ra);
                    preDegree = degree;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}