package com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class InfoSecurityRuleByScoreGen implements DalPojo {
	private Long scoreId;
	private Integer keyId;
	private String keyType;
	private BigDecimal score;
	private Timestamp dataChange_LastTime;
	private BigDecimal elseScore;
	public Long getScoreId() {
		return scoreId;
	}

	public void setScoreId(Long scoreId) {
		this.scoreId = scoreId;
	}

	public Integer getKeyId() {
		return keyId;
	}

	public void setKeyId(Integer keyId) {
		this.keyId = keyId;
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public Timestamp getDataChange_LastTime() {
		return dataChange_LastTime;
	}

	public void setDataChange_LastTime(Timestamp dataChange_LastTime) {
		this.dataChange_LastTime = dataChange_LastTime;
	}

	public BigDecimal getElseScore() {
		return elseScore;
	}

	public void setElseScore(BigDecimal elseScore) {
		this.elseScore = elseScore;
	}

}