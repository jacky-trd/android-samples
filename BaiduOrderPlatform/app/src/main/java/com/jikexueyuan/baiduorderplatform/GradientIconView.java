package com.jikexueyuan.baiduorderplatform;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 该类为自定义控件，对应下边的“首页”,"订单"，“我的”三个图标。在页面间滑动时，可以实现颜色的渐变效果。
 *
 * 说明：上下两个ImageView重叠在一起，通过改变上下ImageView的透明度，实现颜色的渐变
 */
public class GradientIconView extends FrameLayout {

    //上下两个重叠的ImageView
    private ImageView mTopIconView;
    private ImageView mBottomIconView;

    public GradientIconView(Context context) {
        this(context, null, 0);
    }

    public GradientIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取上下两个ImageView
        initView(context);

        //设置自定义的属性
        setAttribute(context,attrs);
    }

    //获取上下两个ImageView
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.icon_layout, this, true);
        mTopIconView = (ImageView) findViewById(R.id.top_icon_view);
        mBottomIconView = (ImageView) findViewById(R.id.bottom_icon_view);
    }

    //设置自定义的属性
    private void setAttribute(Context context, AttributeSet attrs){

        //图标
        BitmapDrawable drawable;

        //获得自定义的属性
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.GradientIconView);

        //设置属性
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.GradientIconView_top_icon) {
                drawable = (BitmapDrawable) array.getDrawable(attr);
                setTopIconView(drawable);
            } else if (attr == R.styleable.GradientIconView_bottom_icon) {
                drawable = (BitmapDrawable) array.getDrawable(attr);
                setBottomIconView(drawable);
            }
        }

        array.recycle();

        //设置为白色（上边的ImageView完全透明）
        setIconAlpha(0.0f);
    }

    //设置上边ImageView的图标
    private void setTopIconView(Drawable drawable) {
        mTopIconView.setBackgroundDrawable(drawable);
    }

    //设置下边ImageView的图标
    private void setBottomIconView(Drawable drawable) {
        mBottomIconView.setBackgroundDrawable(drawable);
    }

    //设置上下ImageView的透明度
    public void setIconAlpha(float alpha) {
        mTopIconView.setAlpha(alpha);
        mBottomIconView.setAlpha(1 - alpha);
    }
}