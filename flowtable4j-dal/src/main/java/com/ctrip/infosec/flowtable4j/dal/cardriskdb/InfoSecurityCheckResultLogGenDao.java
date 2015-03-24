package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityCheckResultLogGen;
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

public class InfoSecurityCheckResultLogGenDao {
	private static final String DATA_BASE = "CardRiskDB_INSERT_1";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from InfoSecurity_CheckResultLog WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM InfoSecurity_CheckResultLog WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by LogID desc ) as rownum" 
			+" from InfoSecurity_CheckResultLog (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_InfoSecurity_CheckResultLog_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_InfoSecurity_CheckResultLog_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_InfoSecurity_CheckResultLog_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_InfoSecurity_CheckResultLog_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_InfoSecurity_CheckResultLog_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_InfoSecurity_CheckResultLog_u";
	private static final String RET_CODE = "retcode";
	
	private DalParser<InfoSecurityCheckResultLogGen> parser = new InfoSecurityCheckResultLogGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<InfoSecurityCheckResultLogGen> rowextractor = null;
	private DalTableDao<InfoSecurityCheckResultLogGen> client;
	private DalClient baseClient;
	
	public InfoSecurityCheckResultLogGenDao() {
		this.client = new DalTableDao<InfoSecurityCheckResultLogGen>(parser);
		dbCategory = this.client.getDatabaseCategory();

		this.rowextractor = new DalRowMapperExtractor<InfoSecurityCheckResultLogGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query InfoSecurityCheckResultLogGen by the specified ID
	 * The ID must be a number
	**/
	public InfoSecurityCheckResultLogGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<InfoSecurityCheckResultLogGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<InfoSecurityCheckResultLogGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
	/**
	 * SP Insert
	**/
	public int insert(DalHints hints, InfoSecurityCheckResultLogGen daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_INSERT_SP_NAME, parameters, parser.getFields(daoPojo));
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		return (Integer)results.get(RET_CODE);
	}
	/**
	 * SP Insert
	**/
	public int insert(DalHints hints, KeyHolder holder, InfoSecurityCheckResultLogGen daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_INSERT_SP_NAME, parameters, parser.getFields(daoPojo));
		parameters.registerInOut("LogID", Types.BIGINT, daoPojo.getLogID());
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		if(holder != null){
			Map<String, Object> map = new LinkedHashMap<String, Object>();
		    map.put("LogID", parameters.get("LogID", ParameterDirection.InputOutput).getValue());
	        holder.getKeyList().add(map);
		}
		return (Integer)results.get(RET_CODE);
	}
	/**
	 * Batch insert without out parameters
	 * Return how many rows been affected for each of parameters
	**/
	public int[] insert(DalHints hints, InfoSecurityCheckResultLogGen...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_INSERT_SP_NAME, parser.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for(int i = 0; i< daoPojos.length; i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
	/**
	 * Batch insert without out parameters
	 * Return how many rows been affected for each of parameters
	**/
	public int[] insert(DalHints hints, List<InfoSecurityCheckResultLogGen> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_INSERT_SP_NAME, parser.getFields(daoPojos.get(0)).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		for(int i = 0; i< daoPojos.size(); i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos.get(i)));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}
	public static class InfoSecurityCheckResultLogGenParser extends AbstractDalParser<InfoSecurityCheckResultLogGen> {
		public static final String DATABASE_NAME = "CardRiskDB_INSERT_1";
		public static final String TABLE_NAME = "InfoSecurity_CheckResultLog";
		private static final String[] COLUMNS = new String[]{
			"LogID",
			"ReqID",
			"RuleType",
			"RuleID",
			"RuleName",
			"RiskLevel",
			"RuleRemark",
			"CreateDate",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"LogID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.BIGINT,
			Types.BIGINT,
			Types.CHAR,
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.VARCHAR,
			Types.TIMESTAMP,
			Types.TIMESTAMP,
		};
		
		public InfoSecurityCheckResultLogGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public InfoSecurityCheckResultLogGen map(ResultSet rs, int rowNum) throws SQLException {
			InfoSecurityCheckResultLogGen pojo = new InfoSecurityCheckResultLogGen();
			
			pojo.setLogID((Long)rs.getObject("LogID"));
			pojo.setReqID((Long)rs.getObject("ReqID"));
			pojo.setRuleType((String)rs.getObject("RuleType"));
			pojo.setRuleID((Integer)rs.getObject("RuleID"));
			pojo.setRuleName((String)rs.getObject("RuleName"));
			pojo.setRiskLevel((Integer)rs.getObject("RiskLevel"));
			pojo.setRuleRemark((String)rs.getObject("RuleRemark"));
			pojo.setCreateDate((Timestamp)rs.getObject("CreateDate"));
			pojo.setDataChange_LastTime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(InfoSecurityCheckResultLogGen pojo) {
			return pojo.getLogID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(InfoSecurityCheckResultLogGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("LogID", pojo.getLogID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(InfoSecurityCheckResultLogGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("LogID", pojo.getLogID());
			map.put("ReqID", pojo.getReqID());
			map.put("RuleType", pojo.getRuleType());
			map.put("RuleID", pojo.getRuleID());
			map.put("RuleName", pojo.getRuleName());
			map.put("RiskLevel", pojo.getRiskLevel());
			map.put("RuleRemark", pojo.getRuleRemark());
			map.put("CreateDate", pojo.getCreateDate());
			map.put("DataChange_LastTime", pojo.getDataChange_LastTime());
	
			return map;
		}
	}
}