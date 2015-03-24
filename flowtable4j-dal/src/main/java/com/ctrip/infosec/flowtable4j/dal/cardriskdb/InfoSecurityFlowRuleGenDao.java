package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityFlowRuleGen;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InfoSecurityFlowRuleGenDao {
	private static final String DATA_BASE = "CardRiskDB_INSERT_1";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from InfoSecurity_FlowRule WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM InfoSecurity_FlowRule WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by FlowRuleID desc ) as rownum" 
			+" from InfoSecurity_FlowRule (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_InfoSecurity_FlowRule_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_InfoSecurity_FlowRule_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_InfoSecurity_FlowRule_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_InfoSecurity_FlowRule_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_InfoSecurity_FlowRule_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_InfoSecurity_FlowRule_u";
	private static final String RET_CODE = "retcode";
	
	private DalParser<InfoSecurityFlowRuleGen> parser = new InfoSecurityFlowRuleGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<InfoSecurityFlowRuleGen> rowextractor = null;
	private DalTableDao<InfoSecurityFlowRuleGen> client;
	private DalClient baseClient;
	
	public InfoSecurityFlowRuleGenDao() {
		this.client = new DalTableDao<InfoSecurityFlowRuleGen>(parser);
		dbCategory = this.client.getDatabaseCategory();

		this.rowextractor = new DalRowMapperExtractor<InfoSecurityFlowRuleGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query InfoSecurityFlowRuleGen by the specified ID
	 * The ID must be a number
	**/
	public InfoSecurityFlowRuleGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<InfoSecurityFlowRuleGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<InfoSecurityFlowRuleGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}

	public static class InfoSecurityFlowRuleGenParser extends AbstractDalParser<InfoSecurityFlowRuleGen> {
		public static final String DATABASE_NAME = "CardRiskDB_INSERT_1";
		public static final String TABLE_NAME = "InfoSecurity_FlowRule";
		private static final String[] COLUMNS = new String[]{
			"FlowRuleID",
			"RuleName",
			"RiskLevel",
			"OrderType",
			"PrepayType",
			"RuleDesc",
			"CreateDate",
			"DataChange_LastTime ",
			"LastOper",
			"Active",
			"MatchIndex",
			"IsModify",
			"IsHighlight",
			"IsAsync",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"FlowRuleID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
			Types.VARCHAR,
			Types.CHAR,
			Types.INTEGER,
			Types.BIT,
			Types.INTEGER,
			Types.INTEGER,
		};
		
		public InfoSecurityFlowRuleGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public InfoSecurityFlowRuleGen map(ResultSet rs, int rowNum) throws SQLException {
			InfoSecurityFlowRuleGen pojo = new InfoSecurityFlowRuleGen();
			
			pojo.setFlowRuleID((Integer)rs.getObject("FlowRuleID"));
			pojo.setRuleName((String)rs.getObject("RuleName"));
			pojo.setRiskLevel((Integer)rs.getObject("RiskLevel"));
			pojo.setOrderType((Integer)rs.getObject("OrderType"));
			pojo.setPrepayType((String)rs.getObject("PrepayType"));
			pojo.setRuleDesc((String)rs.getObject("RuleDesc"));
			pojo.setCreateDate((Timestamp)rs.getObject("CreateDate"));
			pojo.setDataChange_LastTime ((Timestamp)rs.getObject("DataChange_LastTime "));
			pojo.setLastOper((String)rs.getObject("LastOper"));
			pojo.setActive((String)rs.getObject("Active"));
			pojo.setMatchIndex((Integer)rs.getObject("MatchIndex"));
			pojo.setIsModify((Boolean)rs.getObject("IsModify"));
			pojo.setIsHighlight((Integer)rs.getObject("IsHighlight"));
			pojo.setIsAsync((Integer)rs.getObject("IsAsync"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(InfoSecurityFlowRuleGen pojo) {
			return pojo.getFlowRuleID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(InfoSecurityFlowRuleGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("FlowRuleID", pojo.getFlowRuleID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(InfoSecurityFlowRuleGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("FlowRuleID", pojo.getFlowRuleID());
			map.put("RuleName", pojo.getRuleName());
			map.put("RiskLevel", pojo.getRiskLevel());
			map.put("OrderType", pojo.getOrderType());
			map.put("PrepayType", pojo.getPrepayType());
			map.put("RuleDesc", pojo.getRuleDesc());
			map.put("CreateDate", pojo.getCreateDate());
			map.put("DataChange_LastTime ", pojo.getDataChange_LastTime ());
			map.put("LastOper", pojo.getLastOper());
			map.put("Active", pojo.getActive());
			map.put("MatchIndex", pojo.getMatchIndex());
			map.put("IsModify", pojo.getIsModify());
			map.put("IsHighlight", pojo.getIsHighlight());
			map.put("IsAsync", pojo.getIsAsync());
	
			return map;
		}
	}
}