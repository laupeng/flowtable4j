package com.ctrip.infosec.flowtable4j.model.check;

/**
 * Created by zhangsx on 2015/3/24.
 */
public enum CheckType {
    /**
     * 黑白名单
     */
    BW,
    /**
     * 支付
     */
    FLOWRULE,
    /**
     * 账户黑白名单
     */
    ACCOUNT,
    /**
     * 支付适配
     */
    PAYADAPTER,
    /**
     *
     */
    SCORE;
}
