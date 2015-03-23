package com.ctrip.flowtable4j.core.flowRule;

import java.util.List;

import com.ctrip.flowtable4j.core.dao.cardriskdb.entity.InfoSecurity_CheckResultLog;

public class FlowCheckRiskResult {
	String logMessage;
    List<InfoSecurity_CheckResultLog> logList;//InfoSecurity_CheckResultLog
    boolean isFlowRuleWhite; 
    
    int riskLevel;

	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public List<InfoSecurity_CheckResultLog> getLogList() {
		return logList;
	}

	public void setLogList(List<InfoSecurity_CheckResultLog> logList) {
		this.logList = logList;
	}

	public boolean isFlowRuleWhite() {
		return isFlowRuleWhite;
	}

	public void setFlowRuleWhite(boolean isFlowRuleWhite) {
		this.isFlowRuleWhite = isFlowRuleWhite;
	}

	public int getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}
    
    
}
