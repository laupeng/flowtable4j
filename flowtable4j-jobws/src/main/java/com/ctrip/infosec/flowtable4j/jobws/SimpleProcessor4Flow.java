package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.flowlist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = LoggerFactory.getLogger(SimpleProcessor4Flow.class);
    @Override
    public void execute() {
        getFullFlowRules();
    }

    private void getFullFlowRules() {
        List<Map<String, Object>> flowRuleMasters = ruleGetter.getFlowRuleMaster();
        List<Map<String, Object>> valueTerms = ruleGetter.getValueMatchTerms();
        List<Map<String, Object>> fieldTerms = ruleGetter.getFieldMatchTerms();
        List<Map<String, Object>> countTerms = ruleGetter.getCounterMatchTerms();
        List<FlowRuleStatement> results = new ArrayList<FlowRuleStatement>();

        int p_values = -1, p_fields = -1, p_counter = -1;
        int currentRuleId = 0;
        int id=0;
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

            for (p_values++;p_values<valueTerms.size();p_values++) {
                Map<String, Object> value = valueTerms.get(p_values);
                id = Integer.valueOf(Objects.toString(value.get("FlowRuleID"), "-1"));
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

//            for (int i=(p_fields++);i<fieldTerms.size();i++) {
            for (p_fields++;p_fields<fieldTerms.size();p_fields++) {
                Map<String, Object> field = fieldTerms.get(p_fields);
                id = Integer.valueOf(Objects.toString(field.get("FlowRuleID"), "-1"));
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

//            for (int i=(p_counter++);i<countTerms.size();i++) {
            for (p_counter++;p_counter<countTerms.size();p_counter++) {
                Map<String, Object> counter = countTerms.get(p_counter);
                id = Integer.valueOf(Objects.toString(counter.get("FlowRuleID"), "-1"));
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
        logger.info("total load active flow rules:" + results.size());
    }

}