package com.paymanage.framework.service;

import com.boqii.android.framework.data.DataMiner;
import com.boqii.android.framework.data.MinerFactory;
import com.boqii.android.framework.data.annotation.NodeJS;
import com.boqii.android.framework.data.annotation.PHP;
import com.boqii.android.framework.data.annotation.POST;
import com.boqii.android.framework.data.annotation.Param;
import com.boqii.android.framework.data.annotation.PhpDomain;
import com.boqii.android.framework.data.entity.BaseDataEntity;
import com.boqii.petlifehouse.pay.model.PayCouponInfo;
import com.boqii.petlifehouse.pay.model.PayInfo;

/**
 * 支付接口
 * Created by zhangbp on 16/11/7.
 */

public interface PayMiners extends MinerFactory {

    /**
     * 第三方支付调起需要的信息
     */
    public static class PayInfoEntity extends BaseDataEntity<PayInfo> {
    }

    /**
     * 支付优惠信息
     */
    public static class PayCouponInfoEntity extends BaseDataEntity<PayCouponInfo> {
    }


    /**
     * 读取会员卡订单支付信息
     *
     * @param uid
     * @param is_balanced
     * @param payPassword
     * @param topup
     * @param cid
     * @param cuid
     * @param type
     * @param observer
     * @return
     */
    @PHP(PhpDomain.O2O)
    @POST(uri = "cardPay", dataType = PayInfoEntity.class)
    public DataMiner getCardPayInfo(@Param("uid") String uid,
                                    @Param("is_balanced") String is_balanced,
                                    @Param("payPassword") String payPassword,
                                    @Param("topup") String topup,
                                    @Param("cid") String cid,
                                    @Param("cuid") String cuid,
                                    @Param("type") String type,
                                    DataMiner.DataMinerObserver observer);

    /**
     * 读取服务订单支付信息
     *
     * @param UserId
     * @param orderId
     * @param isBalance
     * @param payPassword
     * @param type
     * @param observer
     * @return
     */
    @NodeJS
    @POST(uri = "/orders/pay", dataType = PayInfoEntity.class)
    public DataMiner getO2oPayInfo(@Param("UserId") String UserId,
                                   @Param("orderId") String orderId,
                                   @Param("isBalance") String isBalance,
                                   @Param("payPassword") String payPassword,
                                   @Param("type") String type,
                                   DataMiner.DataMinerObserver observer);

    /**
     * 读取商城订单支付信息
     *
     * @param UserId
     * @param OrderId
     * @param IsUseBalance
     * @param PayPassword
     * @param PayType
     * @param observer
     * @return
     */
    @PHP(PhpDomain.ShoppingMall)
    @POST(uri = "PayGoodsOrder", dataType = PayInfoEntity.class)
    public DataMiner getShopMallPayInfo(@Param("UserId") String UserId,
                                        @Param("OrderId") String OrderId,
                                        @Param("IsUseBalance") String IsUseBalance,
                                        @Param("PayPassword") String PayPassword,
                                        @Param("PayType") String PayType,
                                        DataMiner.DataMinerObserver observer);

    /**
     * 取支付优惠信息
     *
     * @param observer
     * @return
     */
    @PHP(PhpDomain.ShoppingMall)
    @POST(uri = "GetPreferentialInfo", dataType = PayCouponInfoEntity.class)
    public DataMiner getCouponInfo(DataMiner.DataMinerObserver observer);

    /**
     * 支付密码验证
     *
     * @param observer
     * @return
     */
    @PHP
    @POST(uri = "CheckPayPassword", dataType = BaseDataEntity.class)
    public DataMiner CheckPayPassword(@Param("Password") String Password, DataMiner.DataMinerObserver observer);


}
