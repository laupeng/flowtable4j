package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class InfoSecurityRuleStatisticGen implements DalPojo {
	private Integer ruleStatisticID;
	private Integer flowRuleID;
	private String statisticType;
	private Integer statisticTableID;
	private Integer keyFieldID;
	private Integer matchFieldID;
	private String matchType;
	private Integer matchValue;
	private Integer timeLimit;
	private Integer matchIndex;
	private String sqlValue;
	private Integer startTimeLimit;
	private Timestamp dataChange_LastTime;
	public Integer getRuleStatisticID() {
		return ruleStatisticID;
	}

	public void setRuleStatisticID(Integer ruleStatisticID) {
		this.ruleStatisticID = ruleStatisticID;
	}

	public Integer getFlowRuleID() {
		return flowRuleID;
	}

	public void setFlowRuleID(Integer flowRuleID) {
		this.flowRuleID = flowRuleID;
	}

	public String getStatisticType() {
		return statisticType;
	}

	public void setStatisticType(String statisticType) {
		this.statisticType = statisticType;
	}

	public Integer getStatisticTableID() {
		return statisticTableID;
	}

	public void setStatisticTableID(Integer statisticTableID) {
		this.statisticTableID = statisticTableID;
	}

	public Integer getKeyFieldID() {
		return keyFieldID;
	}

	public void setKeyFieldID(Integer keyFieldID) {
		this.keyFieldID = keyFieldID;
	}

	public Integer getMatchFieldID() {
		return matchFieldID;
	}

	public void setMatchFieldID(Integer matchFieldID) {
		this.matchFieldID = matchFieldID;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public Integer getMatchValue() {
		return matchValue;
	}

	public void setMatchValue(Integer matchValue) {
		this.matchValue = matchValue;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Integer getMatchIndex() {
		return matchIndex;
	}

	public void setMatchIndex(Integer matchIndex) {
		this.matchIndex = matchIndex;
	}

	public String getSqlValue() {
		return sqlValue;
	}

	public void setSqlValue(String sqlValue) {
		this.sqlValue = sqlValue;
	}

	public Integer getStartTimeLimit() {
		return startTimeLimit;
	}

	public void setStartTimeLimit(Integer startTimeLimit) {
		this.startTimeLimit = startTimeLimit;
	}

	public Timestamp getDataChange_LastTime() {
		return dataChange_LastTime;
	}

	public void setDataChange_LastTime(Timestamp dataChange_LastTime) {
		this.dataChange_LastTime = dataChange_LastTime;
	}

}