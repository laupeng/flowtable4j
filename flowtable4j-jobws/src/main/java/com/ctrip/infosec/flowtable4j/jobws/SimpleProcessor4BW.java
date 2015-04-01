package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.bwlist.RuleStatement;
import com.ctrip.infosec.flowtable4j.bwlist.RuleTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangsx on 2015/3/24.
 */
@Component("simpleProcessor4BW")
public class SimpleProcessor4BW implements Processor {
    @Autowired
    private RuleGetter ruleGetter;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private Logger logger = LoggerFactory.getLogger(SimpleProcessor4BW.class);
    private Status status = Status.FIRST;

    @Override
    public void execute() {
        if (status == Status.FIRST) {
            //全量更新bw 规则
            this.bwFull();
            status = Status.NOTFIRST;
        } else {
            this.bwIncrement();
        }
    }

    /**
     * 全量黑白名单，Active=’T‘
     */
    private void bwFull(){
        List<Map<String,Object>> bwList = ruleGetter.bwFull();
        List<RuleStatement> bwAll = new ArrayList<RuleStatement>();
        RuleStatement currentRule=null;
        Integer  prevId=-1;
        RuleTerm term;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Iterator<Map<String,Object>> it = bwList.iterator();
        while (it.hasNext()){
             Map<String,Object> rule = it.next();
             Integer ruleId = Integer.valueOf(rule.get("RuleID").toString());
             term =new RuleTerm(rule.get("CheckName").toString(),rule.get("CheckType").toString(),rule.get("CheckValue").toString());
             if(prevId.equals(ruleId)){
                 currentRule.getRuleTerms().add(term);
             } else{
                 currentRule = new RuleStatement();
                 currentRule.setRuleTerms(new ArrayList<RuleTerm>());
                 try {
                     currentRule.setRuleID(ruleId);
                     currentRule.setEffectDate(sdf.parse(rule.get("SDate").toString()));
                     currentRule.setExpireDate(sdf.parse(rule.get("EDate").toString()));
                     currentRule.setOrderType(Integer.valueOf(rule.get("OrderType").toString()));
                     currentRule.setRemark(rule.get("Remark").toString());
                     currentRule.setRiskLevel(Integer.valueOf(rule.get("RiskLevel").toString()));
                     currentRule.getRuleTerms().add(term);
                     bwAll.add(currentRule);
                     prevId = ruleId;
                 }
                 catch (ParseException ex)
                 {
                     logger.warn(ex.getMessage());
                 }
             }
        }
        BWManager.addRule(bwAll);
    }

    /**
     * 增量更新黑白名单， T Add， F remove
     */
    private void bwIncrement(){
        List<Map<String,Object>> bwList = ruleGetter.bwIncrement();
        Map<Integer,List<Map<String,Object>>> map = new HashMap<Integer, List<Map<String, Object>>>();
        List<RuleStatement> bwAdd = new ArrayList<RuleStatement>();
        List<RuleStatement> bwSub = new ArrayList<RuleStatement>();
        RuleStatement currentRule=null;
        Integer  prevId=-1;
        RuleTerm term;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Iterator<Map<String,Object>> it = bwList.iterator();
        while (it.hasNext()){
            Map<String,Object> rule = it.next();
            Integer ruleId = Integer.valueOf(rule.get("RuleID").toString());
            term =new RuleTerm(rule.get("CheckName").toString(),rule.get("CheckType").toString(),rule.get("CheckValue").toString());
            if(prevId.equals(ruleId)){
                currentRule.getRuleTerms().add(term);
            } else{
                currentRule = new RuleStatement();
                currentRule.setRuleTerms(new ArrayList<RuleTerm>());
                try {
                    currentRule.setRuleID(ruleId);
                    currentRule.setEffectDate(sdf.parse(rule.get("SDate").toString()));
                    currentRule.setExpireDate(sdf.parse(rule.get("EDate").toString()));
                    currentRule.setOrderType(Integer.valueOf(rule.get("OrderType").toString()));
                    currentRule.setRemark(rule.get("Remark").toString());
                    currentRule.setRiskLevel(Integer.valueOf(rule.get("RiskLevel").toString()));
                    currentRule.getRuleTerms().add(term);
                    if("T".equals(Objects.toString(rule.get("Active"),""))){
                        bwAdd.add(currentRule);
                    }else{
                        bwSub.add(currentRule);
                    }
                    prevId = ruleId;
                }
                catch (ParseException ex)
                {
                    logger.warn(ex.getMessage());
                }
            }
        }

        if(bwAdd.size()>0){
            logger.info(">>> add rules");
            for(RuleStatement ruleStatement : bwAdd){
                logger.info("rule id:"+ruleStatement.getRuleID().toString());
            }
            logger.info("<<<");
            BWManager.addRule(bwAdd);
        }
        if(bwSub.size()>0){
            logger.info(">>> remove rules");
            for(RuleStatement ruleStatement : bwSub){
                logger.info("rule id:"+ruleStatement.getRuleID().toString());
            }
            logger.info("<<<");
            BWManager.removeRule(bwSub);
        }
    }

    static enum Status{
        FIRST,
        NOTFIRST;
    }
}
