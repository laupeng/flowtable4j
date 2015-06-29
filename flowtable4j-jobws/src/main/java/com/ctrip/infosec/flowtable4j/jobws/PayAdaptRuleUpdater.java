package com.ctrip.infosec.flowtable4j.jobws;
import com.ctrip.infosec.flowtable4j.dal.RuleUpdaterDAO;
import com.ctrip.infosec.flowtable4j.flowdispatch.payAdapt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by zhangsx on 2015/5/6.
 */
@Component
public class PayAdaptRuleUpdater{

    @Autowired
    private RuleUpdaterDAO ruleGetter;

    @Autowired
    private PayAdaptManager payAdaptManager;

    private Logger logger = LoggerFactory.getLogger(FlowRuleUpdater.class);

    public void execute() {
        getFullPayadaptRules();
    }

    private void getFullPayadaptRules(){
        List<Map<String, Object>> flowRuleMasters = ruleGetter.getPayadaptRuleMaster();
        List<Map<String, Object>> valueTerms = ruleGetter.getPayadaptValueMatchTerms();
        List<Map<String, Object>> fieldTerms = ruleGetter.getPayadaptFieldMatchTerms();
        List<Map<String, Object>> countTerms = ruleGetter.getPayadaptCounterMatchTerms();
        List<PayAdaptStatement> results = new ArrayList<PayAdaptStatement>();

        int p_values = -1, p_fields = -1, p_counter = -1;
        int currentRuleId = 0;
        int id=0;
        for (Map<String, Object> flowRuleMaster : flowRuleMasters) {
            currentRuleId = Integer.valueOf(Objects.toString(flowRuleMaster.get("PayAdapterRuleID"), "0"));
            PayAdaptStatement payAdaptStatement = new PayAdaptStatement();

            payAdaptStatement.setSceneType(Objects.toString(flowRuleMaster.get("SceneType"), ""));
            payAdaptStatement.setIsCheckAccount(Integer.valueOf(Objects.toString(flowRuleMaster.get("IsCheckAccount"), "0")));
            payAdaptStatement.setOrderType(Integer.valueOf(Objects.toString(flowRuleMaster.get("OrderType"), "0")));
            payAdaptStatement.setPaymentStatus(Objects.toString(flowRuleMaster.get("PaymentStatus"), ""));
            payAdaptStatement.setRiskLevel(Integer.valueOf(Objects.toString(flowRuleMaster.get("RiskLevel"), "0")));
            payAdaptStatement.setRuleDesc(Objects.toString(flowRuleMaster.get("RuleDesc"), ""));
            payAdaptStatement.setRuleID(Integer.valueOf(Objects.toString(flowRuleMaster.get("PayAdapterRuleID"), "0")));
            payAdaptStatement.setRuleName(Objects.toString(flowRuleMaster.get("RuleName"), ""));
            payAdaptStatement.setFlowRuleTerms(new ArrayList<PayAdaptRuleTerm>());
            results.add(payAdaptStatement);

            for (p_values++;p_values<valueTerms.size();p_values++) {
                Map<String, Object> value = valueTerms.get(p_values);
                id = Integer.valueOf(Objects.toString(value.get("PayAdapterRuleID"), "-1"));
                if (currentRuleId == id) {      //属于当前规则的条款
                    String fieldName = Objects.toString(value.get("ColumnName"), "");
                    String op = Objects.toString(value.get("MatchType"), "").toUpperCase();
                    String matchValue = Objects.toString(value.get("MatchValue"), "");
                    PayAdaptRuleTerm valueTerm = new ValueMatchRuleTerm(fieldName, op, matchValue);
                    payAdaptStatement.getFlowRuleTerms().add(valueTerm);
                } else if(currentRuleId < id){ //属于下条规则条款
                    p_values--;
                    break;
                }  //比当前规则小，略过
            }

            for (p_fields++;p_fields<fieldTerms.size();p_fields++) {
                Map<String, Object> field = fieldTerms.get(p_fields);
                id = Integer.valueOf(Objects.toString(field.get("PayAdapterRuleID"), "-1"));
                if (currentRuleId == id) {
                    String fieldName = Objects.toString(field.get("ColumnName"), "");
                    String op = Objects.toString(field.get("MatchType"), "").toUpperCase();
                    String matchValue = Objects.toString(field.get("MatchValue"), "");
                    PayAdaptRuleTerm fieldTerm = new FieldMatchRuleTerm(fieldName, op, matchValue);
                    payAdaptStatement.getFlowRuleTerms().add(fieldTerm);
                } else if(currentRuleId < id) {
                    p_fields--;
                    break;
                }
            }

            for (p_counter++;p_counter<countTerms.size();p_counter++) {
                Map<String, Object> counter = countTerms.get(p_counter);
                id = Integer.valueOf(Objects.toString(counter.get("FlowRuleID"), "-1"));
                if (currentRuleId == id) {
                    String fieldName = Objects.toString(counter.get("KeyColumnName"), "");
                    String op = Objects.toString(counter.get("MatchType"), "").toUpperCase();
                    String matchValue = Objects.toString(counter.get("MatchValue"), "");
                    String countType = Objects.toString(counter.get("StatisticType"), "");
                    String countField = Objects.toString(counter.get("MatchColumnName"), "");
                    String sql = Objects.toString(counter.get("SqlValue"), "");
                    Integer startOffset = Integer.valueOf(Objects.toString(counter.get("StartTimeLimit"), "0"));
                    Integer endOffset = Integer.valueOf(Objects.toString(counter.get("TimeLimit"), "0"));
                    CounterMatchRuleTerm counterTerm = new CounterMatchRuleTerm(fieldName, op, matchValue);
                    counterTerm.setCountType(countType,countField,sql);
                    counterTerm.setTimeOffset(-startOffset,-endOffset);
                    payAdaptStatement.getFlowRuleTerms().add(counterTerm);
                } else if(currentRuleId < id) {
                    p_counter--;
                    break;
                }
            }
        }
        payAdaptManager.addRule(results);
        logger.info("total load active payadapt rules:" + results.size());
    }
}
