package com.ctrip.infosec.flowtable4j.flowdata;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class FlowRuleStatement {
    final static Logger logger = LoggerFactory.getLogger(FlowRuleStatement.class);
    private Integer statisticTableID;
    private String keyFieldID1;
    private String keyFieldID2;
    private String statisticTableName;
    private int tableType;
    private int orderType;
    private String dataBaseName;
    private List<FlowRuleTerm> flowRuleTerms;

    /**
     * 有些流量规则需要遍历List，比如用passengerList的每位乘客姓名进行比较
     */
    void setParentNode()
    {
        for(FlowRuleTerm term:flowRuleTerms ){
            term.prefix = ExtraFieldManager.getParentNode(term.fieldName,orderType);
        }
    }

    public List<FlowRuleTerm> getFlowRuleTerms() {
        return flowRuleTerms;
    }

    public void setFlowRuleTerms(List<FlowRuleTerm> flowRuleTerms) {
        this.flowRuleTerms = flowRuleTerms;
    }

    public boolean check(FlowFact fact) {
        boolean match = true;
        try {
            if (flowRuleTerms != null && flowRuleTerms.size() > 0) {
                for (FlowRuleTerm term : flowRuleTerms) {
                    match = term.check(fact);
                    if (!match) {
                        match = false;
                        break;
                    }
                }
            }
        }
        catch (Throwable ex){
            logger.error("error",ex);
        }
        return match;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getStatisticTableID() {
        return statisticTableID;
    }

    public void setStatisticTableID(Integer statisticTableID) {
        this.statisticTableID = statisticTableID;
    }

    public String getKeyFieldID1() {
        return keyFieldID1;
    }

    public void setKeyFieldID1(String keyFieldID1) {
        this.keyFieldID1 = keyFieldID1;
    }

    public String getKeyFieldID2() {
        return keyFieldID2;
    }

    public void setKeyFieldID2(String keyFieldID2) {
        this.keyFieldID2 = keyFieldID2;
    }

    public String getStatisticTableName() {
        return statisticTableName;
    }

    public void setStatisticTableName(String statisticTableName) {
        this.statisticTableName = statisticTableName;
    }

    public int getTableType() {
        return tableType;
    }

    public void setTableType(int tableType) {
        this.tableType = tableType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    @Override
    public int hashCode() {
        return statisticTableID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FlowRuleStatement) {
            FlowRuleStatement rs = (FlowRuleStatement) obj;
            return this.statisticTableID.equals(rs.statisticTableID);
        }
        return super.equals(obj);
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }
}

