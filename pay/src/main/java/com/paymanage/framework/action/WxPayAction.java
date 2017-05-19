package com.paymanage.framework.action.aciton;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boqii.petlifehouse.common.eventbus.EventInterface;
import com.boqii.petlifehouse.pay.R;
import com.boqii.petlifehouse.pay.model.PayEnum;
import com.boqii.petlifehouse.pay.model.PayInfo;
import com.boqii.petlifehouse.pay.model.PayResult;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 微信支付
 * Created by snajdan on 2017/5/18.
 */
public class WxPayAction extends BqPayAction implements IWXAPIEventHandler {

    public static final String wxKey = "wx6ce20275fb3ca36b";
    private IWXAPI mIWXAPI;
    private PayInfo mPayInfo;

    public WxPayAction(Activity mActivity, PayEnum.BqServiceType bqServiceType, ArrayMap<String, String> param, PayResultCallBack mPayResultCallBack) {
        super(mActivity, bqServiceType, PayEnum.EscrowPayType.WX_PAY, param, mPayResultCallBack);
    }

    @Override
    public void openThirdPay(PayInfo mPayInfo) {
        this.mPayInfo = mPayInfo;
        if (mPayInfo != null) {
            mIWXAPI = WXAPIFactory.createWXAPI(mActivity, wxKey);
            if (mIWXAPI.isWXAppInstalled() && mIWXAPI.isWXAppSupportAPI()) {
                PayReq mPayReq = getPayReq(mPayInfo.PayMessage);
                mIWXAPI.sendReq(mPayReq);
                EventBus.getDefault().register(this);
            } else {
                Toast.makeText(mActivity, R.string.wx_no_pay_tips, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 解析支付返回的结果
     *
     * @param mWxPayEvent
     */
    @Subscribe
    public void payResultDispose(WxPayEvent mWxPayEvent) {
        EventBus.getDefault().unregister(this);
        Intent mIntent = mWxPayEvent.intent;
        mIWXAPI.handleIntent(mIntent, this);
    }

    /**
     * 返回PayReq
     *
     * @param payInfo
     * @return
     */
    private PayReq getPayReq(String payInfo) {
        if (!TextUtils.isEmpty(payInfo)) {
            JSONObject jsonObj = JSON.parseObject(payInfo);
            PayReq req = new PayReq();
            req.appId = jsonObj.getString("AppId");
            req.partnerId = jsonObj.getString("PartnerId");
            req.prepayId = jsonObj.getString("PrepayId");
            req.nonceStr = jsonObj.getString("NonceStr");
            req.timeStamp = jsonObj.getString("TimeStamp");
            req.packageValue = "Sign=WXPay";
            req.sign = jsonObj.getString("AppSignature");
            return req;
        }
        return null;
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        PayResult mPayResult = new PayResult();
        if (baseResp != null) {
            mPayResult.setStatus(baseResp.errCode == BaseResp.ErrCode.ERR_OK ? PayEnum.PayStatus.PAY_SUCCEED : PayEnum.PayStatus.PAY_FAILURE);
            mPayResult.setMsg(baseResp.errStr);
        } else {
            mPayResult.setStatus(PayEnum.PayStatus.PAY_FAILURE);
        }
        mPayResult.setPayType(PayEnum.EscrowPayType.WX_PAY);
        mPayResult.setPayInfo(mPayInfo);
        callBack(mPayResult);


    }


    public static class WxPayEvent implements EventInterface {
        private Intent intent;

        public WxPayEvent(Intent intent) {
            this.intent = intent;
        }
    }

}
