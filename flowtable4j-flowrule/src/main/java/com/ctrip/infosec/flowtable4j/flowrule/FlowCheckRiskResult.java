package com.ctrip.infosec.flowtable4j.flowrule;

import com.ctrip.infosec.flowtable4j.flowrule.entity.InfoSecurity_CheckResultLog;

import java.util.List;

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
