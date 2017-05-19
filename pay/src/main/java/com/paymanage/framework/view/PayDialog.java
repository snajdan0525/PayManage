package com.paymanage.framework.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.boqii.android.framework.ui.viewpager.BasePagerAdapter;
import com.boqii.android.framework.ui.viewpager.BqViewPager;
import com.boqii.android.framework.util.ContextUtil;
import com.boqii.petlifehouse.common.ui.dialog.DialogView;
import com.boqii.petlifehouse.pay.R;
import com.boqii.petlifehouse.pay.action.PayResultCallBack;
import com.boqii.petlifehouse.pay.model.BqPayOrder;
import com.boqii.petlifehouse.pay.model.PayResult;
import com.boqii.petlifehouse.user.util.UserInfoManager;

/**
 * 支付dialog
 * Created by snajdan on 2016/11/9.
 */
public class PayDialog extends DialogView implements PayResultCallBack {
    private BqViewPager mBqViewPager;
    private PayMainView mPayMainView;
    private PayResultCallBack mPayResultCallBack;
    private PayResult mPayResult;
    private BqPayOrder mBqPay;

    public PayDialog(Context mContext, BqPayOrder mBqPay, PayResultCallBack mPayResultCallBack) {
        super(mContext, com.boqii.petlifehouse.common.R.style.DialogThemeDefalut, R.layout.pay_box_layout);
        setGravity(Gravity.BOTTOM);
        this.mPayResultCallBack = mPayResultCallBack;
        this.mBqPay = mBqPay;
        mBqViewPager = (BqViewPager) getView();
        mBqViewPager.setAdapter(new BasePagerAdapter() {
            @Override
            protected View getView(Context context, int position) {
                View contentView = null;
                if (position == 0) {
                    contentView = getPayMainView();
                } else if (position == 1) {
                    contentView = getInputPayPassWordView();
                }
                return contentView;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        mBqViewPager.setScrollable(false);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 返回支付主界面
     *
     * @return
     */
    private PayMainView getPayMainView() {
        mPayMainView = (PayMainView) LayoutInflater.from(context).inflate(R.layout.pay_layout, mBqViewPager, false);
        mPayMainView.setBqPayOrder(mBqPay, PayDialog.this);
        return mPayMainView;
    }

    /**
     * 返回输入支付密码界面
     *
     * @return
     */
    private InputPayPassWordView getInputPayPassWordView() {
        InputPayPassWordView mInputPayPassWordView = (InputPayPassWordView) LayoutInflater.from(context).inflate(R.layout.input_passwrod, mBqViewPager, false);
        mInputPayPassWordView.setInputPassWordCallBack(new InputPayPassWordView.InputPassWordCallBack() {
            @Override
            public void callBack(String passWord) {
                mPayMainView.setBalancePay(!TextUtils.isEmpty(passWord), passWord);
            }
        });
        return mInputPayPassWordView;
    }

    @Override
    public DialogView show() {
        super.show();
        final Dialog mDialog = getBv();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mPayResultCallBack != null) {
                    mPayResultCallBack.callBack(mPayResult);
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
                lp.height = mBqViewPager.getHeight();

                Activity activity = ContextUtil.getActivity(context);

                if (activity != null && !activity.isFinishing()) {
                    mDialog.getWindow().setAttributes(lp);
                }
            }
        }, 500);
        return this;
    }

    /**
     * 支付后回调
     *
     * @param mPayResult
     */
    @Override
    public void callBack(PayResult mPayResult) {
        this.mPayResult = mPayResult;
        //if (mPayResult != null && mPayResult.getStatus() == PayEnum.PayStatus.PAY_SUCCEED) { 按产品要求修改第三方支付失败，直接返回，不能切换支付方式 （ios也这样），注释防以后改回来
        UserInfoManager.getUserInfoAndSave(null);
        dismiss();
        //}
    }

}
