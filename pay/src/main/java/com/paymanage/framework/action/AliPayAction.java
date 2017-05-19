package com.paymanage.framework.action.aciton;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.boqii.android.framework.util.TaskUtil;
import com.boqii.petlifehouse.pay.model.PayEnum;
import com.boqii.petlifehouse.pay.model.PayInfo;
import com.boqii.petlifehouse.pay.model.PayResult;


/**
 * 支付宝支付
 * Created by snajdan on 2017/5/18.
 */
public class AliPayAction extends BqPayAction {
    private PayEnum.BqServiceType bqServiceType;
    private PayInfo mPayInfo;

    public AliPayAction(Activity mActivity, PayEnum.BqServiceType bqServiceType, ArrayMap<String, String> param, PayResultCallBack mPayResultCallBack) {
        super(mActivity, bqServiceType, PayEnum.EscrowPayType.ALI_PAY, param, mPayResultCallBack);
    }

    @Override
    public void openThirdPay(final PayInfo mPayInfo) {
        this.mPayInfo = mPayInfo;
        if (mPayInfo != null) {
            new Thread() {
                @Override
                public void run() {
                    PayTask mPayTask = new PayTask(mActivity);
                    final String result;
                    result = mPayTask.pay(mPayInfo.PayMessage);
                    TaskUtil.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            payResultDispose(result);
                        }
                    });
                }
            }.start();
        }
    }

    /**
     * 解析支付返回的结果
     *
     * @param rawResult
     */
    public void payResultDispose(String rawResult) {
        PayResult mPayResult = new PayResult();
        if (!TextUtils.isEmpty(rawResult)) {
            String[] resultParams = rawResult.split(";");
            for (String resultParam : resultParams) {
                if (resultParam.startsWith("resultStatus")) {
                    String resultStatus = gatValue(resultParam, "resultStatus");
                    PayEnum.PayStatus status = TextUtils.equals(resultStatus, "9000") ? PayEnum.PayStatus.PAY_SUCCEED : PayEnum.PayStatus.PAY_FAILURE;
                    mPayResult.setStatus(status);
                }
                if (resultParam.startsWith("memo")) {
                    mPayResult.setMsg(gatValue(resultParam, "memo"));
                }
            }
        } else {
            mPayResult.setStatus(PayEnum.PayStatus.PAY_FAILURE);
        }
        mPayResult.setPayType(PayEnum.EscrowPayType.ALI_PAY);
        mPayResult.setPayInfo(mPayInfo);
        callBack(mPayResult);
    }

    /**
     * 返回value
     *
     * @param content
     * @param key
     * @return
     */
    private String gatValue(String content, String key) {
        String prefix = key + "={";
        return content.substring(content.indexOf(prefix) + prefix.length(),
                content.lastIndexOf("}"));
    }

}
