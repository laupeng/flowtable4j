package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.infosec.flowtable4j.dal.BaseQuery;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleStatisticGen;

public interface InfoSecurityRuleStatisticGenDAO extends BaseQuery<InfoSecurityRuleStatisticGen>{
	public List<InfoSecurityRuleStatisticGen> getListByRuleId(int flowRuleID) throws SQLException;;
}
