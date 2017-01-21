package com.jikexueyuan.card2d;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * 任务九作业二：实现一个2D翻转的卡片
 */
public class MainActivity extends AppCompatActivity {

    //正反面的ImageView
    private ImageView imageOneSide;
    private ImageView imageTheOtherSide;

    //正反面的动画：以父容器中心点为基准，水平方向伸缩，垂直方向不变
    private ScaleAnimation saOneSide = new ScaleAnimation(1,0,1,1, Animation.RELATIVE_TO_PARENT,0.5f,Animation.RELATIVE_TO_PARENT,0.5f);
    private ScaleAnimation saTheOtherSide = new ScaleAnimation(0,1,1,1,Animation.RELATIVE_TO_PARENT,0.5f,Animation.RELATIVE_TO_PARENT,0.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //初始化控件，同时绑定事件处理函数
        init();
    }

    //初始化控件，同时绑定事件处理函数
    private void init(){
        //获得正反面ImageView
        imageOneSide = (ImageView) findViewById(R.id.ivOneSide);
        imageTheOtherSide = (ImageView) findViewById(R.id.ivTheOtherSide);

        //设置动画时间跨度
        saOneSide.setDuration(1000);
        saTheOtherSide.setDuration(1000);

        //显示第一个ImageView，隐藏第二个
        showImageOnOneSide();

        //监听动画事件，添加处理函数
        handleAnimationEvent();

        //监听布局点击事件，添加处理函数
        handleRootViewClickEvent();
    }

    //监听动画事件，添加处理函数
    private void handleAnimationEvent(){
        saOneSide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //未实现
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(View.VISIBLE == imageOneSide.getVisibility()){
                    //释放动画对象
                    imageOneSide.setAnimation(null);

                    //隐藏正面图片，显示反面图片
                    showImageOnTheOtherSide();

                    //开始下一个图片动画
                    imageTheOtherSide.startAnimation(saTheOtherSide);
                }else{

                    //释放动画对象
                    imageTheOtherSide.setAnimation(null);

                    //隐藏反面图片，显示正面图片
                    showImageOnOneSide();

                    //开始下一个图片动画
                    imageOneSide.startAnimation(saTheOtherSide);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //未实现
            }
        });
    }

    //监听布局点击事件，添加处理函数
    private void handleRootViewClickEvent(){
        findViewById(R.id.root_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(View.VISIBLE == imageOneSide.getVisibility()){
                    //开始正面图片动画
                    imageOneSide.startAnimation(saOneSide);
                }else{
                    //开始反面图片动画
                    imageTheOtherSide.startAnimation(saOneSide);
                }
            }
        });
    }

    //显示正面图片，隐藏背面图片
    private void showImageOnOneSide(){
        imageOneSide.setVisibility(View.VISIBLE);
        imageTheOtherSide.setVisibility(View.INVISIBLE);
    }

    //显示背面图片，隐藏正面图片
    private void showImageOnTheOtherSide(){
        imageOneSide.setVisibility(View.INVISIBLE);
        imageTheOtherSide.setVisibility(View.VISIBLE);
    }
}