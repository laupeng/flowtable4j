package com.ctrip.infosec.flowtable4j.t3afs.jobws;

import com.ctrip.infosec.flowtable4j.t3afs.dal.RuleUpdaterDAO;
import com.ctrip.infosec.flowtable4j.t3afs.bwrule.BWManager;
import com.ctrip.infosec.flowtable4j.t3afs.bwrule.RuleStatement;
import com.ctrip.infosec.flowtable4j.t3afs.bwrule.RuleTerm;
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
@Component
public class BWRuleUpdater {
    @Autowired
    private RuleUpdaterDAO ruleGetter;
    @Autowired
    private BWManager bwManager;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private Logger logger = LoggerFactory.getLogger(BWRuleUpdater.class);
    private Status status = Status.FIRST;

    public void execute() {
        if (status == Status.FIRST) {
            //全量更新bw 规则
            this.getAllBWRule();
            status = Status.NOTFIRST;
        } else {
            this.getUpdateBWRule();
        }
    }

    /**
     * 全量黑白名单，Active=’T‘
     */
    private void getAllBWRule(){
        List<Map<String,Object>> bwList = ruleGetter.getAllBWRule();
        List<RuleStatement> bwAll = new ArrayList<RuleStatement>();
        RuleStatement currentRule=null;
        Integer  prevId=-1;
        RuleTerm term;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Iterator<Map<String,Object>> it = bwList.iterator();
        while (it.hasNext()){
             Map<String,Object> rule = it.next();
             Integer ruleId = Integer.valueOf(Objects.toString(rule.get("RuleID"), "0"));
             term =new RuleTerm(Objects.toString(rule.get("CheckName"), ""),
                     Objects.toString(rule.get("CheckType"), ""),
                     Objects.toString(rule.get("CheckValue"),""),
                     Objects.toString(rule.get("remark"),""));
             if(prevId.equals(ruleId)){
                 currentRule.getRuleTerms().add(term);
             } else{
                 currentRule = new RuleStatement();
                 currentRule.setRuleTerms(new ArrayList<RuleTerm>());
                 try {
                     currentRule.setRuleID(ruleId);
                     currentRule.setEffectDate(sdf.parse(rule.get("SDate").toString()));
                     currentRule.setExpireDate(sdf.parse(rule.get("EDate").toString()));
                     currentRule.setOrderType(Integer.valueOf(Objects.toString(rule.get("OrderType"), "0")));
                     currentRule.setRuleIDName(Objects.toString(rule.get("RuleIDName"),""));
                     currentRule.setRiskLevel(Integer.valueOf(Objects.toString(rule.get("RiskLevel"),"0")));
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
        logger.info("total load active blackWhite rules:"+bwAll.size());
        bwManager.addRule(bwAll);
    }

    /**
     * 增量更新黑白名单， T Add， F remove
     */
    private void getUpdateBWRule(){
        List<Map<String,Object>> bwList = ruleGetter.getUpdateBWRule();
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
            Integer ruleId = Integer.valueOf(Objects.toString(rule.get("RuleID"), "0"));
            term =new RuleTerm(Objects.toString(rule.get("CheckName"), ""),
                    Objects.toString(rule.get("CheckType"), ""),
                    Objects.toString(rule.get("CheckValue"), ""),
                    Objects.toString(rule.get("remark"),""));
            if(prevId.equals(ruleId)){
                currentRule.getRuleTerms().add(term);
            } else{
                currentRule = new RuleStatement();
                currentRule.setRuleTerms(new ArrayList<RuleTerm>());
                try {
                    currentRule.setRuleID(ruleId);
                    currentRule.setEffectDate(sdf.parse(rule.get("SDate").toString()));
                    currentRule.setExpireDate(sdf.parse(rule.get("EDate").toString()));
                    currentRule.setOrderType(Integer.valueOf(Objects.toString(rule.get("OrderType"), "0")));
                    currentRule.setRuleIDName(Objects.toString(rule.get("RuleIDName"),""));
                    currentRule.setRiskLevel(Integer.valueOf(Objects.toString(rule.get("RiskLevel"), "0")));
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
            logger.info("total update blackWhite rules:"+bwAdd.size());
            logger.info("<<<");
            bwManager.addRule(bwAdd);
        }
        if(bwSub.size()>0){
            logger.info(">>> remove rules");
            logger.info("total remove blackWhite rules:"+bwSub.size());
            logger.info("<<<");
            bwManager.removeRule(bwSub);
        }
    }

    static enum Status{
        FIRST,
        NOTFIRST;
    }
}
