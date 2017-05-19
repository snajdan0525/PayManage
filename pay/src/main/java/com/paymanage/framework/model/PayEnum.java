package com.paymanage.framework.model;

/**
 * 支付相关的常量
 * Created by snajdan on 2017/5/19.
 */

public interface PayEnum {
    enum EscrowPayType {
        BQ_BALANCE(0), ALI_PAY(1), WX_PAY(4), CMB_PAY(6), O2O_CMB_PAY(8),ALLIN_PAY(11);
        private int typeId;

        private EscrowPayType(int typeId) {
            this.typeId = typeId;
        }

        public int getTypeId() {
            return typeId;
        }
    }

    enum PayStatus { //支付状态
        PAY_SUCCEED, PAY_FAILURE
    }

    enum ServiceType { //服务(商城，O2O,会员卡)
        SHAP_MAIL, O2O, CLUB_CARD
    }
}
