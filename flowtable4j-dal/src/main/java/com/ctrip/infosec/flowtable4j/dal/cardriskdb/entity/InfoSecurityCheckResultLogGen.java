package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class InfoSecurityCheckResultLogGen implements DalPojo {
	private Long logID;
	private Long reqID;
	private String ruleType;
	private Integer ruleID;
	private String ruleName;
	private Integer riskLevel;
	private String ruleRemark;
	private Timestamp createDate;
	private Timestamp dataChange_LastTime;
	public Long getLogID() {
		return logID;
	}

	public void setLogID(Long logID) {
		this.logID = logID;
	}

	public Long getReqID() {
		return reqID;
	}

	public void setReqID(Long reqID) {
		this.reqID = reqID;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public Integer getRuleID() {
		return ruleID;
	}

	public void setRuleID(Integer ruleID) {
		this.ruleID = ruleID;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Integer getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(Integer riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getRuleRemark() {
		return ruleRemark;
	}

	public void setRuleRemark(String ruleRemark) {
		this.ruleRemark = ruleRemark;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getDataChange_LastTime() {
		return dataChange_LastTime;
	}

	public void setDataChange_LastTime(Timestamp dataChange_LastTime) {
		this.dataChange_LastTime = dataChange_LastTime;
	}

}