package com.ctrip.infosec.flowtable4j.flowdata.bwrule;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑白名单调配器
 * Created by thyang on 2015/3/13 0013. *
 */
@Component
public class BWManager {
    final static Logger logger = LoggerFactory.getLogger(BWManager.class);
    private final static Integer WHITE_LVL = 0;
    /**
     * 黑名单管理
     */
    private final BlackRule blackRule = new BlackRule();

    /**
     * 白名单管理
     */
    private final WhiteRule whiteRule = new WhiteRule();

    /**
     * 检查白名单
     * @param fact
     * @param results
     * @return
     */
    public  boolean checkWhite(BWFact fact, RiskResult results) {
        try{
            if (whiteRule != null) {
                return whiteRule.check(fact, results);
            }
        }
        catch (Throwable ex){
            logger.warn(ex.getMessage(),ex);
        }
        return false;
    }

    public  boolean checkBlack(BWFact fact, RiskResult results) {
        try{
            if (blackRule != null) {
                return blackRule.check(fact, results);
            }
        }
        catch (Throwable ex){
            logger.warn(ex.getMessage(),ex);
        }
        return false;
    }

    public  boolean addRule(List<RuleStatement> ruleStatements) {
        try {
            List<RuleStatement> white = new ArrayList<RuleStatement>();
            List<RuleStatement> black = new ArrayList<RuleStatement>();
            for (RuleStatement rule : ruleStatements) {
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
        }
        catch (Throwable ex){
            logger.warn(ex.getMessage(),ex);
            return false;
        }
    }

    public  boolean removeRule(List<RuleStatement> ruleStatements) {
        try {
            List<RuleStatement> white = new ArrayList<RuleStatement>();
            List<RuleStatement> black = new ArrayList<RuleStatement>();
            for (RuleStatement rule : ruleStatements) {
                if (rule.getRiskLevel() > WHITE_LVL) {
                    black.add(rule);
                } else {
                    white.add(rule);
                }
            }
            if (white.size() > 0) {
                whiteRule.removeRule(white);
            }
            if (black.size() > 0) {
                blackRule.removeRule(black);
            }
            return true;
        }
        catch (Throwable ex){
            logger.warn(ex.getMessage(),ex);
            return false;
        }
    }
}
