package com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.AbstractCardRiskDBDAO;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityRuleStatisticGenDAO;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleMatchFieldGen;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleStatisticGen;

public class InfoSecurityRuleStatisticGenDAOImpl extends AbstractCardRiskDBDAO  implements InfoSecurityRuleStatisticGenDAO {

	public class InfoSecurityRuleStatisticGenRowMapper implements RowMapper<InfoSecurityRuleStatisticGen> {

		@Override
		public InfoSecurityRuleStatisticGen mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			InfoSecurityRuleStatisticGen pojo = new InfoSecurityRuleStatisticGen();


			pojo.setFlowRuleID((Integer) rs.getObject("FlowRuleID"));
			pojo.setKeyColumnName((String) rs.getObject("KeyColumnName"));
			pojo.setMatchColumnName((String) rs.getObject("MatchColumnName"));
			pojo.setKeyTableName((String) rs.getObject("KeyTableName"));
			pojo.setMatchTableName((String) rs.getObject("MatchTableName"));
			pojo.setStatisticType((String) rs.getObject("StatisticType"));
			pojo.setStartTimeLimit((Integer) rs.getObject("StartTimeLimit"));
			pojo.setTimeLimit((Integer) rs.getObject("TimeLimit"));
			pojo.setSqlValue((String) rs.getObject("SqlValue"));
			pojo.setRuleStatisticID((Integer) rs.getObject("RuleStatisticID"));
			pojo.setMatchType((String) rs.getObject("MatchType"));
			pojo.setMatchValue((Integer) rs.getObject("MatchValue"));
			return pojo;
		}

	}
	
	@Override
	public List<InfoSecurityRuleStatisticGen> getAll() throws SQLException {
		// TODO Auto-generated method stub
		String sql = "select FlowRuleID, f.ColumnName as KeyColumnName,f1.ColumnName as MatchColumnName, f.TableName as KeyTableName,"
                                           + " f1.TableName as MatchTableName,"
                                           + " s.MatchType,"
                                           + " s.MatchValue,"
                                           + " s.StatisticType,"
                                           + " s.StartTimeLimit,"
                                           + " s.TimeLimit,"
                                           + " s.SqlValue,"
                                           + " s.RuleStatisticID "
                                           + " from InfoSecurity_RuleStatistic s (nolock) "
                                           + " join Def_RuleMatchField f (nolock) on s.KeyFieldID = f.FieldID "
                                      + " join Def_RuleMatchField f1 (nolock) on s.MatchFieldID = f1.FieldID "
                                      + " order by FlowRuleID,s.MatchIndex";
		return this.cardRiskDBTemplate.query(sql, new InfoSecurityRuleStatisticGenRowMapper());
	}

	@Override
	public List<InfoSecurityRuleStatisticGen> getListByRuleId(int flowRuleID) {
		// TODO Auto-generated method stub
		String sql = "select FlowRuleID, f.ColumnName as KeyColumnName,f1.ColumnName as MatchColumnName, f.TableName as KeyTableName,"
                + " f1.TableName as MatchTableName,"
                + " s.MatchType,"
                + " s.MatchValue,"
                + " s.StatisticType,"
                + " s.StartTimeLimit,"
                + " s.TimeLimit,"
                + " s.SqlValue,"
                + " s.RuleStatisticID "
                + " from InfoSecurity_RuleStatistic s (nolock) "
                + " join Def_RuleMatchField f (nolock) on s.KeyFieldID = f.FieldID "
           + " join Def_RuleMatchField f1 (nolock) on s.MatchFieldID = f1.FieldID "
           + " WHERE flowruleid = ? "
           + " order by FlowRuleID,s.MatchIndex";
		return this.cardRiskDBTemplate.query(sql,new Object[]{flowRuleID}, new InfoSecurityRuleStatisticGenRowMapper());
	}

}
