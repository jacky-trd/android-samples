package com.jikexueyuan.movebutton;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

/**
 * 本工程代码对应任务九作业一：使用视图动画及属性动画控制按钮运动
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //监听点击事件
        findViewById(R.id.btnViewAnimXML).setOnClickListener(this);
        findViewById(R.id.btnViewAnimCode).setOnClickListener(this);
        findViewById(R.id.btnObjAnimXML).setOnClickListener(this);
        findViewById(R.id.btnObjAnimCode).setOnClickListener(this);
        findViewById(R.id.ivTurnRound).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //视图动画 (XML)
            case R.id.btnViewAnimXML:
                viewAnimationXML(view);
                break;
            //视图动画 (Java Code)
            case R.id.btnViewAnimCode:
                viewAnimationCode(view);
                break;
            //属性动画 (XML)
            case R.id.btnObjAnimXML:
                objAnimatorXML(view);
                break;
            //属性动画 （Java Code）
            case R.id.btnObjAnimCode:
                objAnimatorCode(view);
                break;
            //图片3D翻转
            case R.id.ivTurnRound:
                image3DTurnRound(view);
                break;
            default:
                break;
        }
    }

    //视图动画 (XML)
    private void viewAnimationXML(View view){
        //控件放到最前，以免被遮挡
        view.bringToFront();
        view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_button));
    }

    //视图动画 (Java Code)
    private void viewAnimationCode(View view) {
        //控件放到最前，以免被遮挡
        view.bringToFront();

        AnimationSet as = new AnimationSet(true);

        TranslateAnimation taPhaseOne = new TranslateAnimation(0,200,0,0);
        taPhaseOne.setDuration(1000);
        as.addAnimation(taPhaseOne);

        TranslateAnimation taPhaseTwo = new TranslateAnimation(0,0,0,200);
        taPhaseTwo.setDuration(1000);
        taPhaseTwo.setStartOffset(1000);
        as.addAnimation(taPhaseTwo);

        view.startAnimation(as);
    }

    //属性动画 (XML)
    private void objAnimatorXML(View view){
        //控件放到最前，以免被遮挡
        view.bringToFront();

        AnimatorSet as = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.animator_button);
        as.setTarget(view);
        as.start();
    }

    //属性动画 （Java Code）
    private void objAnimatorCode(View view){
        //控件放到最前，以免被遮挡
        view.bringToFront();

        AnimatorSet as = new AnimatorSet();
        as.playSequentially(
                ObjectAnimator.ofFloat(view,"translationX",0,200).setDuration(1000),
                ObjectAnimator.ofFloat(view,"translationY",0,200).setDuration(1000),
                ObjectAnimator.ofFloat(view,"translationY",200,0).setDuration(1000),
                ObjectAnimator.ofFloat(view,"translationX",200,0).setDuration(1000));
        as.start();
    }

    //图片3D翻转
    private void image3DTurnRound(View view){
        AnimatorSet as = new AnimatorSet();
        as.setDuration(1000);
        as.playSequentially(
                ObjectAnimator.ofFloat(view,"rotationY",0f,180f),
                ObjectAnimator.ofFloat(view,"rotationY",180f,0f));
        as.start();
    }
}