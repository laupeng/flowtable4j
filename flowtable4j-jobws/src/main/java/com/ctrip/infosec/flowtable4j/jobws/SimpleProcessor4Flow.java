package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.flowlist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by zhangsx on 2015/3/24.
 */
@Component("simpleProcessor4Flow")
public class SimpleProcessor4Flow implements Processor {
    @Autowired
    private RuleGetter ruleGetter;

    @Override
    public void execute() {
        getRuleFull();
    }

    /**
     * 比较listMatch 和 listStatistic的top1 的entity的FlowRuleID
     * 若listMatch的较小（<=）则将listMatch 的top1 add 进入 结果集（如果有相同FlowRuleID不覆盖）
     * 反之类似
     */
//    private void ruleFull(){
//        List<Map<String,Object>> listMatch = ruleGetter.ruleMatch();
//        List<Map<String,Object>> listStatistic = ruleGetter.ruleStatistic();
//
//        List<FlowRuleEntity> ruleList = new ArrayList<FlowRuleEntity>();
//
//        int count = listMatch.size()+listStatistic.size();
//        int p_match = 0;
//        int p_statistic=0;
//        boolean matchIsOver = false;
//        boolean statisticIsOver = false;
//        //f.FlowRuleID,f.RuleName,f.RiskLevel,f.OrderType,f.PrepayType,f.RuleDesc,d.ColumnName,r.MatchType,r.MatchValue,d.TableName
//
//        for(int i=0;i<count;i++){
//            if(p_match>=listMatch.size()){
//                p_match = listMatch.size()-1;
//                matchIsOver = true;
//            }
//            if(p_statistic>=listStatistic.size()){
//                p_statistic = listStatistic.size()-1;
//                statisticIsOver = true;
//            }
//            int matchID = Integer.valueOf(listMatch.get(p_match).get("FlowRuleID").toString());
//            int staticID = Integer.valueOf(listStatistic.get(p_statistic).get("FlowRuleID").toString());
//            if(statisticIsOver||matchID<=staticID&&p_match<listMatch.size()&&!matchIsOver){
//                if(ruleList.size()==0||matchID!=ruleList.get(ruleList.size() - 1).getFlowRuleID()){
//                    FlowRuleEntity flowRuleEntity12Add = new FlowRuleEntity();
//                    ruleList.add(flowRuleEntity12Add);
//                    flowRuleEntity12Add.setFlowRuleID(Integer.valueOf( listMatch.get(p_match).get("FlowRuleID").toString()));
//                    flowRuleEntity12Add.setRuleName(listMatch.get(p_match).get("RuleName").toString());
//                    flowRuleEntity12Add.setRiskLevel(Integer.valueOf( listMatch.get(p_match).get("RiskLevel").toString()));
//                    flowRuleEntity12Add.setOrderType(Integer.valueOf(listMatch.get(p_match).get("OrderType").toString()));
//                    flowRuleEntity12Add.setPrepayType(listMatch.get(p_match).get("PrepayType").toString());
//                    flowRuleEntity12Add.setRuleDesc(listMatch.get(p_match).get("RuleDesc").toString());
//
//                    RuleMatchFieldEntity ruleMatchFieldEntity = new RuleMatchFieldEntity();
//                    List<RuleMatchFieldEntity> list = flowRuleEntity12Add.getMatchFieldListItem();
//                    if(list==null){
//                        list=new ArrayList<RuleMatchFieldEntity>();
//                    }
//                    list.add(ruleMatchFieldEntity);
//                    flowRuleEntity12Add.setMatchFieldListItem(list);
//                    ruleMatchFieldEntity.setColumnName(listMatch.get(p_match).get("ColumnName").toString());
//                    ruleMatchFieldEntity.setMatchType(listMatch.get(p_match).get("MatchType").toString());
//                    ruleMatchFieldEntity.setMatchValue(listMatch.get(p_match).get("MatchValue").toString());
//                    ruleMatchFieldEntity.setTableName(listMatch.get(p_match).get("TableName").toString());
//
//                }else{
//                    RuleMatchFieldEntity ruleMatchFieldEntity = new RuleMatchFieldEntity();
//                    ruleMatchFieldEntity.setColumnName(listMatch.get(p_match).get("ColumnName").toString());
//                    ruleMatchFieldEntity.setMatchType(listMatch.get(p_match).get("MatchType").toString());
//                    ruleMatchFieldEntity.setMatchValue(listMatch.get(p_match).get("MatchValue").toString());
//                    ruleMatchFieldEntity.setTableName(listMatch.get(p_match).get("TableName").toString());
////                    List<RuleMatchFieldEntity> list = ruleList.get(ruleList.size() - 1).getMatchFieldListItem();
////                    if(list==null){
////                        list = new ArrayList<RuleMatchFieldEntity>();
////                    }
////                    list.add(ruleMatchFieldEntity);
//
//                    if(ruleList.get(ruleList.size() - 1).getMatchFieldListItem()==null){
//                        ruleList.get(ruleList.size() - 1).setMatchFieldListItem(new ArrayList<RuleMatchFieldEntity>());
//                    }
//                    ruleList.get(ruleList.size() - 1).getMatchFieldListItem().add(ruleMatchFieldEntity);
//                }
//                p_match++;
//            }
//            if(matchIsOver||matchID>staticID&&p_statistic<listStatistic.size()&&!statisticIsOver) {
//
//                if(ruleList.size()==0||staticID!=ruleList.get(ruleList.size() - 1).getFlowRuleID()){
//
//                    FlowRuleEntity flowRuleEntity12Add = new FlowRuleEntity();
//                    ruleList.add(flowRuleEntity12Add);
//                    flowRuleEntity12Add.setFlowRuleID(Integer.valueOf( listMatch.get(p_match).get("FlowRuleID").toString()));
//                    flowRuleEntity12Add.setRuleName(listMatch.get(p_match).get("RuleName").toString());
//                    flowRuleEntity12Add.setRiskLevel(Integer.valueOf( listMatch.get(p_match).get("RiskLevel").toString()));
//                    flowRuleEntity12Add.setOrderType(Integer.valueOf(listMatch.get(p_match).get("OrderType").toString()));
//                    flowRuleEntity12Add.setPrepayType(listMatch.get(p_match).get("PrepayType").toString());
//                    flowRuleEntity12Add.setRuleDesc(listMatch.get(p_match).get("RuleDesc").toString());
//
//                    RuleStatisticEntity ruleStatisticEntity = new RuleStatisticEntity();
//
//                    List<RuleStatisticEntity> list = flowRuleEntity12Add.getStatisticListItem();
//                    if(list==null){
//                        list = new ArrayList<RuleStatisticEntity>();
//                    }
//                    list.add(ruleStatisticEntity);
//                    flowRuleEntity12Add.setStatisticListItem(list);
//                    ruleStatisticEntity.setMatchValue(listStatistic.get(p_statistic).get("MatchValue").toString());
//                    ruleStatisticEntity.setMatchType(listStatistic.get(p_statistic).get("MatchType").toString());
//                    ruleStatisticEntity.setKeyColumnName(listStatistic.get(p_statistic).get("KeyColumnName").toString());
//                    ruleStatisticEntity.setKeyTableName(listStatistic.get(p_statistic).get("KeyTableName").toString());
//                    ruleStatisticEntity.setMatchColumnName(listStatistic.get(p_statistic).get("MatchColumnName").toString());
////                    ruleStatisticEntity.setMatchTableName(listStatistic.get(p_statistic).get("MatchTableName").toString());
//                    ruleStatisticEntity.setSqlValue(listStatistic.get(p_statistic).get("SqlValue").toString());
//                    ruleStatisticEntity.setStartTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("StartTimeLimit").toString()));
//                    ruleStatisticEntity.setStatisticType(listStatistic.get(p_statistic).get("StatisticType").toString());
//                    ruleStatisticEntity.setTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("TimeLimit").toString()));
//                }else{
//
//                    RuleStatisticEntity ruleStatisticEntity = new RuleStatisticEntity();
//                    ruleStatisticEntity.setMatchValue(listStatistic.get(p_statistic).get("MatchValue").toString());
//                    ruleStatisticEntity.setMatchType(listStatistic.get(p_statistic).get("MatchType").toString());
//                    ruleStatisticEntity.setKeyColumnName(listStatistic.get(p_statistic).get("KeyColumnName").toString());
//                    ruleStatisticEntity.setKeyTableName(listStatistic.get(p_statistic).get("KeyTableName").toString());
//                    ruleStatisticEntity.setMatchColumnName(listStatistic.get(p_statistic).get("MatchColumnName").toString());
////                    ruleStatisticEntity.setMatchTableName(listStatistic.get(p_statistic).get("MatchTableName").toString());
//                    ruleStatisticEntity.setSqlValue(listStatistic.get(p_statistic).get("SqlValue").toString());
//                    ruleStatisticEntity.setStartTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("StartTimeLimit").toString()));
//                    ruleStatisticEntity.setStatisticType(listStatistic.get(p_statistic).get("StatisticType").toString());
//                    ruleStatisticEntity.setTimeLimit(Integer.valueOf(listStatistic.get(p_statistic).get("TimeLimit").toString()));
////                    List<RuleStatisticEntity> list = ruleList.get(ruleList.size() - 1).getStatisticListItem();
////                    if(list==null){
////                        list = new ArrayList<RuleStatisticEntity>();
//////                        ruleList.get(ruleList.size() - 1).setStatisticListItem(new ArrayList<RuleStatisticEntity>());
////                    }
////                    list.add(ruleStatisticEntity);
////
//                    if(ruleList.get(ruleList.size() - 1).getStatisticListItem()==null){
//                        ruleList.get(ruleList.size() - 1).setStatisticListItem(new ArrayList<RuleStatisticEntity>());
//                    }
//                    ruleList.get(ruleList.size() - 1).getStatisticListItem().add(ruleStatisticEntity);
//                }
//                p_statistic++;
//            }
//
//        }
//
////        RuleManagerImpl.SetRuleEntities(ruleList);
//    }
    private void getRuleFull() {
        List<Map<String, Object>> flowRuleMasters = ruleGetter.getFlowRuleMaster();
        List<Map<String, Object>> ruleValues = ruleGetter.getRuleValue();
        List<Map<String, Object>> ruleFields = ruleGetter.getRuleField();
        List<Map<String, Object>> counterSqls = ruleGetter.getCountSql();
        List<FlowRuleStatement> results = new ArrayList<FlowRuleStatement>();

        int p_values = -1, p_fields = -1, p_counter = -1;
        for (Map<String, Object> flowRuleMaster : flowRuleMasters) {
            //FlowRuleID,RuleName,RiskLevel,OrderType,PrepayType,RuleDesc
            FlowRuleStatement flowRuleStatement = new FlowRuleStatement();
            results.add(flowRuleStatement);
            flowRuleStatement.setFlowRuleTerms(new ArrayList<FlowRuleTerm>());
            flowRuleStatement.setRuleName(Objects.toString(flowRuleMaster.get("RuleName"), ""));
            flowRuleStatement.setOrderType(Integer.valueOf(Objects.toString(flowRuleMaster.get("OrderType"), "0")));
            flowRuleStatement.setPrepayType(Objects.toString(flowRuleMaster.get("PrepayType"), ""));
            flowRuleStatement.setRemark(Objects.toString(flowRuleMaster.get("RuleDesc"), ""));
            flowRuleStatement.setRiskLevel(Integer.valueOf(Objects.toString(flowRuleMaster.get("RiskLevel"), "0")));
            flowRuleStatement.setRuleID(Integer.valueOf(Objects.toString(flowRuleMaster.get("FlowRuleID"), "0")));

            for (int i=p_values;i<ruleValues.size();i++) {
                p_values++;
                Map<String, Object> value = ruleValues.get(p_values);
                int id = Integer.valueOf(Objects.toString(value.get("FlowRuleID"), "-1"));
                if (flowRuleStatement.getRuleID().intValue() == id) {
                    String fieldName = Objects.toString(value.get("ColumnName"), "");
                    String op = Objects.toString(value.get("MatchType"), "");
                    String matchValue = Objects.toString(value.get("MatchValue"), "");
                    FlowRuleTerm valueTerm = new ValueMatchRuleTerm(fieldName, op, matchValue);
                    flowRuleStatement.getFlowRuleTerms().add(valueTerm);
                } else if(flowRuleStatement.getRuleID().intValue() < id){
                    p_values--;
                    break;
                }
            }

            for (int i=p_fields;i<ruleFields.size();i++) {
                p_fields++;
                Map<String, Object> field = ruleFields.get(p_fields);
                int id = Integer.valueOf(Objects.toString(field.get("FlowRuleID"), "-1"));
                if (flowRuleStatement.getRuleID().intValue() == id) {
                    String fieldName = Objects.toString(field.get("ColumnName"), "");
                    String op = Objects.toString(field.get("MatchType"), "");
                    String matchValue = Objects.toString(field.get("MatchValue"), "");
                    FlowRuleTerm fieldTerm = new FieldMatchRuleTerm(fieldName, op, matchValue);
                    flowRuleStatement.getFlowRuleTerms().add(fieldTerm);
                } else if(flowRuleStatement.getRuleID().intValue() < id) {
                    p_fields--;
                    break;
                }
            }

            for (int i=p_counter;i<counterSqls.size();i++) {
                p_counter++;
                Map<String, Object> counter = counterSqls.get(p_counter);
                int id = Integer.valueOf(Objects.toString(counter.get("FlowRuleID"), "-1"));
                if (flowRuleStatement.getRuleID().intValue() == id) {
                    String fieldName = Objects.toString(counter.get("KeyColumnName"), "");
                    String op = Objects.toString(counter.get("MatchType"), "");
                    String matchValue = Objects.toString(counter.get("MatchValue"), "");
                    String countType = Objects.toString(counter.get("StatisticType"), "");
                    String countField = Objects.toString(counter.get("MatchColumnName"), "");
                    String sql = Objects.toString(counter.get("SqlValue"), "");
                    Integer startOffset = Integer.valueOf(Objects.toString(flowRuleMaster.get("StartTimeLimit"), "0"));
                    Integer endOffset = Integer.valueOf(Objects.toString(flowRuleMaster.get("TimeLimit"), "0"));
                    CounterMatchRuleTerm counterTerm = new CounterMatchRuleTerm(fieldName, op, matchValue);

                    counterTerm.setCountType(countType,countField,sql);
                    counterTerm.setTimeOffset(-startOffset,-endOffset);
                    flowRuleStatement.getFlowRuleTerms().add(counterTerm);
                } else if(flowRuleStatement.getRuleID().intValue() < id) {
                    p_counter--;
                    break;
                }
            }
        }
        FlowRuleManager.addRule(results);
    }

}

