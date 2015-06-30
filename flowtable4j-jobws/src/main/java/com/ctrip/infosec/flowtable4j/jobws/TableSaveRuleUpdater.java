package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.dal.CheckRiskDAO;
import com.ctrip.infosec.flowtable4j.flowdata.*;
import com.ctrip.infosec.flowtable4j.dal.RuleUpdaterDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by zhangsx on 2015/6/29.
 */
@Component
public class TableSaveRuleUpdater {

    @Autowired
    private RuleUpdaterDAO ruleUpdaterDAO;

    @Autowired
    private CheckRiskDAO checkRiskDAO;

    @Autowired
    private TableSaveRuleManager flowRuleManager;
    public void execute() {
        List<Map<String, Object>> statisticTables = ruleUpdaterDAO.getRuleStatisticTable();
        List<Map<String, Object>> statisticTableFiltersValue = ruleUpdaterDAO.getStatisticTableFilterValue();
        List<Map<String, Object>> statisticTableFiltersField = ruleUpdaterDAO.getStatisticTableFilterField();

        Map<String,String> originalRiskLevelTables =  new HashMap<String, String>();

        List<FlowRuleStatement> results = new ArrayList<FlowRuleStatement>();
        int p_value = -1;
        int p_fields = -1;
        int currentRuleId = 0;
        int id = 0;
        for (Map<String, Object> master : statisticTables) {
            currentRuleId = Integer.valueOf(Objects.toString(master.get("StatisticTableID"), "0"));
            FlowRuleStatement flowRuleStatement = new FlowRuleStatement();
            flowRuleStatement.setKeyFieldID1(Objects.toString(master.get("KeyFieldID1"), ""));
            flowRuleStatement.setKeyFieldID2(Objects.toString(master.get("KeyFieldID2"), ""));
            flowRuleStatement.setStatisticTableName(Objects.toString(master.get("StatisticTableName"),""));
            flowRuleStatement.setOrderType(Integer.parseInt(Objects.toString(master.get("OrderType"), "0")));
            flowRuleStatement.setStatisticTableID(currentRuleId);
            flowRuleStatement.setTableType(Integer.parseInt(Objects.toString(master.get("TableType"), "0")));
            flowRuleStatement.setFlowRuleTerms(new ArrayList<FlowRuleTerm>());
            results.add(flowRuleStatement);
            if(flowRuleStatement.getTableType()==1){
                originalRiskLevelTables.put(String.format("%s|%s",flowRuleStatement.getKeyFieldID1(),flowRuleStatement.getOrderType()).toUpperCase(),flowRuleStatement.getStatisticTableName());
            }
            for (p_value++; p_value < statisticTableFiltersValue.size(); p_value++) {
                Map<String, Object> value = statisticTableFiltersValue.get(p_value);
                id = Integer.valueOf(Objects.toString(value.get("StatisticTableID"), "-1"));
                if (currentRuleId == id) {      //属于当前规则的条款
                    String fieldName = Objects.toString(value.get("KeyColumnName"), "");
                    String op = Objects.toString(value.get("MatchType"), "");
                    String matchValue = Objects.toString(value.get("MatchValue"), "");
                    FlowRuleTerm valueTerm = new ValueMatchRuleTerm(fieldName, op, matchValue);
                    flowRuleStatement.getFlowRuleTerms().add(valueTerm);
                } else if (currentRuleId < id) { //属于下条规则条款
                    p_value--;
                    break;
                }  //比当前规则小，略过
            }

            for (p_fields++;p_fields<statisticTableFiltersField.size();p_fields++) {
                Map<String, Object> field = statisticTableFiltersField.get(p_fields);
                id = Integer.valueOf(Objects.toString(field.get("StatisticTableID"), "-1"));
                if (currentRuleId == id) {
                    String fieldName = Objects.toString(field.get("KeyColumnName"), "");
                    String op = Objects.toString(field.get("MatchType"), "").toUpperCase();
                    String matchValue = Objects.toString(field.get("MatchValue"), "");
                    FlowRuleTerm fieldTerm = new FieldMatchRuleTerm(fieldName, op, matchValue);
                    flowRuleStatement.getFlowRuleTerms().add(fieldTerm);
                } else if(currentRuleId < id) {
                    p_fields--;
                    break;
                }
            }
        }
        flowRuleManager.addRule(results);
        checkRiskDAO.updateOriginRiskLevelTable(originalRiskLevelTables);
    }
}
