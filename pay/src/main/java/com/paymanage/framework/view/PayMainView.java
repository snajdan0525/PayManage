package com.paymanage.framework.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boqii.android.framework.ui.viewpager.BqViewPager;
import com.boqii.android.framework.util.NumberUtil;
import com.boqii.petlifehouse.common.activity.BaseActivity;
import com.boqii.petlifehouse.common.model.User;
import com.boqii.petlifehouse.common.ui.dialog.DialogView;
import com.boqii.petlifehouse.pay.R;
import com.boqii.petlifehouse.pay.action.BqPayManage;
import com.boqii.petlifehouse.pay.action.PayResultCallBack;
import com.boqii.petlifehouse.pay.model.BqPayOrder;
import com.boqii.petlifehouse.pay.model.PayEnum;
import com.boqii.petlifehouse.user.LoginManager;
import com.boqii.petlifehouse.user.view.activity.account.SafetyVerificationActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 支付主界面
 * Created by zhangbp on 2016/11/22.
 */
public class PayMainView extends LinearLayout implements View.OnClickListener {
    private PayWayView mPayWayView;
    private TextView needMoney, balance, finalMoney;
    private View payPasswordTips;
    private BqPayOrder mBqPayOrder;
    private PayResultCallBack mPayResultCallBack;
    private CheckBox paySwitch;
    private PayEnum.EscrowPayType payType;

    public PayMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置订单信息
     *
     * @param mBqPayOrder
     */
    public void setBqPayOrder(BqPayOrder mBqPayOrder, PayResultCallBack mPayResultCallBack) {
        if (mBqPayOrder != null) {
            this.mBqPayOrder = mBqPayOrder;
            this.mPayResultCallBack = mPayResultCallBack;
            User mUser = LoginManager.getLoginUser();
            if (TextUtils.isEmpty(mUser.Balance) || TextUtils.equals(mUser.Balance, "0")) {
                findViewById(R.id.balance_box).setVisibility(View.GONE);
                findViewById(R.id.balance_box_line).setVisibility(View.GONE);
                findViewById(R.id.final_money_box).setVisibility(View.GONE);
            }
            setPaySwitchStatus();
            double money = NumberUtil.parseDouble(mBqPayOrder.money);
            String moneyShow = new DecimalFormat("#0.00").format(money);
            finalMoney.setText(moneyShow);
            needMoney.setText(moneyShow);
            double balanceD = NumberUtil.parseDouble(mUser.Balance);
            String balanceShow = new DecimalFormat("#0.00").format(balanceD);
            balance.setText(getContext().getResources().getString(R.string.yuan, balanceShow));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPayWayView = (PayWayView) findViewById(R.id.pay_way_layout);
        needMoney = (TextView) findViewById(R.id.need_money);
        balance = (TextView) findViewById(R.id.balance);
        finalMoney = (TextView) findViewById(R.id.final_money);
        payPasswordTips = findViewById(R.id.pay_password_tips);
        paySwitch = (CheckBox) findViewById(R.id.pay_switch);
        paySwitch.setTag("");
        initAction();
    }

    /**
     * 设置余额支付开关和支付密码的状态
     *
     * @author zhangbp
     */
    private void setPaySwitchStatus() {
        User mUser = LoginManager.getLoginUser();
        if (mUser.getHasPayPassword() == 1) {
            payPasswordTips.setVisibility(View.GONE);
            paySwitch.setVisibility(View.VISIBLE);
        } else {
            payPasswordTips.setVisibility(View.VISIBLE);
            paySwitch.setVisibility(View.GONE);
        }
    }

    /**
     * 设置余额支付
     *
     * @param isBalance
     * @param passWord
     */
    public void setBalancePay(boolean isBalance, String passWord) {
        if (paySwitch != null) {
            paySwitch.setChecked(isBalance);
            paySwitch.setTag(isBalance ? passWord : "");
        }
    }

    private void initAction() {
        mPayWayView.setPayWayCallBack(new PayWayView.PayWayCallBack() {
            @Override
            public void callBack(PayEnum.EscrowPayType payType) {
                PayMainView.this.payType = payType;
            }
        });
        payPasswordTips.setOnClickListener(this);
        paySwitch.setOnClickListener(this);
        findViewById(R.id.ok_btn).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);

        paySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //计算还需支付的金额
                double fMoney = new BigDecimal(mBqPayOrder.money).doubleValue();
                if (isChecked) {
                    String balance_str = LoginManager.getLoginUser().Balance;
                    if (!TextUtils.isEmpty(balance_str)) {
                        fMoney = new BigDecimal(mBqPayOrder.money).subtract(new BigDecimal(balance_str)).doubleValue();
                        fMoney = fMoney < 0 ? 0.00 : fMoney;
                    }
                }
                finalMoney.setText(new DecimalFormat("#0.00").format(fMoney));
                mPayWayView.setSwitchable(fMoney > 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (_id == R.id.pay_password_tips) {
            ((BaseActivity) getContext()).startActivityForResult(SafetyVerificationActivity.getIntent(getContext(), SafetyVerificationActivity.MODIFY_PASSWORD), 10121, new BaseActivity.OnActivityResultListener() {
                @Override
                public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data) {
                    setPaySwitchStatus();
                }
            });
        } else if (_id == R.id.pay_switch) {
            if (!paySwitch.isChecked()) {
                if (getParent() instanceof BqViewPager) {
                    ((BqViewPager) getParent()).setCurrentItem(1);
                }
            } else {
                paySwitch.setChecked(false);
            }
        } else if (_id == R.id.ok_btn) {
            if (payType == null) {
                payType = paySwitch.isChecked() && NumberUtil.parseDouble(finalMoney.getText().toString()) == 0 ? PayEnum.EscrowPayType.BQ_BALANCE : null;
            }
            if (payType != null) {
                ArrayMap<String, String> param = getParam();
                BqPayManage.startPay((Activity) getContext(), mBqPayOrder.getBqServiceType(), payType, param, mPayResultCallBack);
            } else {
                Toast.makeText(getContext(), R.string.please_pay_way_tips, Toast.LENGTH_SHORT).show();
            }
        } else if (_id == R.id.close) {
            if (mPayResultCallBack instanceof DialogView) {
                ((DialogView) mPayResultCallBack).dismiss();
            }
        }
    }

    /**
     * 返回支付参数
     *
     * @return
     */
    private ArrayMap<String, String> getParam() {
        User mUser = LoginManager.getLoginUser();
        ArrayMap<String, String> param = new ArrayMap<String, String>();
        param.put("UserId", mUser.getUserId());
        param.put("OrderId", mBqPayOrder.orderId);
        param.put("IsUseBalance", paySwitch.isChecked() ? "1" : "0");
        param.put("PayPassword", paySwitch.getTag().toString());
        if (mBqPayOrder.extension != null) {
            param.putAll((SimpleArrayMap<? extends String, ? extends String>) mBqPayOrder.extension);
        }
        return param;
    }
}
