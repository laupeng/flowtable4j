package com.ctrip.infosec.flowtable4j.flowrule.entity;

import java.util.List;

/**
 * 流量规则校验缓存数据
 * 
 * @author weiyu
 * @date 2015年3月16日
 */
public class FlowRuleEntity {
	int flowRuleID;
	String ruleName;
	int riskLevel;
	int orderType;
	String prepayType;
	String ruleDesc;

	List<RuleMatchFieldEntity> matchFieldListItem;
	List<RuleStatisticEntity> statisticListItem;

	public int getFlowRuleID() {
		return flowRuleID;
	}

	public void setFlowRuleID(int flowRuleID) {
		this.flowRuleID = flowRuleID;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public int getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
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

	public List<RuleMatchFieldEntity> getMatchFieldListItem() {
		return matchFieldListItem;
	}

	public void setMatchFieldListItem(List<RuleMatchFieldEntity> matchFieldListItem) {
		this.matchFieldListItem = matchFieldListItem;
	}

	public List<RuleStatisticEntity> getStatisticListItem() {
		return statisticListItem;
	}

	public void setStatisticListItem(List<RuleStatisticEntity> statisticListItem) {
		this.statisticListItem = statisticListItem;
	}

}






