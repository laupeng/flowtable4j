package com.ctrip.flowtable4j.core.ruleUpdateJob;

import com.ctrip.flowtable4j.core.blackList.BWManager;
import com.ctrip.flowtable4j.core.blackList.RuleStatement;
import com.ctrip.flowtable4j.core.blackList.RuleTerm;
import com.ctrip.flowtable4j.core.flowRule.RuleManager;
import com.ctrip.flowtable4j.core.flowRule.entity.FlowRuleEntity;
import com.ctrip.flowtable4j.core.flowRule.entity.RuleMatchFieldEntity;
import com.ctrip.flowtable4j.core.flowRule.entity.RuleStatisticEntity;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangsx on 2015/3/13.
 */
public class SimpleProcesser implements Processer {
    @Autowired
    private RuleGetter ruleGetter;
    private Logger logger = LoggerFactory.getLogger(SimpleProcesser.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Status status = Status.FIRST;

    @Override
    public void execute() {
        if (status == Status.FIRST) {
            //全量更新bw 规则
            this.bwFull();
            status = Status.NOTFIRST;
        } else {
            this.bwIncrement();
        }
        //TODO 更新rule
        ruleFull();
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
                    ruleStatement.setEffectDate(sdf.parse(item.get("Edate").toString()));
                    ruleStatement.setExpireDate(sdf.parse(item.get("Sdate").toString()));
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
                    ruleStatement.setEffectDate(sdf.parse(item.get("Edate").toString()));
                    ruleStatement.setExpireDate(sdf.parse(item.get("Sdate").toString()));
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
            BWManager.addRule(listAdd);
        }
        if(bwSub.size()>0){
            List<RuleStatement> listSub = new ArrayList<RuleStatement>();
            listSub.addAll(bwSub);
            BWManager.removeRule(listSub);
        }

    }

