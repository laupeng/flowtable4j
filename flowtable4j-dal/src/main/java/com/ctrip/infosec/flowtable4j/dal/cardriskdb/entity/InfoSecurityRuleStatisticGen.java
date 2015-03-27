package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;



import com.ctrip.infosec.flowtable4j.dal.BaseGen;


public class InfoSecurityRuleStatisticGen  implements BaseGen {
	private Integer ruleStatisticID;
	private Integer flowRuleID;
	private String statisticType;
	private Integer timeLimit;
	private String sqlValue;
	private Integer startTimeLimit;

	private String keyColumnName;
	private String keyTableName;
	private String matchColumnName;
	private String matchTableName;
	
	private String matchType;
	private Integer matchValue;
	
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
	public Integer getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
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
	public String getKeyColumnName() {
		return keyColumnName;
	}
	public void setKeyColumnName(String keyColumnName) {
		this.keyColumnName = keyColumnName;
	}
	public String getKeyTableName() {
		return keyTableName;
	}
	public void setKeyTableName(String keyTableName) {
		this.keyTableName = keyTableName;
	}
	public String getMatchColumnName() {
		return matchColumnName;
	}
	public void setMatchColumnName(String matchColumnName) {
		this.matchColumnName = matchColumnName;
	}
	public String getMatchTableName() {
		return matchTableName;
	}
	public void setMatchTableName(String matchTableName) {
		this.matchTableName = matchTableName;
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
	
	



}