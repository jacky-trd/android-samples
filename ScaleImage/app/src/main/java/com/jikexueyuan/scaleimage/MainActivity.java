package com.jikexueyuan.scaleimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 作业十一任务三：实现双指缩放图片
 * 要求：使用多点触摸相关的API实现双指缩放图片的效果
 */
public class MainActivity extends AppCompatActivity {

    //显示缩放图像的ImageView
    private ImageView scaleView;
    //当前显示的图片
    private Bitmap bitmap;

    //当前的图像变换矩阵
    private Matrix matrix = new Matrix();
    //手指滑动时，前一次保存的图像变换矩阵
    private Matrix savedMatrix = new Matrix();

    //mode代表用户手指操作的类型：单指按下，双指按下等等
    private int mode = 0;

    //当前双指间的距离
    private float distance;
    //手指滑动时，前一次的双指间距离。用于计算缩放比例。
    private float preDistance=0f;

    //两指中点
    private PointF mid = new PointF();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取ImageView和图片
        scaleView = (ImageView) findViewById(R.id.scaleView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scale_image);

        //图片太小了。。。所以先放大一下
        matrix.setScale(2f, 2f);
        //居中显示
        moveImage2Center();
        scaleView.setImageMatrix(matrix);

        //监听Touch事件
        scaleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouchEvent(v,event);
            }
        });
    }

    /*Touch事件处理函数*/
    private boolean handleTouchEvent(View v, MotionEvent event){
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //单指按下
            case MotionEvent.ACTION_DOWN:
                mode = 1;
                break;
            //双指按下
            case MotionEvent.ACTION_POINTER_DOWN:
                preDistance = getDistance(event);
                //当两指间距大于10时，计算两指中心点
                if (preDistance > 10f) {
                    //获取双指中心点
                    mid = getMid(event);
                    //保存当前图像矩阵
                    savedMatrix.set(matrix);
                    //标记当前为双指模式
                    mode = 2;
                }
                break;
            //单指抬起
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            //双指抬起
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            //手指移动
            case MotionEvent.ACTION_MOVE:
                //如果是两指缩放，计算缩放比例
                if (mode == 2) {
                    distance = getDistance(event);
                    if (distance > 10f) {
                        float scale = distance / preDistance;
                        matrix.set(savedMatrix);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        //缩放图像，同时居中显示
        view.setImageMatrix(matrix);
        moveImage2Center();

        return true;
    }

    /*获取两指间距离*/
    private float getDistance(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        float distance = (float) Math.sqrt(x * x + y * y);//两点间的距离
        return distance;
    }

    /*获取两指间中心点*/
    public static PointF getMid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    /*使图片居中*/
    private void moveImage2Center() {

        //把当前图片映射为一个矩形，后续将操作该矩形计算中心位置等
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Matrix m = new Matrix();
        m.set(matrix);
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;

        //获取屏幕矩阵
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //获取屏幕的宽度和高度
        int screenWidth, screenHeight;
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        //获取ActionBar的高度
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
        }

        //计算当前图片纵轴中心点距离屏幕中心点的偏移
        if (height < screenHeight) {
            deltaY = (screenHeight - height) / 2 - rect.top - actionBarHeight;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < screenHeight) {
            deltaY = scaleView.getHeight() - rect.bottom;
        }

        //计算当前图片横轴中心点距离屏幕中心点的偏移
        if (width < screenWidth) {
            deltaX = (screenWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < screenWidth) {
            deltaX = screenWidth - rect.right;
        }

        //将图像居中
        matrix.postTranslate(deltaX, deltaY);
    }
}