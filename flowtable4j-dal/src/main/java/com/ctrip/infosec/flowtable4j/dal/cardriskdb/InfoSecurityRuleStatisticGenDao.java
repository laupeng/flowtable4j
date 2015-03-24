package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleStatisticGen;
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

public class InfoSecurityRuleStatisticGenDao {
	private static final String DATA_BASE = "CardRiskDB_INSERT_1";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from InfoSecurity_RuleStatistic WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM InfoSecurity_RuleStatistic WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by RuleStatisticID desc ) as rownum" 
			+" from InfoSecurity_RuleStatistic (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_InfoSecurity_RuleStatistic_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_InfoSecurity_RuleStatistic_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_InfoSecurity_RuleStatistic_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_InfoSecurity_RuleStatistic_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_InfoSecurity_RuleStatistic_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_InfoSecurity_RuleStatistic_u";
	private static final String RET_CODE = "retcode";
	
	private DalParser<InfoSecurityRuleStatisticGen> parser = new InfoSecurityRuleStatisticGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<InfoSecurityRuleStatisticGen> rowextractor = null;
	private DalTableDao<InfoSecurityRuleStatisticGen> client;
	private DalClient baseClient;
	
	public InfoSecurityRuleStatisticGenDao() {
		this.client = new DalTableDao<InfoSecurityRuleStatisticGen>(parser);
		dbCategory = this.client.getDatabaseCategory();

		this.rowextractor = new DalRowMapperExtractor<InfoSecurityRuleStatisticGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query InfoSecurityRuleStatisticGen by the specified ID
	 * The ID must be a number
	**/
	public InfoSecurityRuleStatisticGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<InfoSecurityRuleStatisticGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<InfoSecurityRuleStatisticGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}

	public static class InfoSecurityRuleStatisticGenParser extends AbstractDalParser<InfoSecurityRuleStatisticGen> {
		public static final String DATABASE_NAME = "CardRiskDB_INSERT_1";
		public static final String TABLE_NAME = "InfoSecurity_RuleStatistic";
		private static final String[] COLUMNS = new String[]{
			"RuleStatisticID",
			"FlowRuleID",
			"StatisticType",
			"StatisticTableID",
			"KeyFieldID",
			"MatchFieldID",
			"MatchType",
			"MatchValue",
			"TimeLimit",
			"MatchIndex",
			"SqlValue",
			"StartTimeLimit",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"RuleStatisticID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.TIMESTAMP,
		};
		
		public InfoSecurityRuleStatisticGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public InfoSecurityRuleStatisticGen map(ResultSet rs, int rowNum) throws SQLException {
			InfoSecurityRuleStatisticGen pojo = new InfoSecurityRuleStatisticGen();
			
			pojo.setRuleStatisticID((Integer)rs.getObject("RuleStatisticID"));
			pojo.setFlowRuleID((Integer)rs.getObject("FlowRuleID"));
			pojo.setStatisticType((String)rs.getObject("StatisticType"));
			pojo.setStatisticTableID((Integer)rs.getObject("StatisticTableID"));
			pojo.setKeyFieldID((Integer)rs.getObject("KeyFieldID"));
			pojo.setMatchFieldID((Integer)rs.getObject("MatchFieldID"));
			pojo.setMatchType((String)rs.getObject("MatchType"));
			pojo.setMatchValue((Integer)rs.getObject("MatchValue"));
			pojo.setTimeLimit((Integer)rs.getObject("TimeLimit"));
			pojo.setMatchIndex((Integer)rs.getObject("MatchIndex"));
			pojo.setSqlValue((String)rs.getObject("SqlValue"));
			pojo.setStartTimeLimit((Integer)rs.getObject("StartTimeLimit"));
			pojo.setDataChange_LastTime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(InfoSecurityRuleStatisticGen pojo) {
			return pojo.getRuleStatisticID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(InfoSecurityRuleStatisticGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("RuleStatisticID", pojo.getRuleStatisticID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(InfoSecurityRuleStatisticGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("RuleStatisticID", pojo.getRuleStatisticID());
			map.put("FlowRuleID", pojo.getFlowRuleID());
			map.put("StatisticType", pojo.getStatisticType());
			map.put("StatisticTableID", pojo.getStatisticTableID());
			map.put("KeyFieldID", pojo.getKeyFieldID());
			map.put("MatchFieldID", pojo.getMatchFieldID());
			map.put("MatchType", pojo.getMatchType());
			map.put("MatchValue", pojo.getMatchValue());
			map.put("TimeLimit", pojo.getTimeLimit());
			map.put("MatchIndex", pojo.getMatchIndex());
			map.put("SqlValue", pojo.getSqlValue());
			map.put("StartTimeLimit", pojo.getStartTimeLimit());
			map.put("DataChange_LastTime", pojo.getDataChange_LastTime());
	
			return map;
		}
	}
}