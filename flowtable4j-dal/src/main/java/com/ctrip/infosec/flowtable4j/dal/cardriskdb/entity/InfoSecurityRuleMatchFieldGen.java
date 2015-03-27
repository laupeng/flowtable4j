package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;

import java.sql.Timestamp;

import com.ctrip.infosec.flowtable4j.dal.BaseGen;


public class InfoSecurityRuleMatchFieldGen  implements BaseGen {
	private Integer fieldMatchID;
	private Integer flowRuleID;

	private String matchType;
	private String matchValue;

	
	
	private String keyColumnName;
	private String keyTableName;
	
	public Integer getFieldMatchID() {
		return fieldMatchID;
	}

	public void setFieldMatchID(Integer fieldMatchID) {
		this.fieldMatchID = fieldMatchID;
	}

	public Integer getFlowRuleID() {
		return flowRuleID;
	}

	public void setFlowRuleID(Integer flowRuleID) {
		this.flowRuleID = flowRuleID;
	}


	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public String getMatchValue() {
		return matchValue;
	}

	public void setMatchValue(String matchValue) {
		this.matchValue = matchValue;
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

}