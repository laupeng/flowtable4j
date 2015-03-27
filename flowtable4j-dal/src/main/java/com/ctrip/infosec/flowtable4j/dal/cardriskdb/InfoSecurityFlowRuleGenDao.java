package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.infosec.flowtable4j.dal.BaseDAO;
import com.ctrip.infosec.flowtable4j.dal.BaseQuery;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityFlowRuleGen;

public interface InfoSecurityFlowRuleGenDao extends BaseDAO<InfoSecurityFlowRuleGen,Number>,BaseQuery<InfoSecurityFlowRuleGen> {
	public List<InfoSecurityFlowRuleGen> getListByActive(String active)  throws SQLException;;
}
