package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class InfoSecurityFlowRuleGen implements DalPojo {
	private Integer flowRuleID;
	private String ruleName;
	private Integer riskLevel;
	private Integer orderType;
	private String prepayType;
	private String ruleDesc;
	private Timestamp createDate;
	private Timestamp dataChange_LastTime ;
	private String lastOper;
	private String active;
	private Integer matchIndex;
	private Boolean isModify;
	private Integer isHighlight;
	private Integer isAsync;
	public Integer getFlowRuleID() {
		return flowRuleID;
	}

	public void setFlowRuleID(Integer flowRuleID) {
		this.flowRuleID = flowRuleID;
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

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getPrepayType() {
		return prepayType;
	}

	public void setPrepayType(String prepayType) {
		this.prepayType = prepayType;
	}

	public String getRuleDesc() {
		return ruleDesc;
	}

	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getDataChange_LastTime () {
		return dataChange_LastTime ;
	}

	public void setDataChange_LastTime (Timestamp dataChange_LastTime ) {
		this.dataChange_LastTime  = dataChange_LastTime ;
	}

	public String getLastOper() {
		return lastOper;
	}

	public void setLastOper(String lastOper) {
		this.lastOper = lastOper;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Integer getMatchIndex() {
		return matchIndex;
	}

	public void setMatchIndex(Integer matchIndex) {
		this.matchIndex = matchIndex;
	}

	public Boolean getIsModify() {
		return isModify;
	}

	public void setIsModify(Boolean isModify) {
		this.isModify = isModify;
	}

	public Integer getIsHighlight() {
		return isHighlight;
	}

	public void setIsHighlight(Integer isHighlight) {
		this.isHighlight = isHighlight;
	}

	public Integer getIsAsync() {
		return isAsync;
	}

	public void setIsAsync(Integer isAsync) {
		this.isAsync = isAsync;
	}

}