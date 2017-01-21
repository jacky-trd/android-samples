package com.jikexueyuan.baiduorderplatform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类对应百度外卖中的“首页”页
 */
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //生成给商家分类的View，包含：餐饮，超市购，水果生鲜，下午茶等等
        View viewGrid = createShopCategoryView();
        //生产商家信息集合
        List<Shop> shops = createShopsList();

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //获取商家列表
        ListView listView = (ListView) rootView.findViewById(R.id.lvHome);
        //将商家列表与商家分类绑定到一起
        listView.addHeaderView(viewGrid);

        //设置适配器Adapter
        ShopsAdapter myAdapter = new ShopsAdapter(getActivity(), shops);
        listView.setAdapter(myAdapter);

        return rootView;
   }

    //生成给商家分类的View，包含：餐饮，超市购，水果生鲜，下午茶等等
    private View createShopCategoryView(){

        //商家分类的图标
        final int[] grid_icon = new int[] {R.drawable.home_icon_food_drink,R.drawable.home_icon_super_market,R.drawable.home_icon_fruit,R.drawable.home_icon_coffee,
                R.drawable.home_icon_rich,R.drawable.home_icon_new_shop,R.drawable.home_icon_baidu_delivery,R.drawable.home_icon_hot_pot};
        //商家分类的字符说明
        final String[] grid_text= new String[]{getActivity().getString(R.string.food_and_drink),getActivity().getString(R.string.super_market),getActivity().getString(R.string.fruit),
                getActivity().getString(R.string.coffee),getActivity().getString(R.string.rich),getActivity().getString(R.string.new_shop),getActivity().getString(R.string.baidu_delivery),
                getActivity().getString(R.string.hot_pot)};

        //设置适配器Adapter
        List<Map<String,Object>> grid_data = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < grid_icon.length;++i) {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(getActivity().getString(R.string.tag_image),grid_icon[i]);
            map.put(getActivity().getString(R.string.tag_text),grid_text[i]);
            grid_data.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),grid_data,R.layout.grid_shop_cell,new String[]{"image","text"},
                new int[]{R.id.ivShopCategory,R.id.tvShopCategory});

        //获取GridView
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View viewGrid = layoutInflater.inflate(R.layout.home_head_view,null);
        GridView gridView = (GridView) viewGrid.findViewById(R.id.gvShopCategory);

        //绑定GridView与适配器Adapter
        gridView.setAdapter(simpleAdapter);

        return viewGrid;
    }

    //生产商家信息集合
    private List<Shop> createShopsList(){

        List<Shop> shops = new ArrayList<Shop>();
        shops.add(new Shop(R.drawable.home_image_shop_burgerking, true, getActivity().getString(R.string.burger_king), true, true, true, true, 4.7f, "1378", "990m", "30", "5", "35", getActivity().getString(R.string.discount_online_pay), ""));
        shops.add(new Shop(R.drawable.home_image_shop_pizza, true, getActivity().getString(R.string.pizza), false, false, true, true, 4.2f, "221", "1.0km", "20", "12", "41", "", getActivity().getString(R.string.discount_baidu_wallat)));
        shops.add(new Shop(R.drawable.home_image_shop_spicychicken, false, getActivity().getString(R.string.spicy_chicken), true, true, false, false, 3.5f, "846", "500m", "30", "5", "35", "", ""));
        shops.add(new Shop(R.drawable.home_image_shop_boiledchicken, true, getActivity().getString(R.string.boiled_chicken), true, true, false, true, 4.0f, "1418", "1.2m", "20", "0", "50", getActivity().getString(R.string.discount_online_pay), getActivity().getString(R.string.discount_baidu_wallat)));
        shops.add(new Shop(R.drawable.home_image_shop_bonesoup, true, getActivity().getString(R.string.bone_soup), true, true, true, true, 3.1f, "2842", "1.8km", "20", "1", "60", "", ""));
        shops.add(new Shop(R.drawable.home_image_shop_dicos, true, getActivity().getString(R.string.dicos), true, true, true, true, 4.7f, "611", "900m", "20", "5", "35", "", getActivity().getString(R.string.discount_baidu_wallat)));
        shops.add(new Shop(R.drawable.home_image_shop_famouskitchen, false, getActivity().getString(R.string.famous_kitchen), true, true, true, true, 2.5f, "944", "620m", "20", "8", "35", getActivity().getString(R.string.discount_online_pay), ""));
        shops.add(new Shop(R.drawable.home_image_shop_curryfood, false, getActivity().getString(R.string.curry_food), true, true, true, true, 4.2f, "855", "1.8km", "25", "5", "35", "", ""));
        shops.add(new Shop(R.drawable.home_image_shop_japanesefood, true, getActivity().getString(R.string.japanese_food), true, true, true, true, 4.8f, "695", "800m", "30", "5", "35", getActivity().getString(R.string.discount_online_pay), getActivity().getString(R.string.discount_baidu_wallat)));

        return shops;
    }
}