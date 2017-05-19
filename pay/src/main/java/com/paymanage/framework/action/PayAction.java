package com.paymanage.framework.action.aciton;

import android.app.Activity;
import android.support.v4.util.ArrayMap;

import com.paymanage.framework.model.PayEnum;
import com.paymanage.framework.model.PayInfo;
import com.paymanage.framework.model.PayResult;


/**
 * 支付基类
 * Created by snajdan on 2017/5/18.
 */
public abstract class PayAction {
    protected Activity mActivity;
    private PayResultCallBack mPayResultCallBack;
    private PayEnum.ServiceType bqServiceType;
    private PayEnum.EscrowPayType payType;
    private ArrayMap<String, String> param;

    public PayAction(Activity mActivity, PayEnum.ServiceType bqServiceType, PayEnum.EscrowPayType payType, ArrayMap<String, String> param, PayResultCallBack mPayResultCallBack) {
        this.mActivity = mActivity;
        this.bqServiceType = bqServiceType;
        this.payType = payType;
        this.param = param;
        this.mPayResultCallBack = mPayResultCallBack;
    }


    public abstract void openThirdPay(PayInfo mPayInfo);

    /**
     * 开始支付
     */
    public void startPay() {
        getPayInfo();
    }

    /**
     * 支付结果回调
     *
     * @param mPayResult
     */
    protected void callBack(PayResult mPayResult) {
        if (mPayResultCallBack != null) {
            mPayResultCallBack.callBack(mPayResult);
        }
    }

    /**
     * 读取支付信息
     */
    private void getPayInfo() {
        PayInfoDataMiner mPayInfoDataMiner = new PayInfoDataMiner();
        if (bqServiceType == PayEnum.BqServiceType.SHAP_MAIL) {
            getShopMallPayInfo(param, payType.getTypeId() + "", mPayInfoDataMiner);
        } else if (bqServiceType == PayEnum.BqServiceType.O2O) {
            getO2oPayInfo(param, payType.getTypeId() + "", mPayInfoDataMiner);
        } else if (bqServiceType == PayEnum.BqServiceType.CLUB_CARD) {
            getCardPayInfo(param, payType.getTypeId() + "", mPayInfoDataMiner);
        }
    }

    /**
     * 读取商城订单支付信息
     *
     * @param PayType
     * @param observer
     * @return
     */
    private void getShopMallPayInfo(ArrayMap<String, String> param, String PayType, DataMiner.DataMinerObserver observer) {
        if (param != null) {
            BqData.getMinerService(PayMiners.class).getShopMallPayInfo(param.get("UserId"),
                    param.get("OrderId"),
                    param.get("IsUseBalance"),
                    param.get("PayPassword"),
                    PayType,
                    observer).showLoading(mActivity).work();
        }
    }

    /**
     * 读取服务订单支付信息
     *
     * @param PayType
     * @param observer
     * @return
     */
    private void getO2oPayInfo(ArrayMap<String, String> param, String PayType, DataMiner.DataMinerObserver observer) {
        if (param != null) {
            BqData.getMinerService(PayMiners.class).getO2oPayInfo(param.get("UserId"),
                    param.get("OrderId"),
                    param.get("IsUseBalance"),
                    param.get("PayPassword"),
                    PayType,
                    observer).showLoading(mActivity).work();
        }
    }

    /**
     * 读取会员卡订单支付信息
     *
     * @param PayType
     * @param observer
     * @return
     */
    private void getCardPayInfo(ArrayMap<String, String> param, String PayType, DataMiner.DataMinerObserver observer) {
        if (param != null) {
            BqData.getMinerService(PayMiners.class).getCardPayInfo(param.get("uid"),
                    param.get("IsUseBalance"),
                    param.get("PayPassword"),
                    param.get("topup"),
                    param.get("cid"),
                    param.get("cuid"),
                    PayType,
                    observer).showLoading(mActivity).work();
        }
    }

    /**
     * 读取支付信息回调
     */

    class PayInfoDataMiner implements DataMiner.DataMinerObserver {

        @Override
        public boolean onDataError(DataMiner miner, DataMiner.DataMinerError error) {
            return false;
        }

        @Override
        public void onDataSuccess(final DataMiner miner) {
            TaskUtil.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PayMiners.PayInfoEntity mPayInfoEntity = miner.getData();
                    PayInfo mPayInfo = mPayInfoEntity.getResponseData();
                    if (mPayInfo.Type == PayEnum.EscrowPayType.BQ_BALANCE.getTypeId()) {
                        PayResult mPayResult = new PayResult();
                        mPayResult.setPayType(PayEnum.EscrowPayType.BQ_BALANCE);
                        mPayResult.setStatus(PayEnum.PayStatus.PAY_SUCCEED);
                        mPayResult.setPayInfo(mPayInfo);
                        callBack(mPayResult);
                    } else {
                        openThirdPay(mPayInfo);
                    }
                }
            });
        }
    }
}
