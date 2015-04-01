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

    private void getRuleFull() {
        List<Map<String, Object>> flowRuleMasters = ruleGetter.getFlowRuleMaster();
        List<Map<String, Object>> ruleValues = ruleGetter.getRuleValue();
        List<Map<String, Object>> ruleFields = ruleGetter.getRuleField();
        List<Map<String, Object>> counterSqls = ruleGetter.getCountSql();
        List<FlowRuleStatement> results = new ArrayList<FlowRuleStatement>();

        int p_values = -1, p_fields = -1, p_counter = -1;
        int currentRuleId = 0;

        for (Map<String, Object> flowRuleMaster : flowRuleMasters) {
            //FlowRuleID,RuleName,RiskLevel,OrderType,PrepayType,RuleDesc
            currentRuleId = Integer.valueOf(Objects.toString(flowRuleMaster.get("FlowRuleID"), "0"));
            FlowRuleStatement flowRuleStatement = new FlowRuleStatement();
            flowRuleStatement.setFlowRuleTerms(new ArrayList<FlowRuleTerm>());
            flowRuleStatement.setRuleName(Objects.toString(flowRuleMaster.get("RuleName"), ""));
            flowRuleStatement.setOrderType(Integer.valueOf(Objects.toString(flowRuleMaster.get("OrderType"), "0")));
            flowRuleStatement.setPrepayType(Objects.toString(flowRuleMaster.get("PrepayType"), ""));
            flowRuleStatement.setRemark(Objects.toString(flowRuleMaster.get("RuleDesc"), ""));
            flowRuleStatement.setRiskLevel(Integer.valueOf(Objects.toString(flowRuleMaster.get("RiskLevel"), "0")));
            flowRuleStatement.setRuleID(currentRuleId);
            results.add(flowRuleStatement);

            for (int i=p_values;i<ruleValues.size();i++) {
                p_values++;
                Map<String, Object> value = ruleValues.get(p_values);
                int id = Integer.valueOf(Objects.toString(value.get("FlowRuleID"), "-1"));
                if (currentRuleId == id) {      //属于当前规则的条款
                    String fieldName = Objects.toString(value.get("ColumnName"), "");
                    String op = Objects.toString(value.get("MatchType"), "");
                    String matchValue = Objects.toString(value.get("MatchValue"), "");
                    FlowRuleTerm valueTerm = new ValueMatchRuleTerm(fieldName, op, matchValue);
                    flowRuleStatement.getFlowRuleTerms().add(valueTerm);
                } else if(currentRuleId < id){ //属于下条规则条款
                    p_values--;
                    break;
                }  //比当前规则小，略过
            }

            for (int i=p_fields;i<ruleFields.size();i++) {
                p_fields++;
                Map<String, Object> field = ruleFields.get(p_fields);
                int id = Integer.valueOf(Objects.toString(field.get("FlowRuleID"), "-1"));
                if (currentRuleId == id) {
                    String fieldName = Objects.toString(field.get("ColumnName"), "");
                    String op = Objects.toString(field.get("MatchType"), "");
                    String matchValue = Objects.toString(field.get("MatchValue"), "");
                    FlowRuleTerm fieldTerm = new FieldMatchRuleTerm(fieldName, op, matchValue);
                    flowRuleStatement.getFlowRuleTerms().add(fieldTerm);
                } else if(currentRuleId < id) {
                    p_fields--;
                    break;
                }
            }

            for (int i=p_counter;i<counterSqls.size();i++) {
                p_counter++;
                Map<String, Object> counter = counterSqls.get(p_counter);
                int id = Integer.valueOf(Objects.toString(counter.get("FlowRuleID"), "-1"));
                if (currentRuleId == id) {
                    String fieldName = Objects.toString(counter.get("KeyColumnName"), "");
                    String op = Objects.toString(counter.get("MatchType"), "");
                    String matchValue = Objects.toString(counter.get("MatchValue"), "");
                    String countType = Objects.toString(counter.get("StatisticType"), "");
                    String countField = Objects.toString(counter.get("MatchColumnName"), "");
                    String sql = Objects.toString(counter.get("SqlValue"), "");
                    Integer startOffset = Integer.valueOf(Objects.toString(counter.get("StartTimeLimit"), "0"));
                    Integer endOffset = Integer.valueOf(Objects.toString(counter.get("TimeLimit"), "0"));
                    CounterMatchRuleTerm counterTerm = new CounterMatchRuleTerm(fieldName, op, matchValue);
                    counterTerm.setCountType(countType,countField,sql);
                    counterTerm.setTimeOffset(-startOffset,-endOffset);
                    flowRuleStatement.getFlowRuleTerms().add(counterTerm);
                } else if(currentRuleId < id) {
                    p_counter--;
                    break;
                }
            }
        }
        FlowRuleManager.addRule(results);
    }

}