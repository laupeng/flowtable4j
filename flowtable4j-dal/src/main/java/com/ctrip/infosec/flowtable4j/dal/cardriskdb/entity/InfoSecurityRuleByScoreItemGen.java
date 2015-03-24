package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class InfoSecurityRuleByScoreItemGen implements DalPojo {
	private Long itemId;
	private Long scoreId;
	private String matchType;
	private String matchValue;
	private Timestamp dataChange_LastTime;
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getScoreId() {
		return scoreId;
	}

	public void setScoreId(Long scoreId) {
		this.scoreId = scoreId;
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

	public Timestamp getDataChange_LastTime() {
		return dataChange_LastTime;
	}

	public void setDataChange_LastTime(Timestamp dataChange_LastTime) {
		this.dataChange_LastTime = dataChange_LastTime;
	}

}