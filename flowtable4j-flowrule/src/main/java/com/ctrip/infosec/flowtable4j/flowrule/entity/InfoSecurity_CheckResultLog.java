package com.ctrip.infosec.flowtable4j.flowrule.entity;

import java.util.Date;

public class InfoSecurity_CheckResultLog {
	private long logID;
	private long reqID;
	

	private String ruleType;
	

	private int ruleID ;
	

	private String ruleName;
	

	private int riskLevel;
	

	private String ruleRemark;
	

	private Date createDate = new Date(System.currentTimeMillis());


	public long getLogID() {
		return logID;
	}


	public void setLogID(long logID) {
		this.logID = logID;
	}


	public long getReqID() {
		return reqID;
	}


	public void setReqID(long reqID) {
		this.reqID = reqID;
	}


	public String getRuleType() {
		return ruleType;
	}


	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}


	public int getRuleID() {
		return ruleID;
	}


	public void setRuleID(int ruleID) {
		this.ruleID = ruleID;
	}


	public String getRuleName() {
		return ruleName;
	}


	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}


	public int getRiskLevel() {
		return riskLevel;
	}


	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}


	public String getRuleRemark() {
		return ruleRemark;
	}


	public void setRuleRemark(String ruleRemark) {
		this.ruleRemark = ruleRemark;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