    /**
     * 比较listMatch 和 listStatistic的top1 的entity的FlowRuleID
     * 若listMatch的较小（<=）则将listMatch 的top1 add 进入 结果集（如果有相同FlowRuleID不覆盖）
     * 反之类似
     */
    private void ruleFull(){
        List<Map<String,Object>> listMatch = ruleGetter.ruleMatch();
        List<Map<String,Object>> listStatistic = ruleGetter.ruleStatistic();

        List<FlowRuleEntity> ruleList = new ArrayList<FlowRuleEntity>();

        int count = listMatch.size()+listStatistic.size();
        int p_match = 0;
        int p_statistic=0;
        boolean matchIsOver = false;
        boolean statisticIsOver = false;
        //f.FlowRuleID,f.RuleName,f.RiskLevel,f.OrderType,f.PrepayType,f.RuleDesc,d.ColumnName,r.MatchType,r.MatchValue,d.TableName

        for(int i=0;i<count;i++){
            if(p_match>=listMatch.size()){
                p_match = listMatch.size()-1;
                matchIsOver = true;
            }
            if(p_statistic>=listStatistic.size()){
                p_statistic = listStatistic.size()-1;
                statisticIsOver = true;
            }
            int matchID = Integer.valueOf(listMatch.get(p_match).get("FlowRuleID").toString());
            int staticID = Integer.valueOf(listStatistic.get(p_statistic).get("FlowRuleID").toString());
//            FlowRuleEntity flowRuleEntity = ruleList.get(ruleList.size() - 1);
            if(statisticIsOver||matchID<=staticID&&p_match<listMatch.size()&&!matchIsOver){

                if(ruleList.size()==0||matchID!=ruleList.get(ruleList.size() - 1).getFlowRuleID()){
                    FlowRuleEntity flowRuleEntity12Add = new FlowRuleEntity();
                    ruleList.add(flowRuleEntity12Add);
                    flowRuleEntity12Add.setFlowRuleID(Integer.valueOf( listMatch.get(p_match).get("FlowRuleID").toString()));
                    flowRuleEntity12Add.setRuleName(listMatch.get(p_match).get("RuleName").toString());
                    flowRuleEntity12Add.setRiskLevel(Integer.valueOf( listMatch.get(p_match).get("RiskLevel").toString()));
                    flowRuleEntity12Add.setOrderType(Integer.valueOf(listMatch.get(p_match).get("OrderType").toString()));
                    flowRuleEntity12Add.setPrepayType(listMatch.get(p_match).get("PrepayType").toString());
                    flowRuleEntity12Add.setRuleDesc(listMatch.get(p_match).get("RuleDesc").toString());

                    RuleMatchFieldEntity ruleMatchFieldEntity = new RuleMatchFieldEntity();
                    List<RuleMatchFieldEntity> list = flowRuleEntity12Add.getMatchFieldListItem();
                    if(list==null){
                        list=new ArrayList<RuleMatchFieldEntity>();
                    }
                    list.add(ruleMatchFieldEntity);
                    flowRuleEntity12Add.setMatchFieldListItem(list);
                    ruleMatchFieldEntity.setColumnName(listMatch.get(p_match).get("ColumnName").toString());
                    ruleMatchFieldEntity.setMatchType(listMatch.get(p_match).get("MatchType").toString());
                    ruleMatchFieldEntity.setMatchValue(listMatch.get(p_match).get("MatchValue").toString());
                    ruleMatchFieldEntity.setTableName(listMatch.get(p_match).get("TableName").toString());

                }else{
                    RuleMatchFieldEntity ruleMatchFieldEntity = new RuleMatchFieldEntity();
                    ruleMatchFieldEntity.setColumnName(listMatch.get(p_match).get("ColumnName").toString());
                    ruleMatchFieldEntity.setMatchType(listMatch.get(p_match).get("MatchType").toString());
                    ruleMatchFieldEntity.setMatchValue(listMatch.get(p_match).get("MatchValue").toString());
                    ruleMatchFieldEntity.setTableName(listMatch.get(p_match).get("TableName").toString());
//                    List<RuleMatchFieldEntity> list = ruleList.get(ruleList.size() - 1).getMatchFieldListItem();
//                    if(list==null){
//                        list = new ArrayList<RuleMatchFieldEntity>();
//                    }
//                    list.add(ruleMatchFieldEntity);

                    if(ruleList.get(ruleList.size() - 1).getMatchFieldListItem()==null){
                        ruleList.get(ruleList.size() - 1).setMatchFieldListItem(new ArrayList<RuleMatchFieldEntity>());
                    }
                    ruleList.get(ruleList.size() - 1).getMatchFieldListItem().add(ruleMatchFieldEntity);
                }
                p_match++;
            }
            if(matchIsOver||matchID>staticID&&p_statistic<listStatistic.size()&&!statisticIsOver) {

                if(ruleList.size()==0||staticID!=ruleList.get(ruleList.size() - 1).getFlowRuleID()){

                    FlowRuleEntity flowRuleEntity12Add = new FlowRuleEntity();
                    ruleList.add(flowRuleEntity12Add);
                    flowRuleEntity12Add.setFlowRuleID(Integer.valueOf( listMatch.get(p_match).get("FlowRuleID").toString()));
                    flowRuleEntity12Add.setRuleName(listMatch.get(p_match).get("RuleName").toString());
                    flowRuleEntity12Add.setRiskLevel(Integer.valueOf( listMatch.get(p_match).get("RiskLevel").toString()));
                    flowRuleEntity12Add.setOrderType(Integer.valueOf(listMatch.get(p_match).get("OrderType").toString()));
                    flowRuleEntity12Add.setPrepayType(listMatch.get(p_match).get("PrepayType").toString());
                    flowRuleEntity12Add.setRuleDesc(listMatch.get(p_match).get("RuleDesc").toString());

                    RuleStatisticEntity ruleStatisticEntity = new RuleStatisticEntity();

                    List<RuleStatisticEntity> list = flowRuleEntity12Add.getStatisticListItem();
                    if(list==null){
                        list = new ArrayList<RuleStatisticEntity>();
                    }
                    list.add(ruleStatisticEntity);
                    flowRuleEntity12Add.setStatisticListItem(list);
                    ruleStatisticEntity.setMatchValue(listStatistic.get(p_statistic).get("MatchValue").toString());
                    ruleStatisticEntity.setMatchType(listStatistic.get(p_statistic).get("MatchType").toString());
                    ruleStatisticEntity.setKeyColumnName(listStatistic.get(p_statistic).get("KeyColumnName").toString());
                    ruleStatisticEntity.setKeyTableName(listStatistic.get(p_statistic).get("KeyTableName").toString());
                    ruleStatisticEntity.setMatchColumnName(listStatistic.get(p_statistic).get("MatchColumnName").toString());
//                    ruleStatisticEntity.setMatchTableName(listStatistic.get(p_statistic).get("MatchTableName").toString());
                    ruleStatisticEntity.setSqlValue(listStatistic.get(p_statistic).get("SqlValue").toString());
                    ruleStatisticEntity.setStartTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("StartTimeLimit").toString()));
                    ruleStatisticEntity.setStatisticType(listStatistic.get(p_statistic).get("StatisticType").toString());
                    ruleStatisticEntity.setTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("TimeLimit").toString()));
                }else{

                    RuleStatisticEntity ruleStatisticEntity = new RuleStatisticEntity();
                    ruleStatisticEntity.setMatchValue(listStatistic.get(p_statistic).get("MatchValue").toString());
                    ruleStatisticEntity.setMatchType(listStatistic.get(p_statistic).get("MatchType").toString());
                    ruleStatisticEntity.setKeyColumnName(listStatistic.get(p_statistic).get("KeyColumnName").toString());
                    ruleStatisticEntity.setKeyTableName(listStatistic.get(p_statistic).get("KeyTableName").toString());
                    ruleStatisticEntity.setMatchColumnName(listStatistic.get(p_statistic).get("MatchColumnName").toString());
//                    ruleStatisticEntity.setMatchTableName(listStatistic.get(p_statistic).get("MatchTableName").toString());
                    ruleStatisticEntity.setSqlValue(listStatistic.get(p_statistic).get("SqlValue").toString());
                    ruleStatisticEntity.setStartTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("StartTimeLimit").toString()));
                    ruleStatisticEntity.setStatisticType(listStatistic.get(p_statistic).get("StatisticType").toString());
                    ruleStatisticEntity.setTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("TimeLimit").toString()));
//                    List<RuleStatisticEntity> list = ruleList.get(ruleList.size() - 1).getStatisticListItem();
//                    if(list==null){
//                        list = new ArrayList<RuleStatisticEntity>();
////                        ruleList.get(ruleList.size() - 1).setStatisticListItem(new ArrayList<RuleStatisticEntity>());
//                    }
//                    list.add(ruleStatisticEntity);
//
                    if(ruleList.get(ruleList.size() - 1).getStatisticListItem()==null){
                        ruleList.get(ruleList.size() - 1).setStatisticListItem(new ArrayList<RuleStatisticEntity>());
                    }
                    ruleList.get(ruleList.size() - 1).getStatisticListItem().add(ruleStatisticEntity);
                }
                p_statistic++;
            }

        }

        RuleManager.SetRuleEntities(ruleList);
    }
}
