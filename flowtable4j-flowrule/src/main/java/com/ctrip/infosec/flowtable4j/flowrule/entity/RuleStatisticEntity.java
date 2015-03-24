package com.ctrip.infosec.flowtable4j.flowrule.entity;

import java.util.List;

/**
 * 规则统计项
 * 
 * @author weiyu
 * @date 2015年3月16日
 */
public class RuleStatisticEntity {
	 String keyColumnName;
	 String matchColumnName;
	 String keyTableName;
	 String matchTableName;
	 String matchType;
	 String matchValue;
	 String statisticType;
	 int startTimeLimit; // 单位：分钟
	 int timeLimit; // 单位：分钟
	 String sqlValue;

	 List<RuleByScore> ruleScoreList;

	public String getKeyColumnName() {
		return keyColumnName;
	}

	public void setKeyColumnName(String keyColumnName) {
		this.keyColumnName = keyColumnName;
	}

	public String getMatchColumnName() {
		return matchColumnName;
	}

	public void setMatchColumnName(String matchColumnName) {
		this.matchColumnName = matchColumnName;
	}

	public String getKeyTableName() {
		return keyTableName;
	}

	public void setKeyTableName(String keyTableName) {
		this.keyTableName = keyTableName;
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

	public String getMatchValue() {
		return matchValue;
	}

	public void setMatchValue(String matchValue) {
		this.matchValue = matchValue;
	}

	public String getStatisticType() {
		return statisticType;
	}

	public void setStatisticType(String statisticType) {
		this.statisticType = statisticType;
	}

	public int getStartTimeLimit() {
		return startTimeLimit;
	}

	public void setStartTimeLimit(int startTimeLimit) {
		this.startTimeLimit = startTimeLimit;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getSqlValue() {
		return sqlValue;
	}

	public void setSqlValue(String sqlValue) {
		this.sqlValue = sqlValue;
	}

	public List<RuleByScore> getRuleScoreList() {
		return ruleScoreList;
	}

	public void setRuleScoreList(List<RuleByScore> ruleScoreList) {
		this.ruleScoreList = ruleScoreList;
	}
	 
	 
}