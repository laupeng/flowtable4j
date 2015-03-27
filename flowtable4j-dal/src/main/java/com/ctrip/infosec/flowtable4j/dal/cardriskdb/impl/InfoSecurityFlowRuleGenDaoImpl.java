package com.ctrip.infosec.flowtable4j.dal.cardriskdb.impl;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.AbstractCardRiskDBDAO;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.InfoSecurityFlowRuleGenDao;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityFlowRuleGen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;




import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class InfoSecurityFlowRuleGenDaoImpl extends AbstractCardRiskDBDAO implements InfoSecurityFlowRuleGenDao {
	

	
	
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from InfoSecurity_FlowRule WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM InfoSecurity_FlowRule WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by FlowRuleID desc ) as rownum"
			+ " from InfoSecurity_FlowRule (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_InfoSecurity_FlowRule_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_InfoSecurity_FlowRule_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_InfoSecurity_FlowRule_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_InfoSecurity_FlowRule_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_InfoSecurity_FlowRule_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_InfoSecurity_FlowRule_u";
	private static final String RET_CODE = "retcode";



	public class InfoSecurityFlowRuleGenRowMapper implements RowMapper<InfoSecurityFlowRuleGen> {

		@Override
		public InfoSecurityFlowRuleGen mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			InfoSecurityFlowRuleGen pojo = new InfoSecurityFlowRuleGen();

			pojo.setFlowRuleID((Integer) rs.getObject("FlowRuleID"));
			pojo.setRuleName((String) rs.getObject("RuleName"));
			pojo.setRiskLevel((Integer) rs.getObject("RiskLevel"));
			pojo.setOrderType((Integer) rs.getObject("OrderType"));
			pojo.setPrepayType((String) rs.getObject("PrepayType"));
			pojo.setRuleDesc((String) rs.getObject("RuleDesc"));
			pojo.setCreateDate((Timestamp) rs.getObject("CreateDate"));
			pojo.setDataChange_LastTime((Timestamp) rs.getObject("DataChange_LastTime "));
			pojo.setLastOper((String) rs.getObject("LastOper"));
			pojo.setActive((String) rs.getObject("Active"));
			pojo.setMatchIndex((Integer) rs.getObject("MatchIndex"));
			pojo.setIsModify((Boolean) rs.getObject("IsModify"));
			pojo.setIsHighlight((Integer) rs.getObject("IsHighlight"));
			pojo.setIsAsync((Integer) rs.getObject("IsAsync"));

			return pojo;
		}

	}

	public static final String TABLE_NAME = "InfoSecurity_FlowRule";
	private static final String[] COLUMNS = new String[] { "FlowRuleID", "RuleName", "RiskLevel", "OrderType", "PrepayType", "RuleDesc", "CreateDate", "DataChange_LastTime ", "LastOper", "Active",
			"MatchIndex", "IsModify", "IsHighlight", "IsAsync", };

	private static final String[] PRIMARY_KEYS = new String[] { "FlowRuleID", };

	private static final int[] COLUMN_TYPES = new int[] { Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR,
			Types.CHAR, Types.INTEGER, Types.BIT, Types.INTEGER, Types.INTEGER, };

	public boolean isAutoIncrement() {
		return true;
	}

	public Number getIdentityValue(InfoSecurityFlowRuleGen pojo) {
		return pojo.getFlowRuleID();
	}

	public Map<String, ?> getPrimaryKeys(InfoSecurityFlowRuleGen pojo) {
		Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();

		primaryKeys.put("FlowRuleID", pojo.getFlowRuleID());
		return primaryKeys;
	}

	public Map<String, ?> getFields(InfoSecurityFlowRuleGen pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put("FlowRuleID", pojo.getFlowRuleID());
		map.put("RuleName", pojo.getRuleName());
		map.put("RiskLevel", pojo.getRiskLevel());
		map.put("OrderType", pojo.getOrderType());
		map.put("PrepayType", pojo.getPrepayType());
		map.put("RuleDesc", pojo.getRuleDesc());
		map.put("CreateDate", pojo.getCreateDate());
		map.put("DataChange_LastTime ", pojo.getDataChange_LastTime());
		map.put("LastOper", pojo.getLastOper());
		map.put("Active", pojo.getActive());
		map.put("MatchIndex", pojo.getMatchIndex());
		map.put("IsModify", pojo.getIsModify());
		map.put("IsHighlight", pojo.getIsHighlight());
		map.put("IsAsync", pojo.getIsAsync());

		return map;
	}
	
	
	@Override
	public int insert(InfoSecurityFlowRuleGen entity) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(InfoSecurityFlowRuleGen entity) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(InfoSecurityFlowRuleGen entity) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<InfoSecurityFlowRuleGen> getAll() throws SQLException {
		// TODO Auto-generated method stub
		String sql = ALL_SQL_PATTERN;
		List<InfoSecurityFlowRuleGen> gens = null;
		if(cardRiskDBTemplate!=null){
			gens = this.cardRiskDBTemplate.query(sql , new InfoSecurityFlowRuleGenRowMapper());
		}
		return gens;
	}

	@Override
	public List<InfoSecurityFlowRuleGen> getListByActive(String active) {
		String sql = ALL_SQL_PATTERN + " where Active=?";
		List<InfoSecurityFlowRuleGen> gens = null;
		if(cardRiskDBTemplate!=null){
			gens = this.cardRiskDBTemplate.query(sql, new Object[] { active }, new InfoSecurityFlowRuleGenRowMapper());
		}
		return gens;
	}

	@Override
	public InfoSecurityFlowRuleGen queryByPk(Number id) throws SQLException {
		// TODO Auto-generated method stub
		String sql = ALL_SQL_PATTERN + " where FlowRuleID = ?";
		InfoSecurityFlowRuleGen gen = this.cardRiskDBTemplate.queryForObject(sql, new Object[] { id }, new InfoSecurityFlowRuleGenRowMapper());
		return gen;
	}
}