package com.ctrip.infosec.flowtable4j.flowrule;

import java.util.Map;

public interface CheckRiskCtrip {
	public FlowCheckRiskResult CheckFlowRuleList(Map basicCheckRiskData, Map checkEntity, boolean isFlowRuleWhite, boolean isWhiteCheck);
}
