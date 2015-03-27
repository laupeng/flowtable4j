package com.ctrip.infosec.flowtable4j.flowrule;

import java.util.List;

import com.ctrip.infosec.flowtable4j.flowrule.entity.FlowRuleEntity;
import com.ctrip.infosec.flowtable4j.flowrule.entity.InfoSecurity_CheckResultLog;

public interface RuleManager {
	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType);
	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType);
	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, boolean isWhiteCheck);
	public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType, boolean isWhiteCheck);
	public List<FlowRuleEntity> GetFlowRuleList();
	public InfoSecurity_CheckResultLog getCheckedFlowRuleInfo(FlowRuleEntity entity);
	

}
