package com.ctrip.flowtable4j.core.flowRule.entity;

import java.util.List;

/**
 * 
 * @author weiyu
 * @date 2015年3月16日
 */

public class RuleMatchFieldEntity {
	String columnName;
	String matchType;
	String matchValue;
	String tableName;

	List<RuleByScore> ruleScoreList;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<RuleByScore> getRuleScoreList() {
		return ruleScoreList;
	}

	public void setRuleScoreList(List<RuleByScore> ruleScoreList) {
		this.ruleScoreList = ruleScoreList;
	}
	
	
}
