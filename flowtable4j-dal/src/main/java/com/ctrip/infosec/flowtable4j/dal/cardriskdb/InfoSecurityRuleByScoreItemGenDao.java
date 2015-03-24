package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleByScoreItemGen;
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

public class InfoSecurityRuleByScoreItemGenDao {
	private static final String DATA_BASE = "CardRiskDB_INSERT_1";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from InfoSecurity_RuleByScoreItem WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM InfoSecurity_RuleByScoreItem WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by ItemId desc ) as rownum" 
			+" from InfoSecurity_RuleByScoreItem (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_InfoSecurity_RuleByScoreItem_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_InfoSecurity_RuleByScoreItem_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_InfoSecurity_RuleByScoreItem_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_InfoSecurity_RuleByScoreItem_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_InfoSecurity_RuleByScoreItem_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_InfoSecurity_RuleByScoreItem_u";
	private static final String RET_CODE = "retcode";
	
	private DalParser<InfoSecurityRuleByScoreItemGen> parser = new InfoSecurityRuleByScoreItemGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<InfoSecurityRuleByScoreItemGen> rowextractor = null;
	private DalTableDao<InfoSecurityRuleByScoreItemGen> client;
	private DalClient baseClient;
	
	public InfoSecurityRuleByScoreItemGenDao() {
		this.client = new DalTableDao<InfoSecurityRuleByScoreItemGen>(parser);
		dbCategory = this.client.getDatabaseCategory();

		this.rowextractor = new DalRowMapperExtractor<InfoSecurityRuleByScoreItemGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query InfoSecurityRuleByScoreItemGen by the specified ID
	 * The ID must be a number
	**/
	public InfoSecurityRuleByScoreItemGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<InfoSecurityRuleByScoreItemGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<InfoSecurityRuleByScoreItemGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}

	public static class InfoSecurityRuleByScoreItemGenParser extends AbstractDalParser<InfoSecurityRuleByScoreItemGen> {
		public static final String DATABASE_NAME = "CardRiskDB_INSERT_1";
		public static final String TABLE_NAME = "InfoSecurity_RuleByScoreItem";
		private static final String[] COLUMNS = new String[]{
			"ItemId",
			"ScoreId",
			"MatchType",
			"MatchValue",
			"DataChange_LastTime",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ItemId",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.BIGINT,
			Types.BIGINT,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.TIMESTAMP,
		};
		
		public InfoSecurityRuleByScoreItemGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public InfoSecurityRuleByScoreItemGen map(ResultSet rs, int rowNum) throws SQLException {
			InfoSecurityRuleByScoreItemGen pojo = new InfoSecurityRuleByScoreItemGen();
			
			pojo.setItemId((Long)rs.getObject("ItemId"));
			pojo.setScoreId((Long)rs.getObject("ScoreId"));
			pojo.setMatchType((String)rs.getObject("MatchType"));
			pojo.setMatchValue((String)rs.getObject("MatchValue"));
			pojo.setDataChange_LastTime((Timestamp)rs.getObject("DataChange_LastTime"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(InfoSecurityRuleByScoreItemGen pojo) {
			return pojo.getItemId();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(InfoSecurityRuleByScoreItemGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ItemId", pojo.getItemId());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(InfoSecurityRuleByScoreItemGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ItemId", pojo.getItemId());
			map.put("ScoreId", pojo.getScoreId());
			map.put("MatchType", pojo.getMatchType());
			map.put("MatchValue", pojo.getMatchValue());
			map.put("DataChange_LastTime", pojo.getDataChange_LastTime());
	
			return map;
		}
	}
}