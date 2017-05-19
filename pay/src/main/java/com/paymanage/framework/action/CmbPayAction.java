package com.paymanage.framework.action.aciton;

import android.app.Activity;
import android.support.v4.util.ArrayMap;

import com.boqii.petlifehouse.common.eventbus.EventInterface;
import com.boqii.petlifehouse.pay.activity.CmbPayActivity;
import com.boqii.petlifehouse.pay.model.PayEnum;
import com.boqii.petlifehouse.pay.model.PayInfo;
import com.boqii.petlifehouse.pay.model.PayResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 招商银行支付
 * Created by snajdan on 2017/5/18.
 */
public class CmbPayAction extends BqPayAction {
    private PayInfo mPayInfo;

    public CmbPayAction(Activity mActivity, PayEnum.BqServiceType bqServiceType, ArrayMap<String, String> param, PayResultCallBack mPayResultCallBack) {
        super(mActivity, bqServiceType, bqServiceType == PayEnum.BqServiceType.O2O ? PayEnum.EscrowPayType.O2O_CMB_PAY : PayEnum.EscrowPayType.CMB_PAY, param, mPayResultCallBack);
    }

    @Override
    public void openThirdPay(PayInfo mPayInfo) {
        this.mPayInfo = mPayInfo;
        if (mPayInfo != null) {
            mActivity.startActivity(CmbPayActivity.getIntent(mActivity, mPayInfo.PayMessage));
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 解析支付返回的结果
     */
    @Subscribe
    public void payResultDispose(CmbPayEvent mCmbPayEvent) {
        EventBus.getDefault().unregister(this);
        PayResult mPayResult = new PayResult();
        mPayResult.setStatus(mCmbPayEvent.status);
        mPayResult.setPayType(PayEnum.EscrowPayType.CMB_PAY);
        mPayResult.setPayInfo(mPayInfo);
        callBack(mPayResult);
    }


    public static class CmbPayEvent implements EventInterface {
        private PayEnum.PayStatus status;

        public CmbPayEvent(PayEnum.PayStatus status) {
            this.status = status;
        }
    }

}
