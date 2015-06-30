package com.ctrip.infosec.flowtable4j.flowdata.payAdapt;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 流量规则管理器
 * Created by thyang on 2015/3/13 0013. *
 */
@Component
public class PayAdaptManager {

    final static Logger logger = LoggerFactory.getLogger(PayAdaptManager.class);

    /**
     * 流量规则
     */
    private final PayAdaptRule payAdaptRule = new PayAdaptRule();

    /**
     * 检查流量规则
     * 白名单命中任意一条即退出
     *
     * @param fact
     * @param results
     * @return
     */
    public boolean check(FlowFact fact, List<PayAdaptResultItem> results) {
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
    public boolean addRule(List<PayAdaptStatement> flowRules) {
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
