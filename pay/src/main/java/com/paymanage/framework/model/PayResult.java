package com.paymanage.framework.model;

/**
 * 支付结果
 * Created by snajdan on 2017/5/19.
 */

public class PayResult {
    private PayEnum.EscrowPayType payType;
    private PayEnum.PayStatus status;
    private String msg;
    private PayInfo mPayInfo;

    public PayEnum.EscrowPayType getPayType() {
        return payType;
    }

    public void setPayType(PayEnum.EscrowPayType payType) {
        this.payType = payType;
    }

    public PayEnum.PayStatus getStatus() {
        return status;
    }

    public void setStatus(PayEnum.PayStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PayInfo getPayInfo() {
        return mPayInfo;
    }

    public void setPayInfo(PayInfo mPayInfo) {
        this.mPayInfo = mPayInfo;
    }

}
