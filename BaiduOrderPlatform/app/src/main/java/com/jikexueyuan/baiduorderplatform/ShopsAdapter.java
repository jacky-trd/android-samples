package com.jikexueyuan.baiduorderplatform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * 该类为adapter类，与首页中的商家列表ListView配合使用
 */
public class ShopsAdapter extends BaseAdapter {

    //商家集合
    private List<Shop> shops;
    
    private Context context;

    public ShopsAdapter(Context context, List<Shop> shops){
        this.context = context;
        this.shops = shops;
    }

    @Override
    public int getCount() {
        return (null == shops) ? 0 : shops.size();
    }

    @Override
    public Object getItem(int i) {
        return shops.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;

        //首次则初始化，否则直接通过view.getTag()获取holder
        if(null == view){
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_shop_cell, null);
            holder.ivShopImage = (ImageView) view.findViewById(R.id.ivShopImage);
            holder.ivBaiduDelivery = (ImageView) view.findViewById(R.id.ivBaiduDelivery);
            holder.tvShopName = (TextView) view.findViewById(R.id.tvShopName);
            holder.tvCoupon = (TextView) view.findViewById(R.id.tvCoupon);
            holder.tvTicket = (TextView) view.findViewById(R.id.tvTicket);
            holder.tvOnlinePay = (TextView) view.findViewById(R.id.tvOnlinePay);
            holder.tvRepay = (TextView) view.findViewById(R.id.tvRepay);
            holder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            holder.tvSales = (TextView) view.findViewById(R.id.tvSales);
            holder.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            holder.tvDeliveryMinOrder = (TextView) view.findViewById(R.id.tvDeliveryMinOrder);
            holder.tvDeliveryCost = (TextView) view.findViewById(R.id.tvDeliveryCost);
            holder.tvAverageTime = (TextView) view.findViewById(R.id.tvAverageTime);
            holder.tvDiscountForOnlinePay = (TextView) view.findViewById(R.id.tvDiscountOnline);
            holder.tvDiscountForBaiduWallat = (TextView) view.findViewById(R.id.tvDiscountWallat);
            holder.list_home_discount_parent = view.findViewById(R.id.list_home_discount_parent);
            holder.list_home_discount_online = view.findViewById(R.id.list_home_discount_oneline);
            holder.list_home_discount_wallat = view.findViewById(R.id.list_home_discount_wallat);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        //根据商家信息，设置holder中各个控件的状态
        Shop shop = (Shop) getItem(i);

        holder.ivShopImage.setImageResource(shop.getShopImageId());
        holder.tvShopName.setText(shop.getShopName());
        holder.ratingBar.setRating(shop.getRating());
        holder.tvSales.setText(context.getString(R.string.sold) + shop.getSales() + context.getString(R.string.sold_unit));
        holder.tvDistance.setText(shop.getDistance());
        holder.tvDeliveryMinOrder.setText(context.getString(R.string.least_delivery_amount) + shop.getDeliveryMinOrder());
        holder.tvDeliveryCost.setText(context.getString(R.string.delivery_cost) + shop.getDeliveryCost());
        holder.tvAverageTime.setText(context.getString(R.string.average) + shop.getAverageTime() + context.getString(R.string.time_unit));
        holder.tvDiscountForOnlinePay.setText(shop.getDiscountForOnelinePay());
        holder.tvDiscountForBaiduWallat.setText(shop.getDiscountForBaiduWallat());

        if (shop.isBaiduDelivery()) {
            holder.ivBaiduDelivery.setVisibility(View.VISIBLE);
        }else{
            holder.ivBaiduDelivery.setVisibility(View.GONE);
        }

        if(shop.isCoupon()){
            holder.tvCoupon.setText(R.string.has_coupon);
            holder.tvCoupon.setVisibility(View.VISIBLE);
        }else {
            holder.tvCoupon.setVisibility(View.GONE);
        }

        if(shop.isTicket()){
            holder.tvTicket.setText(R.string.has_ticket);
            holder.tvTicket.setVisibility(View.VISIBLE);
        }else {
            holder.tvTicket.setVisibility(View.GONE);
        }

        if(shop.isOnlinePay()){
            holder.tvOnlinePay.setText(R.string.has_online_pay);
            holder.tvOnlinePay.setVisibility(View.VISIBLE);
        }else {
            holder.tvOnlinePay.setVisibility(View.GONE);
        }

        if(shop.isRepay()){
            holder.tvRepay.setText(R.string.has_repay);
            holder.tvRepay.setVisibility(View.VISIBLE);
        }else {
            holder.tvRepay.setVisibility(View.GONE);
        }

        if (holder.tvDiscountForOnlinePay.getText() == "" && holder.tvDiscountForBaiduWallat.getText() == "") {
            holder.list_home_discount_parent.setVisibility(View.GONE);
        } else {
            holder.list_home_discount_parent.setVisibility(View.VISIBLE);

            if (holder.tvDiscountForOnlinePay.getText() == "") {
                holder.list_home_discount_online.setVisibility(View.GONE);
            }else{
                holder.list_home_discount_online.setVisibility(View.VISIBLE);
            }

            if (holder.tvDiscountForBaiduWallat.getText() == "") {
                holder.list_home_discount_wallat.setVisibility(View.GONE);
            }else{
                holder.list_home_discount_wallat.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    //通过Holder类提高效率，防止每次都调用findViewById函数
    class ViewHolder {
        ImageView ivShopImage;
        ImageView ivBaiduDelivery;
        TextView tvShopName;
        TextView tvCoupon, tvTicket, tvOnlinePay, tvRepay;
        RatingBar ratingBar;
        TextView tvSales, tvDistance;
        TextView tvDeliveryMinOrder, tvDeliveryCost, tvAverageTime;
        TextView tvDiscountForOnlinePay, tvDiscountForBaiduWallat;
        View list_home_discount_parent, list_home_discount_online, list_home_discount_wallat;
    }
}