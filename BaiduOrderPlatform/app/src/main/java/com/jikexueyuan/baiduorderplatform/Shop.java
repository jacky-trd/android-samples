package com.jikexueyuan.baiduorderplatform;

/**
 * 该类封装了首页商家列表中每一个商家的信息
 */
public class Shop {

    //商家图片Id
    private int shopImageId;
    //该商家是否是百度配送
    private boolean isBaiduDelivery;
    //商家名称
    private String shopName;
    //是否有“券”，“票”，“付”，“陪”
    private boolean hasCoupon, hasTicket, hasOnlinePay, hasRepay;
    //商家的评价等级
    private float rating;
    //商家的销售量和距离
    private String sales, distance;
    //商家的起送额，配送费，和平均到达时间
    private String deliveryMinOrder, deliveryCost, averageTime;
    //商家的在线支付折扣，和百度钱包折扣
    private String discountForOnelinePay, discountForBaiduWallat;

    //在构造函数中赋值
    public Shop(int shopImageId, boolean isBaiduDelivery, String shopName, boolean hasCoupon, boolean hasTicket,
                boolean hasOnlinePay, boolean hasRepay, float rating, String sales, String distance, String deliveryMinOrder,
                String deliveryCost, String averageTime, String discountForOnelinePay, String discountForBaiduWallat){
        this.shopImageId = shopImageId;
        this.isBaiduDelivery = isBaiduDelivery;
        this.shopName = shopName;
        this.hasCoupon = hasCoupon;
        this.hasTicket = hasTicket;
        this.hasOnlinePay = hasOnlinePay;
        this.hasRepay = hasRepay;
        this.rating = rating;
        this.sales = sales;
        this.distance = distance;
        this.deliveryMinOrder = deliveryMinOrder;
        this.deliveryCost = deliveryCost;
        this.averageTime = averageTime;
        this.discountForOnelinePay = discountForOnelinePay;
        this.discountForBaiduWallat = discountForBaiduWallat;
    }

    //以下属性均为只读属性
    public int getShopImageId() {
        return shopImageId;
    }

    public boolean isBaiduDelivery() {
        return isBaiduDelivery;
    }

    public String getShopName() {
        return shopName;
    }

    public boolean isCoupon() {
        return hasCoupon;
    }

    public boolean isTicket() {
        return hasTicket;
    }

    public boolean isOnlinePay() {
        return hasOnlinePay;
    }

    public boolean isRepay() {
        return hasRepay;
    }

    public float getRating() {
        return rating;
    }

    public String getSales() {
        return sales;
    }

    public String getDistance() {
        return distance;
    }

    public String getDeliveryMinOrder() {
        return deliveryMinOrder;
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public String getAverageTime() {
        return averageTime;
    }

    public String getDiscountForOnelinePay() {
        return discountForOnelinePay;
    }

    public String getDiscountForBaiduWallat() {
        return discountForBaiduWallat;
    }
}
