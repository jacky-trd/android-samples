package com.jikexueyuan.cloudnote.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.ui.fragment.ConfigFragment;
import com.jikexueyuan.cloudnote.ui.fragment.NoteFragment;
import com.jikexueyuan.cloudnote.ui.widget.TabView;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 *主Activity，用于显示所有笔记
 */
public class MainActivity extends BaseActivity {
    //底部两个“笔记”和“设置”按钮的相关变量
    private String[] mTitle = {"笔记", "设置"};
    private int[] mIconSelect = {R.drawable.docker_tab_doc_selected, R.drawable.docker_tab_setting_selected};
    private int[] mIconNormal = {R.drawable.docker_tab_doc_normal, R.drawable.docker_tab_setting_normal};

    //中间用于显示fragment的viewpager
    private ViewPager mViewPager ;
    //底部用于显示“笔记”和“设置”按钮的tabview
    private TabView mTabView ;
    //“笔记”和“设置”两个fragment的map
    private Map<Integer,Fragment> mFragmentMap ;

    //重写父类视图初始化
    @Override
    protected void initView() {
        mFragmentMap = new HashMap<>() ;

        //初始化view pager
        mViewPager = (ViewPager)findViewById(R.id.id_view_pager) ;
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));

        //初始化tabview
        mTabView = (TabView)findViewById(R.id.id_tab) ;
        mTabView.setViewPager(mViewPager);

        Logger.d("您的用户名是："+ BmobUser.getCurrentUser().getUsername());
    }

    //重写父类数据初始化，无逻辑
    @Override
    protected void initData() {}

    //重写父类监听器初始化，无逻辑
    @Override
    protected void setListeners() {}

    //重写父类获取布局id
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private Fragment getFragment(int position){
        Fragment fragment = mFragmentMap.get(position) ;
        if(fragment == null){
            switch (position){
                case 0:
                    fragment = new NoteFragment() ;
                    break ;
                case 1:
                    fragment = new ConfigFragment();
                    break ;
            }
            mFragmentMap.put(position,fragment) ;
        }
        return fragment ;
    }

    class PageAdapter extends FragmentPagerAdapter implements TabView.OnItemIconTextSelectListener{

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }
        @Override
        public int[] onIconSelect(int position) {
            int icon[] = new int[2] ;
            icon[0] = mIconSelect[position] ;
            icon[1] = mIconNormal[position] ;
            return icon;
        }
        @Override
        public String onTextSelect(int position) {
            return mTitle[position];
        }

        @Override
        public int getCount() {
            return mTitle.length;
        }
    }
}
