package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleMatchFieldGen;
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

public class InfoSecurityRuleMatchFieldGenDao {
	private static final String DATA_BASE = "CardRiskDB_INSERT_1";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from InfoSecurity_RuleMatchField WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM InfoSecurity_RuleMatchField WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by FieldMatchID desc ) as rownum" 
			+" from InfoSecurity_RuleMatchField (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_InfoSecurity_RuleMatchField_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_InfoSecurity_RuleMatchField_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_InfoSecurity_RuleMatchField_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_InfoSecurity_RuleMatchField_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_InfoSecurity_RuleMatchField_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_InfoSecurity_RuleMatchField_u";
	private static final String RET_CODE = "retcode";
	
	private DalParser<InfoSecurityRuleMatchFieldGen> parser = new InfoSecurityRuleMatchFieldGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<InfoSecurityRuleMatchFieldGen> rowextractor = null;
	private DalTableDao<InfoSecurityRuleMatchFieldGen> client;
	private DalClient baseClient;
	
	public InfoSecurityRuleMatchFieldGenDao() {
		this.client = new DalTableDao<InfoSecurityRuleMatchFieldGen>(parser);
		dbCategory = this.client.getDatabaseCategory();

		this.rowextractor = new DalRowMapperExtractor<InfoSecurityRuleMatchFieldGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query InfoSecurityRuleMatchFieldGen by the specified ID
	 * The ID must be a number
	**/
	public InfoSecurityRuleMatchFieldGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<InfoSecurityRuleMatchFieldGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<InfoSecurityRuleMatchFieldGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}

	public static class InfoSecurityRuleMatchFieldGenParser extends AbstractDalParser<InfoSecurityRuleMatchFieldGen> {
		public static final String DATABASE_NAME = "CardRiskDB_INSERT_1";
		public static final String TABLE_NAME = "InfoSecurity_RuleMatchField";
		private static final String[] COLUMNS = new String[]{
			"FieldMatchID",
			"FlowRuleID",
			"FieldID",
			"MatchType",
			"MatchValue",
			"MatchIndex",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"FieldMatchID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.TIMESTAMP,
		};
		
		public InfoSecurityRuleMatchFieldGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public InfoSecurityRuleMatchFieldGen map(ResultSet rs, int rowNum) throws SQLException {
			InfoSecurityRuleMatchFieldGen pojo = new InfoSecurityRuleMatchFieldGen();
			
			pojo.setFieldMatchID((Integer)rs.getObject("FieldMatchID"));
			pojo.setFlowRuleID((Integer)rs.getObject("FlowRuleID"));
			pojo.setFieldID((Integer)rs.getObject("FieldID"));
			pojo.setMatchType((String)rs.getObject("MatchType"));
			pojo.setMatchValue((String)rs.getObject("MatchValue"));
			pojo.setMatchIndex((Integer)rs.getObject("MatchIndex"));
			pojo.setDataChange_LastTime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(InfoSecurityRuleMatchFieldGen pojo) {
			return pojo.getFieldMatchID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(InfoSecurityRuleMatchFieldGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("FieldMatchID", pojo.getFieldMatchID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(InfoSecurityRuleMatchFieldGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("FieldMatchID", pojo.getFieldMatchID());
			map.put("FlowRuleID", pojo.getFlowRuleID());
			map.put("FieldID", pojo.getFieldID());
			map.put("MatchType", pojo.getMatchType());
			map.put("MatchValue", pojo.getMatchValue());
			map.put("MatchIndex", pojo.getMatchIndex());
			map.put("DataChange_LastTime", pojo.getDataChange_LastTime());
	
			return map;
		}
	}
}