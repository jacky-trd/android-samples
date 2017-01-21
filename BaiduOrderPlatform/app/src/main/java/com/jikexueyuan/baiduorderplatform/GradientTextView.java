package com.jikexueyuan.baiduorderplatform;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 该类为自定义控件，对应下边的“首页”,"订单"，“我的”三个文字。在页面间滑动时，可以实现颜色的渐变效果。
 *
 * 说明：上下两个TextView重叠在一起，通过改变上下TextView的透明度，实现颜色的渐变
 */
public class GradientTextView extends FrameLayout {

    private TextView mTopTextView;
    private TextView mBottomTextView;

    public GradientTextView(Context context) {
        this(context, null, 0);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取上下两个TextView
        initView(context);

        //设置自定义的属性
        setAttribute(context,attrs);
    }

    //获取上下两个TextView
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.text_layout, this, true);
        mTopTextView = (TextView) findViewById(R.id.top_text_view);
        mBottomTextView = (TextView) findViewById(R.id.bottom_text_view);
    }

    //设置自定义的属性
    private void setAttribute(Context context, AttributeSet attrs){

        //文字颜色
        int color;

        //获得自定义的属性
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.GradientTextView);

        //设置属性
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if(attr == R.styleable.GradientTextView_top_text_color) {
                color = array.getColor(attr, Color.TRANSPARENT);
                setTopTextViewColor(color);
            }
            else if(attr == R.styleable.GradientTextView_bottom_text_color) {
                color = array.getColor(attr, Color.BLACK);
                setBottomTextViewColor(color);
            }
            else if(attr == R.styleable.GradientTextView_text) {
                String text = array.getString(attr);
                setGradientText(text);
            }
            else if(attr == R.styleable.GradientTextView_text_size) {
                int textSize = (int) array.getDimension(attr, TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                                getResources().getDisplayMetrics()));
                setGradientTextSize(textSize);
            }
        }

        array.recycle();

        //设置为白色（上边的TextView完全透明）
        setTextViewAlpha(0.0f);
    }

    //设置上边文字的颜色
    private void setTopTextViewColor(int color) {
        mTopTextView.setTextColor(color);
    }

    //设置下边文字的颜色
    private void setBottomTextViewColor(int color) {
        mBottomTextView.setTextColor(color);
    }

    //设置文字内容
    private void setGradientText(String text) {
        mTopTextView.setText(text);
        mBottomTextView.setText(text);
    }

    //设置文字大小
    private void setGradientTextSize(int textSize) {
        mTopTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mBottomTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    //设置上下文字的透明度
    public void setTextViewAlpha(float alpha) {
        mTopTextView.setAlpha(alpha);
        mBottomTextView.setAlpha(1 - alpha);
    }
}
