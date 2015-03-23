package com.ctrip.infosec.flowtable4j.bwlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑白名单调配器
 * Created by thyang on 2015/3/13 0013. *
 */
public class BWManager {
    final static Logger logger = LoggerFactory.getLogger(BWManager.class);
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
     * @param fact
     * @param results
     * @return
     */
    public static boolean checkWhite(BWFact fact, List<BWResult> results) {
        try{
            if (whiteRule != null) {
                return whiteRule.check(fact, results);
            }
        }
        catch (Throwable ex){
            logger.warn(ex.getMessage());
        }
        return false;
    }

    public static boolean checkBlack(BWFact fact, List<BWResult> results) {
        try{
            if (blackRule != null) {
                return blackRule.check(fact, results);
            }
        }
        catch (Throwable ex){
            logger.warn(ex.getMessage());
        }
        return false;
    }

    public static boolean addRule(List<RuleStatement> ruleStatements) {
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
            logger.warn(ex.getMessage());
            return false;
        }
    }

    public static boolean removeRule(List<RuleStatement> ruleStatements) {
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
            logger.warn(ex.getMessage());
            return false;
        }
    }
}
