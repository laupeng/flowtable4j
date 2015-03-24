package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class InfoSecurityRuleMatchFieldGen implements DalPojo {
	private Integer fieldMatchID;
	private Integer flowRuleID;
	private Integer fieldID;
	private String matchType;
	private String matchValue;
	private Integer matchIndex;
	private Timestamp dataChange_LastTime;
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

	public Integer getFieldID() {
		return fieldID;
	}

	public void setFieldID(Integer fieldID) {
		this.fieldID = fieldID;
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

	public Integer getMatchIndex() {
		return matchIndex;
	}

	public void setMatchIndex(Integer matchIndex) {
		this.matchIndex = matchIndex;
	}

	public Timestamp getDataChange_LastTime() {
		return dataChange_LastTime;
	}

	public void setDataChange_LastTime(Timestamp dataChange_LastTime) {
		this.dataChange_LastTime = dataChange_LastTime;
	}

}