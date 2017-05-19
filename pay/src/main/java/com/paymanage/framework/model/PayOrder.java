package com.paymanage.framework.model;

import android.support.v4.util.ArrayMap;

/**
 * 支付订单信息
 * Created by snajdan on 2017/5/19.
 */

public class PayOrder {

    public String orderId;
    public String money;
    public PayEnum.BqServiceType bqServiceType;
    public ArrayMap<String, String> extension;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public PayEnum.BqServiceType getBqServiceType() {
        return bqServiceType;
    }

    public void setBqServiceType(PayEnum.BqServiceType bqServiceType) {
        this.bqServiceType = bqServiceType;
    }

    public ArrayMap<String, String> getExtension() {
        return extension;
    }

    public void setExtension(ArrayMap<String, String> extension) {
        this.extension = extension;
    }

}
