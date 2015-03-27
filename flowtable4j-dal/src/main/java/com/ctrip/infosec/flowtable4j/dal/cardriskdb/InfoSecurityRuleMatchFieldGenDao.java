package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import java.util.List;

import com.ctrip.infosec.flowtable4j.dal.BaseDAO;
import com.ctrip.infosec.flowtable4j.dal.BaseQuery;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleMatchFieldGen;

public interface InfoSecurityRuleMatchFieldGenDao extends BaseQuery<InfoSecurityRuleMatchFieldGen> {
	public List<InfoSecurityRuleMatchFieldGen> getListByRuleId(int flowRuleID);
}
