package com.paymanage.framework.action.aciton;

import com.paymanage.framework.model.PayResult;

/**
 * 支付结果回调
 * Created by snajdan on 2017/5/18.
 */
public interface PayResultCallBack {
    public void callBack(PayResult mPayResult);
}
