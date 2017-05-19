package com.paymanage.framework.model;

/**
 * 支付优惠信息
 * Created by snajdan on 2017/5/19.
 */

public class PayCouponInfo {
    private String CMB;
    private String ICBC;

    public String getCMB() {
        return CMB;
    }

    public void setCMB(String CMB) {
        this.CMB = CMB;
    }

    public String getICBC() {
        return ICBC;
    }

    public void setICBC(String ICBC) {
        this.ICBC = ICBC;
    }
}
