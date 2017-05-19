package com.paymanage.framework.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.boqii.android.framework.data.BqData;
import com.boqii.android.framework.data.DataMiner;
import com.boqii.android.framework.ui.viewpager.BqViewPager;
import com.boqii.android.framework.util.TaskUtil;
import com.boqii.petlifehouse.pay.R;
import com.boqii.petlifehouse.pay.service.PayMiners;
import com.boqii.petlifehouse.user.view.activity.account.SafetyVerificationActivity;

import okhttp3.internal.Util;

/**
 * 输入支付密码
 * Created by zhangbp on 2016/11/11.
 */
public class InputPayPassWordView extends LinearLayout implements View.OnClickListener {
    private InputPassWordCallBack mInputPassWordCallBack;
    private View backBtn, okBtn, forgetPassword;
    private EditText passwordEdit;

    public InputPayPassWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        backBtn = findViewById(R.id.back_btn);
        okBtn = findViewById(R.id.ok_btn);
        passwordEdit = (EditText) findViewById(R.id.edit_password);
        forgetPassword = findViewById(R.id.forget_password);
        backBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (_id == R.id.back_btn) {
            callBack(null);
        } else if (_id == R.id.ok_btn) {
            String password = passwordEdit.getText().toString();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), R.string.please_input_password, Toast.LENGTH_SHORT).show();
            } else {
                checkPassword(password);
            }
        } else if (_id == R.id.forget_password) {
            getContext().startActivity(SafetyVerificationActivity.getIntent(getContext(), SafetyVerificationActivity.MODIFY_PASSWORD));
        }
    }

    /**
     * 返回支付界面
     *
     * @param passWord
     */
    private void callBack(String passWord) {
        if (mInputPassWordCallBack != null) {
            mInputPassWordCallBack.callBack(passWord);
        }
        if (getParent() instanceof BqViewPager) {
            ((BqViewPager) getParent()).setCurrentItem(0);
        }
    }

    /**
     * 验证密码
     */
    private void checkPassword(String password) {
        final String finalPassword = password;
        password = Util.md5Hex(password);
        BqData.getMinerService(PayMiners.class).CheckPayPassword(password, new DataMiner.DataMinerObserver() {
            @Override
            public boolean onDataError(DataMiner miner, DataMiner.DataMinerError error) {
                return false;
            }

            @Override
            public void onDataSuccess(final DataMiner miner) {
                TaskUtil.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack(finalPassword);
                    }
                });
            }
        }).showLoading(getContext(), "").work();
        System.out.println("getContext()getContext()getContext()"+getContext());
    }


    /**
     * 设置InputPassWordCallBack
     *
     * @param mInputPassWordCallBack
     */
    public void setInputPassWordCallBack(InputPassWordCallBack mInputPassWordCallBack) {
        this.mInputPassWordCallBack = mInputPassWordCallBack;
    }

    /**
     * 输入密码回调
     */
    public interface InputPassWordCallBack {
        public void callBack(String passWord);
    }
}
