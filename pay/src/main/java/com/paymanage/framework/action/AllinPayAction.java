package com.paymanage.framework.action.aciton;


import android.app.Activity;
import android.support.v4.util.ArrayMap;
import com.paymanage.framework.model.PayEnum;
import com.paymanage.framework.model.PayInfo;
import com.paymanage.framework.model.PayResult;

/**
 * 通联支付
 * Created by snajdan on 2017/5/18.
 */
public class AllinPayAction extends PayAction {
    private PayInfo mPayInfo;

    public AllinPayAction(Activity mActivity, PayEnum.BqServiceType bqServiceType, ArrayMap<String, String> param, PayResultCallBack mPayResultCallBack) {
        super(mActivity, bqServiceType, PayEnum.EscrowPayType.ALLIN_PAY, param, mPayResultCallBack);
    }

    @Override
    public void openThirdPay(PayInfo mPayInfo) {
        this.mPayInfo = mPayInfo;
        if (mPayInfo != null) {
            EventBus.getDefault().register(this);
            mActivity.startActivity(AllinPayActivity.getIntent(mActivity, mPayInfo.PayMessage));
        }
    }

    /**
     * 解析支付返回的结果
     */
    @Subscribe
    public void payResultDispose(AllinPayAction.AllinPayEvent mAllinPayEvent) {
        EventBus.getDefault().unregister(this);
        PayResult mPayResult = new PayResult();
        mPayResult.setStatus(mAllinPayEvent.status);
        mPayResult.setPayType(PayEnum.EscrowPayType.ALLIN_PAY);
        mPayResult.setPayInfo(mPayInfo);
        callBack(mPayResult);
    }


    public static class AllinPayEvent implements EventInterface {
        private PayEnum.PayStatus status;

        public AllinPayEvent(PayEnum.PayStatus status) {
            this.status = status;
        }
    }
}
