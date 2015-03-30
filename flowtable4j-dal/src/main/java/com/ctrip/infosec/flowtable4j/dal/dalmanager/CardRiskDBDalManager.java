package com.ctrip.infosec.flowtable4j.dal.dalmanager;

import javax.annotation.Resource;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityFlowRuleGenDao;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityRuleMatchFieldGenDao;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityRuleStatisticGenDAO;

public class CardRiskDBDalManager {
	@Resource(name = "infoSecurityFlowRuleGenDao")
	InfoSecurityFlowRuleGenDao infoSecurityFlowRuleGenDao;
	
	@Resource(name = "infoSecurityRuleMatchFieldGenDao")
	InfoSecurityRuleMatchFieldGenDao infoSecurityRuleMatchFieldGenDao;	
	
	@Resource(name = "infoSecurityRuleStatisticGenDAO")
	InfoSecurityRuleStatisticGenDAO infoSecurityRuleStatisticGenDAO;

	
	
	public InfoSecurityFlowRuleGenDao getInfoSecurityFlowRuleGenDao() {
		return infoSecurityFlowRuleGenDao;
	}

	public InfoSecurityRuleMatchFieldGenDao getInfoSecurityRuleMatchFieldGenDao() {
		return infoSecurityRuleMatchFieldGenDao;
	}

	public InfoSecurityRuleStatisticGenDAO getInfoSecurityRuleStatisticGenDAO() {
		return infoSecurityRuleStatisticGenDAO;
	}
	
	
}
