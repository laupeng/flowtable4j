package com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.AbstractCardRiskDBDAO;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityRuleMatchFieldGenDao;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleMatchFieldGen;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

public class InfoSecurityRuleMatchFieldGenDaoImpl extends AbstractCardRiskDBDAO implements InfoSecurityRuleMatchFieldGenDao {

	public class InfoSecurityRuleMatchFieldGenRowMapper implements RowMapper<InfoSecurityRuleMatchFieldGen> {

		@Override
		public InfoSecurityRuleMatchFieldGen mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			InfoSecurityRuleMatchFieldGen pojo = new InfoSecurityRuleMatchFieldGen();


			pojo.setFlowRuleID((Integer) rs.getObject("FlowRuleID"));
			pojo.setKeyColumnName((String) rs.getObject("KeyColumnName"));
			pojo.setKeyTableName((String) rs.getObject("KeyTableName"));
			pojo.setMatchType((String) rs.getObject("MatchType"));
			pojo.setMatchValue((String) rs.getObject("MatchValue"));
			pojo.setFieldMatchID((Integer) rs.getObject("FieldMatchID"));
			
			
			return pojo;
		}

	}

	@Override
	public List<InfoSecurityRuleMatchFieldGen> getAll() throws SQLException {
		// TODO Auto-generated method stub
		String sql = "select FlowRuleID, m.ColumnName as KeyColumnName,  m.TableName as KeyTableName, f.MatchType, f.MatchValue, f.FieldMatchID " 
				+ " from InfoSecurity_RuleMatchField f (nolock) "
				+ " join Def_RuleMatchField m (nolock) on m.FieldID = f.FieldID  order by FlowRuleID,f.MatchIndex";

		List<InfoSecurityRuleMatchFieldGen> gens = null;

		gens = this.cardRiskDBTemplate.query(sql, new InfoSecurityRuleMatchFieldGenRowMapper());
		return gens;
	}

	@Override
	public List<InfoSecurityRuleMatchFieldGen> getListByRuleId(int flowRuleID) {
		// TODO Auto-generated method stub
		String sql = "select FlowRuleID, m.ColumnName as KeyColumnName,  m.TableName as KeyTableName, f.MatchType, f.MatchValue, f.FieldMatchID " + " from InfoSecurity_RuleMatchField f (nolock) "
				+ " join Def_RuleMatchField m (nolock) on m.FieldID = f.FieldID "
				+ " where FlowRuleID = ? "
				+ " order by FlowRuleID,f.MatchIndex";

		List<InfoSecurityRuleMatchFieldGen> gens = null;

		gens = this.cardRiskDBTemplate.query(sql, new Object[] { flowRuleID }, new InfoSecurityRuleMatchFieldGenRowMapper());
		return gens;
	}

}