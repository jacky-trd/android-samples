package com.jikexueyuan.baiduorderplatform;

import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * 本工程代码对应任务八作业二：实现百度外卖基本样式
 * 作业要求：实现百度外卖页面显示内容，首页可以除搜索栏可以全屏上下滑动，需要实现菜单列表，图片不能出现明显的拉伸效果
 *
 * 代码说明：
 * 1，MainActivity：该类为主类，从模板Tabbed Activity生成。该类包含了三个Fragment：HomeFragment,OderFragment和MeFragment
 * 2, HomeFragment：该类对应百度外卖中的“首页”页
 * 3，OderFragment：该类对应百度外卖中的“订单”页
 * 4，MeFragment：  该类对应百度外卖中的“我的”页
 * 5，GradientIconView：该类为自定义控件，对应下边的“首页”,"订单"，“我的”三个图标。在页面间滑动时，可以实现颜色的渐变效果
 * 6，GradientIconView：该类为自定义控件，对应下边的“首页”,"订单"，“我的”三个文字。在页面间滑动时，可以实现颜色的渐变效果
 * 7，Shop：该类封装了首页商家列表中每一个商家的信息
 * 8，ShopsAdapter：该类为adapter类，与首页中的商家列表ListView配合使用
 */
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    //模板自动生成，用于包含和显示三个Fragment
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    //自定义控件的数组，对应“首页”,"订单"，“我的”三个图标和文字，实现滑动渐变效果
    private List<GradientIconView> mTabIcons = new ArrayList<GradientIconView>();
    private List<GradientTextView> mTabTexts = new ArrayList<GradientTextView>();

    //三个页面的索引常量
    final int homeIndex = 0, orderIndex = 1, meIndex = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //初始化“首页”,"订单"，“我的”三个图标和文字
        initTabIndicator();

        //绑定ViewPager和Adapter，从而包含三个Fragment
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //监听页面滑动，计算下面三个按钮各自的颜色
        mViewPager.addOnPageChangeListener(this);
    }

    //初始化“首页”,"订单"，“我的”三个图标和文字
    private void initTabIndicator() {
        //获取图标和文字控件，并添加到数组中
        GradientIconView iconHome = (GradientIconView) findViewById(R.id.id_icon_home);
        GradientIconView iconOrder = (GradientIconView) findViewById(R.id.id_icon_order);
        GradientIconView iconMe = (GradientIconView) findViewById(R.id.id_icon_me);

        GradientTextView textHome = (GradientTextView) findViewById(R.id.id_text_home);
        GradientTextView textOrder = (GradientTextView) findViewById(R.id.id_text_order);
        GradientTextView textMe = (GradientTextView) findViewById(R.id.id_text_me);

        mTabIcons.add(iconHome);
        mTabIcons.add(iconOrder);
        mTabIcons.add(iconMe);

        mTabTexts.add(textHome);
        mTabTexts.add(textOrder);
        mTabTexts.add(textMe);

        //监听点击事件，切换页面，同时变换按钮和文字颜色
        iconHome.setOnClickListener(this);
        iconOrder.setOnClickListener(this);
        iconMe.setOnClickListener(this);

        textHome.setOnClickListener(this);
        textOrder.setOnClickListener(this);
        textMe.setOnClickListener(this);

        //初始化时，设置“首页”按钮和文字为红色
        iconHome.setIconAlpha(1.0f);
        textHome.setTextViewAlpha(1.0f);
    }

    //通过SectionsPagerAdapter返回不同的Fragment
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case homeIndex:
                    return new HomeFragment();
                case orderIndex:
                    return new OrderFragment();
                case meIndex:
                    return new MeFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    //监听点击事件，切换页面，同时变换按钮和文字颜色
    @Override
    public void onClick(View view) {

        //首先将所有按钮和文字变成白色
        resetTabs();

        //点击的按钮和文字变为红色，同时切换页面
        switch (view.getId()) {
            case R.id.id_icon_home:
            case R.id.id_text_home:
                mTabIcons.get(homeIndex).setIconAlpha(1.0f);
                mTabTexts.get(homeIndex).setTextViewAlpha(1.0f);
                mViewPager.setCurrentItem(homeIndex, false);
                break;
            case R.id.id_icon_order:
            case R.id.id_text_order:
                mTabIcons.get(orderIndex).setIconAlpha(1.0f);
                mTabTexts.get(orderIndex).setTextViewAlpha(1.0f);
                mViewPager.setCurrentItem(orderIndex, false);
                break;
            case R.id.id_icon_me:
            case R.id.id_text_me:
                mTabIcons.get(meIndex).setIconAlpha(1.0f);
                mTabTexts.get(meIndex).setTextViewAlpha(1.0f);
                mViewPager.setCurrentItem(meIndex, false);
                break;
        }
    }

    //首先将所有按钮和文字变成白色
    private void resetTabs() {
        for (int i = 0; i < mTabIcons.size(); i++) {
            mTabIcons.get(i).setIconAlpha(0);
        }
        for (int i = 0; i < mTabTexts.size(); i++) {
            mTabTexts.get(i).setTextViewAlpha(0);
        }
    }

    //监听页面滑动，计算下面三个按钮各自的颜色
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (positionOffset > 0) {

            //获取滑动时左右的按钮和文字
            GradientIconView iconLeft = mTabIcons.get(position);
            GradientIconView iconRight = mTabIcons.get(position + 1);

            GradientTextView textLeft = mTabTexts.get(position);
            GradientTextView textRight = mTabTexts.get(position + 1);

            //设置颜色
            iconLeft.setIconAlpha(1 - positionOffset);
            textLeft.setTextViewAlpha(1 - positionOffset);
            iconRight.setIconAlpha(positionOffset);
            textRight.setTextViewAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //该方法未实现
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //该方法未实现
    }
}
