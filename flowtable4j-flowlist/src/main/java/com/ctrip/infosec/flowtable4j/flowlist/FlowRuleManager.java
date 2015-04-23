package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量规则管理器
 * Created by thyang on 2015/3/13 0013. *
 */
public class FlowRuleManager {
    final static Logger logger = LoggerFactory.getLogger(FlowRuleManager.class);
    private final static Integer WHITE_LVL = 0;

    /**
     * 黑名单管理
     */
    private final static BlackRule blackRule = new BlackRule();

    /**
     * 白名单管理
     */
    private final static WhiteRule whiteRule = new WhiteRule();

    /**
     * 检查白名单
     *
     * @param fact
     * @param results
     * @return
     */
    private static boolean checkWhite(FlowFact fact, RiskResult results) {
        try {
            if (whiteRule != null) {
                return whiteRule.check(fact, results);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return false;
    }

    private static boolean checkBlack(FlowFact fact, RiskResult results) {
        try {
            if (blackRule != null) {
                return blackRule.check(fact, results);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return false;
    }

    /**
     * 检查流量规则
     * 白名单命中任意一条即退出
     * @param fact
     * @param results
     * @return
     */
    public static boolean check(FlowFact fact, RiskResult results) {
        return checkWhite(fact,results) || checkBlack(fact,results);
    }

    /**
     * 增加流量规则
     * 全量
     * @param flowRuleStatements
     * @return
     */
    public static boolean addRule(List<FlowRuleStatement> flowRuleStatements) {
        try {
            List<FlowRuleStatement> white = new ArrayList<FlowRuleStatement>();
            List<FlowRuleStatement> black = new ArrayList<FlowRuleStatement>();
            for (FlowRuleStatement rule : flowRuleStatements) {
                /**
                 * 设置父节点，查找ExtraFieldManager字典表
                 */
                rule.setParentNode();
                if (rule.getRiskLevel() > WHITE_LVL) {
                    black.add(rule);
                } else {
                    white.add(rule);
                }
            }
            if (white.size() > 0) {
                whiteRule.addRule(white);
            }
            if (black.size() > 0) {
                blackRule.addRule(black);
            }
            return true;
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(),ex);
            return false;
        }
    }
}
