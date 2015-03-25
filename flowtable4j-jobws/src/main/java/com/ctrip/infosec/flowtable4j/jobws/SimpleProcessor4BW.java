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
public class SimpleProcessor4BW implements Processer {
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

    private void bwFull(){
        List<Map<String,Object>> bwList = ruleGetter.bwFull();
        Map<Integer,List<Map<String,Object>>> map = new HashMap<Integer, List<Map<String, Object>>>();
        List<RuleStatement> bwfull = new ArrayList<RuleStatement>();
        for(Map item : bwList){
            Integer map_key = Integer.valueOf(item.get("RuleID").toString());
            if(map.containsKey(map_key)){
                map.get(map_key).add(item);
            }else{
                List<Map<String,Object>> map_value = new ArrayList<Map<String, Object>>();
                map_value.add(item);
                map.put(map_key,map_value);
            }
        }
        for(Iterator<Integer> it = map.keySet().iterator();it.hasNext();){
            Integer ruleID = it.next();
            RuleStatement ruleStatement = new RuleStatement();
            bwfull.add(ruleStatement);
            ruleStatement.setRuleID(ruleID);
            List<RuleTerm> list = new ArrayList<RuleTerm>();
            ruleStatement.setRuleTerms(list);
            for(Map<String,Object> item : map.get(ruleID)){
                try {
                    ruleStatement.setEffectDate(sdf.parse(item.get("Sdate").toString()));
                    ruleStatement.setExpireDate(sdf.parse(item.get("Edate").toString()));
                } catch (ParseException e) {
                    logger.error("",e);
                }
                ruleStatement.setOrderType(Integer.valueOf(item.get("OrderType").toString()));
                ruleStatement.setRemark(item.get("Remark").toString());
                ruleStatement.setRiskLevel(Integer.valueOf(item.get("RiskLevel").toString()));

                RuleTerm term = new RuleTerm(item.get("CheckName").toString(),item.get("CheckType").toString(),item.get("CheckValue").toString());
                list.add(term);
            }
        }
        BWManager.addRule(bwfull);
    }

    private void bwIncrement(){
        List<Map<String,Object>> bwList = ruleGetter.bwIncrement();
        Map<Integer,List<Map<String,Object>>> map = new HashMap<Integer, List<Map<String, Object>>>();
        Set<RuleStatement> bwAdd = new HashSet<RuleStatement>();
        Set<RuleStatement> bwSub = new HashSet<RuleStatement>();
        for(Map item : bwList){
            Integer map_key = Integer.valueOf(item.get("RuleID").toString());
            if(map.containsKey(map_key)){
                map.get(map_key).add(item);
            }else{
                List<Map<String,Object>> map_value = new ArrayList<Map<String, Object>>();
                map_value.add(item);
                map.put(map_key,map_value);
            }
        }
        for(Iterator<Integer> it = map.keySet().iterator();it.hasNext();){
            Integer ruleID = it.next();
            RuleStatement ruleStatement = new RuleStatement();
            ruleStatement.setRuleID(ruleID);
            List<RuleTerm> list = new ArrayList<RuleTerm>();
            ruleStatement.setRuleTerms(list);
            for(Map<String,Object> item : map.get(ruleID)){
                try {
                    ruleStatement.setEffectDate(sdf.parse(item.get("Sdate").toString()));
                    ruleStatement.setExpireDate(sdf.parse(item.get("Edate").toString()));
                } catch (ParseException e) {
                    logger.error("",e);
                }
                ruleStatement.setOrderType(Integer.valueOf(item.get("OrderType").toString()));
                ruleStatement.setRemark(item.get("Remark").toString());
                ruleStatement.setRiskLevel(Integer.valueOf(item.get("RiskLevel").toString()));

                RuleTerm term = new RuleTerm(item.get("CheckName").toString(),item.get("CheckType").toString(),item.get("CheckValue").toString());
                list.add(term);

                if("T".equals(item.get("Active").toString())){
                    bwAdd.add(ruleStatement);
                }else{
                    bwSub.add(ruleStatement);
                }
            }
        }
        if(bwAdd.size()>0){
            List<RuleStatement> listAdd = new ArrayList<RuleStatement>();
            listAdd.addAll(bwAdd);
            logger.info(">>> bwadd");
            for(RuleStatement ruleStatement : listAdd){
                logger.info("ruleid"+ruleStatement.getRuleID().toString());
            }
            logger.info("<<<");
            BWManager.addRule(listAdd);
        }
        if(bwSub.size()>0){
            List<RuleStatement> listSub = new ArrayList<RuleStatement>();
            listSub.addAll(bwSub);
            logger.info(">>> bwsub");
            for(RuleStatement ruleStatement : listSub){
                logger.info("ruleid"+ruleStatement.getRuleID().toString());
            }
            logger.info("<<<");
            BWManager.removeRule(listSub);
        }
    }

    static enum Status{
        FIRST,
        NOTFIRST;
    }
}