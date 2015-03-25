package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.flowrule.RuleManager;
import com.ctrip.infosec.flowtable4j.flowrule.entity.FlowRuleEntity;
import com.ctrip.infosec.flowtable4j.flowrule.entity.RuleMatchFieldEntity;
import com.ctrip.infosec.flowtable4j.flowrule.entity.RuleStatisticEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/3/24.
 */
@Component("simpleProcessor4Flow")
public class SimpleProcessor4Flow implements Processor {
    @Autowired
    private RuleGetter ruleGetter;
    @Override
    public void execute() {
        ruleFull();
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

