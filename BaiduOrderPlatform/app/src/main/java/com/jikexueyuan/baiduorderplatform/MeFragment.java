package com.jikexueyuan.baiduorderplatform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 该类对应百度外卖中的“我的”页
 */
public class MeFragment extends Fragment {

    //包含所有控件的LinearLayout布局
    private LinearLayout layoutPageMe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        layoutPageMe = (LinearLayout) rootView.findViewById(R.id.id_layout_me);

        //初始化LinearLayout布局
        initView();

        return rootView;
    }

    public void initView() {

        final String[] list_texts = new String[]{getActivity().getString(R.string.my_address),getActivity().getString(R.string.my_coupon),getActivity().getString(R.string.my_repay_money),
                getActivity().getString(R.string.my_message), getActivity().getString(R.string.my_favorite),getActivity().getString(R.string.my_comments), getActivity().getString(R.string.my_wallat),
                getActivity().getString(R.string.baidu_rice), getActivity().getString(R.string.common_questions)};
        final int[] list_images = new int[]{R.drawable.me_image_address, R.drawable.me_image_coupon, R.drawable.me_image_repay_money,
                R.drawable.me_image_message, R.drawable.me_image_favorite, R.drawable.me_image_comments, R.drawable.me_image_wallet,
                R.drawable.me_image_rice, R.drawable.me_image_common_question};

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        for (int i = 0; i < list_images.length; i++) {

            View view = layoutInflater.inflate(R.layout.list_cell_me, layoutPageMe, false);
            //每一行最前面的“我的信息分类”的图标
            ImageView ivIcon = (ImageView) view.findViewById(R.id.ivInfoIcon);
            ivIcon.setImageResource(list_images[i]);

            //每一行中“我的信息分类”的文字说明
            TextView tvCategory = (TextView) view.findViewById(R.id.tvInfoCategory);
            tvCategory.setText(list_texts[i]);

            //每一行中最右边的箭头图标
            ImageView ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
            ivArrow.setImageResource(R.drawable.me_icon_right_arrow);
            layoutPageMe.addView(view);

            //添加横隔条（间距）
            if (i == 2 || i == 5 || i == 7 || i == 8) {
                View viewMargin = layoutInflater.inflate(R.layout.list_cell_me_graybar, layoutPageMe, false);
                layoutPageMe.addView(viewMargin);
            }
        }

        //添加客服联系信息
        View viewPhoneCall = layoutInflater.inflate(R.layout.list_cell_me_call, layoutPageMe, false);
        layoutPageMe.addView(viewPhoneCall);
    }
}