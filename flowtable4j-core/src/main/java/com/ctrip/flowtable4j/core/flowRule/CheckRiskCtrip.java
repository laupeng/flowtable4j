package com.ctrip.flowtable4j.core.flowRule;

import java.util.Map;

public interface CheckRiskCtrip {
	public FlowCheckRiskResult CheckFlowRuleList(Map basicCheckRiskData, Map checkEntity, boolean isFlowRuleWhite, boolean isWhiteCheck);
}
