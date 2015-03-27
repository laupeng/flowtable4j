
package com.ctrip.infosec.flowtable4j.flowrule.impl;


import com.ctrip.infosec.flowtable4j.flowrule.entity.FlowRuleEntity;
import com.ctrip.infosec.flowtable4j.flowrule.entity.InfoSecurity_CheckResultLog;

import java.util.ArrayList;
import java.util.List;



public class RuleManagerImpl {
	
	static List<FlowRuleEntity> lst;
	
	public static void SetRuleEntities(List<FlowRuleEntity> ruleList){
		lst = ruleList;
	}
	
    public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType)
    {
    	return GetFlowRuleListByOrderType(strOrderType,"ALL");
    }
	
    public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType)
    {
    	int orderType;
    	orderType = org.apache.commons.lang.math.NumberUtils.toInt(strOrderType, 0);
        List<FlowRuleEntity> list = new ArrayList<FlowRuleEntity>();
        
        for(FlowRuleEntity tmp:GetFlowRuleList() ){
        	if( (tmp.getOrderType()==0 || tmp.getOrderType() == orderType)
        		&& prepayType.equalsIgnoreCase(tmp.getPrepayType())){
        		list.add(tmp);
        	}
        }
        return list;
    }

    /**
     * 根据OrderType获取所有 preType = All 的流量规则
     * @param strOrderType
     * @param isWhiteCheck
     * @return
     */
    public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, boolean isWhiteCheck)
    {
    	return GetFlowRuleListByOrderType(strOrderType,"ALL",isWhiteCheck);
    }
    
    public List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType, boolean isWhiteCheck)
    {
    	int orderType;
    	orderType = org.apache.commons.lang.math.NumberUtils.toInt(strOrderType, 0);
        List<FlowRuleEntity> list = new ArrayList<FlowRuleEntity>();
        
        for(FlowRuleEntity tmp:GetFlowRuleList() ){
        	if( (tmp.getOrderType()==0 || tmp.getOrderType() == orderType)
        		&& prepayType.equalsIgnoreCase(tmp.getPrepayType())){
        		if(isWhiteCheck){
        			if(tmp.getRiskLevel() == 0)
        				list.add(tmp);
        		}else{
        			if(tmp.getRiskLevel() > 0)
        				list.add(tmp);
        		}
        		
        	}
        }
        return list;
    }
    
    
    
    /**
     * 获取全部流量
     * @return
     */
    public List<FlowRuleEntity> GetFlowRuleList(){
    	return lst;
    }
    
    
    public InfoSecurity_CheckResultLog getCheckedFlowRuleInfo(FlowRuleEntity en)
    {
        InfoSecurity_CheckResultLog log = null;

        try
        {
            if (en != null)
            {
                log = new InfoSecurity_CheckResultLog();
                log.setRiskLevel(en.getRiskLevel());
                log.setRuleID(en.getFlowRuleID());
                log.setRuleName( en.getRuleName());
                log.setRuleRemark(en.getRuleDesc());
                log.setRuleType("D");
            }
        }
        catch(Exception e)
        {
            //nRef = (int)RetCodeDefinition.CheckRiskRetCode.SaveCheckedBlackRuleErr;
        }

        return log;
    }
    

}