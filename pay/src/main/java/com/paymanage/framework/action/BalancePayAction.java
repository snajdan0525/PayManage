package com.paymanage.framework.action.aciton;

import android.app.Activity;
import android.support.v4.util.ArrayMap;

import com.boqii.petlifehouse.pay.model.PayEnum;
import com.boqii.petlifehouse.pay.model.PayInfo;


/**
 * 余额支付
 * Created by snajdan on 2017/5/18.
 */
public class BalancePayAction extends PayAction {

    public BalancePayAction(Activity mActivity, PayEnum.BqServiceType bqServiceType, ArrayMap<String, String> param, PayResultCallBack mPayResultCallBack) {
        super(mActivity, bqServiceType, PayEnum.EscrowPayType.BQ_BALANCE, param, mPayResultCallBack);
    }

    @Override
    public void openThirdPay(final PayInfo mPayInfo) {

    }

}
