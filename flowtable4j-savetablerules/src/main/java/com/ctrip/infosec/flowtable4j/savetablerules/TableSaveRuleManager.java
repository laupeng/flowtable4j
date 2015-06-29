package com.ctrip.infosec.flowtable4j.savetablerules;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量规则管理器
 * Created by thyang on 2015/3/13 0013. *
 */
@Component
public class TableSaveRuleManager {
    final static Logger logger = LoggerFactory.getLogger(TableSaveRuleManager.class);
    private final static Integer WHITE_LVL = 0;
    /**
     * 黑名单管理
     */
    @Autowired
    private BlackRule blackRule;

    /**
     * 检查流量规则
     * 白名单命中任意一条即退出
     *
     * @param fact
     * @return
     */
    public void checkAndSave(FlowFact fact) {
        blackRule.checkAndSave(fact);
    }

    /**
     * 增加流量规则
     * 全量
     *
     * @param flowRuleStatements
     * @return
     */
    public boolean addRule(List<FlowRuleStatement> flowRuleStatements) {
        try {
            List<FlowRuleStatement> white = new ArrayList<FlowRuleStatement>();
            List<FlowRuleStatement> black = new ArrayList<FlowRuleStatement>();
            for (FlowRuleStatement rule : flowRuleStatements) {
                /**
                 * 设置父节点，查找ExtraFieldManager字典表
                 */
                rule.setParentNode();
                black.add(rule);
            }
            if (black.size() > 0) {
                blackRule.addRule(black);
            }
            return true;
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
            return false;
        }
    }
}
