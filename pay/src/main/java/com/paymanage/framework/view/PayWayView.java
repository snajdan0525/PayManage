package com.paymanage.framework.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boqii.android.framework.data.BqData;
import com.boqii.android.framework.data.DataMiner;
import com.boqii.android.framework.data.entity.BaseDataEntity;
import com.boqii.android.framework.util.TaskUtil;
import com.boqii.petlifehouse.pay.R;
import com.boqii.petlifehouse.pay.model.PayCouponInfo;
import com.boqii.petlifehouse.pay.model.PayEnum;
import com.boqii.petlifehouse.pay.service.PayMiners;

/**
 * 支付方式
 * Created by zhangbp on 2016/11/11.
 */
public class PayWayView extends LinearLayout implements View.OnClickListener {
    private PayWayCallBack mPayWayCallBack;
    private int[][] itemIds = new int[][]{{R.id.alipay_box, R.id.alipay_check}, {R.id.wechat_box, R.id.wechat_check}, {R.id.cmbc_box, R.id.cmbc_check},{R.id.allinpay_box, R.id.allinpay_check}};
    private PayEnum.EscrowPayType[] types = new PayEnum.EscrowPayType[]{PayEnum.EscrowPayType.ALI_PAY, PayEnum.EscrowPayType.WX_PAY, PayEnum.EscrowPayType.CMB_PAY,PayEnum.EscrowPayType.ALLIN_PAY};
    private int checkIndex = -1;
    private boolean isSwitchable = true;

    public PayWayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < itemIds.length; i++) {
            View boxView = findViewById(itemIds[i][0]);
            boxView.setOnClickListener(this);
            boxView.setTag(i + "");
        }
        getCouponInfo();
    }

    /**
     * 是否可以选择
     *
     * @param isSwitchable
     */
    public void setSwitchable(boolean isSwitchable) {
        this.isSwitchable = isSwitchable;
        if (!isSwitchable) {
            switchPayWay(-1);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isSwitchable) return;
        int index = Integer.parseInt(v.getTag() + "");
        switchPayWay(index);
    }

    /**
     * 选择支付方式
     *
     * @param index
     */
    private void switchPayWay(int index) {
        if (checkIndex == index) {
            if (index > -1) {
                findViewById(itemIds[index][1]).setVisibility(View.INVISIBLE);
            }
            checkIndex = -1;
        } else {
            if (checkIndex > -1) {
                findViewById(itemIds[checkIndex][1]).setVisibility(View.INVISIBLE);
            }
            if (index > -1) {
                findViewById(itemIds[index][1]).setVisibility(View.VISIBLE);
            }
            checkIndex = index;
        }
        if (mPayWayCallBack != null) {
            mPayWayCallBack.callBack(checkIndex == -1 ? null : types[index]);
        }
    }

    /**
     * 读取优惠信息
     */
    private void getCouponInfo() {
        BqData.getMinerService(PayMiners.class).getCouponInfo(new DataMiner.DataMinerObserver() {
            @Override
            public boolean onDataError(DataMiner miner, DataMiner.DataMinerError error) {
                return true;
            }

            @Override
            public void onDataSuccess(final DataMiner miner) {
                TaskUtil.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseDataEntity<PayCouponInfo> data = miner.getData();
                        String cmbc = data.getResponseData().getCMB();
                        if (!TextUtils.isEmpty(cmbc)) {
                            TextView tipsView = ((TextView) findViewById(R.id.cmb_coupon_tips));
                            tipsView.setText(cmbc);
                            tipsView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).work();
    }

    /**
     * 设置PayWayCallBack
     *
     * @param mPayWayCallBack
     */
    public void setPayWayCallBack(PayWayCallBack mPayWayCallBack) {
        this.mPayWayCallBack = mPayWayCallBack;
    }

    /**
     * 选择支付方式回调
     */
    public interface PayWayCallBack {
        public void callBack(PayEnum.EscrowPayType payType);
    }
}
