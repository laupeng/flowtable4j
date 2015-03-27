package com.ctrip.infosec.flowtable4j.flowrule;

import java.util.Map;

public interface RiskFlowControl {
	/**
	 * 执行流量验证
	 * 
	 * @param basicOrderData 订单实体
	 * @param ruleKPIEntity 规则KPI实体
	 * @param isFlowRuleWhite
	 * @param isWhiteCheck
	 * @return
	 */
	public FlowCheckRiskResult CheckFlowRuleList(Map basicOrderData, Map ruleKPIEntity, boolean isFlowRuleWhite, boolean isWhiteCheck);
}
