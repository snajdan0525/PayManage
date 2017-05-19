package com.paymanage.framework.action.aciton;

import android.app.Activity;
import android.support.v4.util.ArrayMap;

import com.boqii.petlifehouse.pay.model.BqPayOrder;
import com.boqii.petlifehouse.pay.model.PayEnum;
import com.boqii.petlifehouse.pay.view.BqPayDialog;
import com.boqii.petlifehouse.user.LoginManager;

/**
 * 支付管理
 * Created by snajdan on 2016/5/18.
 */

public class PayManage {

    /**
     * 启动支付界面
     *
     * @param mActivity
     * @param mBqPay
     * @param mPayResultCallBack
     */
    public static void pay(final Activity mActivity, final BqPayOrder mBqPay, final PayResultCallBack mPayResultCallBack) {

        LoginManager.executeAfterLogin(mActivity, new Runnable() {
            @Override
            public void run() {
                new BqPayDialog(mActivity, mBqPay, mPayResultCallBack).show();
            }
        });
    }

    /**
     * 启动第三方支付
     *
     * @param mActivity
     * @param bqServiceType
     * @param payType
     * @param param
     * @param mPayResultCallBack
     */
    public static void startPay(Activity mActivity, PayEnum.BqServiceType bqServiceType, PayEnum.EscrowPayType payType, ArrayMap<String, String> param, PayResultCallBack mPayResultCallBack) {
        BqPayAction mBqPayAction = null;
        if (payType == PayEnum.EscrowPayType.ALI_PAY) {
            mBqPayAction = new AliPayAction(mActivity, bqServiceType, param, mPayResultCallBack);
        } else if (payType == PayEnum.EscrowPayType.WX_PAY) {
            mBqPayAction = new WxPayAction(mActivity, bqServiceType, param, mPayResultCallBack);
        } else if (payType == PayEnum.EscrowPayType.CMB_PAY) {
            mBqPayAction = new CmbPayAction(mActivity, bqServiceType, param, mPayResultCallBack);
        } else if (payType == PayEnum.EscrowPayType.BQ_BALANCE) {
            mBqPayAction = new BalancePayAction(mActivity, bqServiceType, param, mPayResultCallBack);
        } else if (payType == PayEnum.EscrowPayType.ALLIN_PAY) {
            mBqPayAction = new AllinPayAction(mActivity, bqServiceType, param, mPayResultCallBack);
        }
        if (mBqPayAction != null) {
            mBqPayAction.startPay();
        }
    }

}
