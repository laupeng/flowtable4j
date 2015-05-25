package com.ctrip.infosec.flowtable4j.payAdapt;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * 流量规则管理器
 * Created by thyang on 2015/3/13 0013. *
 */
public class PayAdaptManager {

    final static Logger logger = LoggerFactory.getLogger(PayAdaptManager.class);

    /**
     * 流量规则
     */
    private final static PayAdaptRule payAdaptRule = new PayAdaptRule();

    /**
     * 检查流量规则
     * 白名单命中任意一条即退出
     *
     * @param fact
     * @param results
     * @return
     */
    public static boolean check(FlowFact fact, List<PayAdaptResultItem> results) {
        try {
            if (payAdaptRule != null) {
                return payAdaptRule.check(fact, results);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 增加流量规则
     * 全量
     *
     * @param flowRules
     * @return
     */
    public static boolean addRule(List<PayAdaptStatement> flowRules) {
        try {
            if (payAdaptRule != null && flowRules != null) {
                payAdaptRule.addRule(flowRules);
            }
            return true;
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
            return false;
        }
    }
}
