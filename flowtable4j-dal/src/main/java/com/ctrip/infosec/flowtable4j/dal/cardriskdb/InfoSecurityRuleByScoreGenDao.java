package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityRuleByScoreGen;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InfoSecurityRuleByScoreGenDao {
	private static final String DATA_BASE = "CardRiskDB_INSERT_1";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from InfoSecurity_RuleByScore WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM InfoSecurity_RuleByScore WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by ScoreId desc ) as rownum" 
			+" from InfoSecurity_RuleByScore (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_InfoSecurity_RuleByScore_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_InfoSecurity_RuleByScore_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_InfoSecurity_RuleByScore_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_InfoSecurity_RuleByScore_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_InfoSecurity_RuleByScore_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_InfoSecurity_RuleByScore_u";
	private static final String RET_CODE = "retcode";
	
	private DalParser<InfoSecurityRuleByScoreGen> parser = new InfoSecurityRuleByScoreGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<InfoSecurityRuleByScoreGen> rowextractor = null;
	private DalTableDao<InfoSecurityRuleByScoreGen> client;
	private DalClient baseClient;
	
	public InfoSecurityRuleByScoreGenDao() {
		this.client = new DalTableDao<InfoSecurityRuleByScoreGen>(parser);
		dbCategory = this.client.getDatabaseCategory();

		this.rowextractor = new DalRowMapperExtractor<InfoSecurityRuleByScoreGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query InfoSecurityRuleByScoreGen by the specified ID
	 * The ID must be a number
	**/
	public InfoSecurityRuleByScoreGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<InfoSecurityRuleByScoreGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<InfoSecurityRuleByScoreGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}

	public static class InfoSecurityRuleByScoreGenParser extends AbstractDalParser<InfoSecurityRuleByScoreGen> {
		public static final String DATABASE_NAME = "CardRiskDB_INSERT_1";
		public static final String TABLE_NAME = "InfoSecurity_RuleByScore";
		private static final String[] COLUMNS = new String[]{
			"ScoreId",
			"KeyId",
			"KeyType",
			"Score",
			"DataChange_LastTime",
			"ElseScore",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ScoreId",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.BIGINT,
			Types.INTEGER,
			Types.VARCHAR,
			Types.DECIMAL,
			Types.TIMESTAMP,
			Types.DECIMAL,
		};
		
		public InfoSecurityRuleByScoreGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public InfoSecurityRuleByScoreGen map(ResultSet rs, int rowNum) throws SQLException {
			InfoSecurityRuleByScoreGen pojo = new InfoSecurityRuleByScoreGen();
			
			pojo.setScoreId((Long)rs.getObject("ScoreId"));
			pojo.setKeyId((Integer)rs.getObject("KeyId"));
			pojo.setKeyType((String)rs.getObject("KeyType"));
			pojo.setScore((BigDecimal)rs.getObject("Score"));
			pojo.setDataChange_LastTime((Timestamp)rs.getObject("DataChange_LastTime"));
			pojo.setElseScore((BigDecimal)rs.getObject("ElseScore"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(InfoSecurityRuleByScoreGen pojo) {
			return pojo.getScoreId();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(InfoSecurityRuleByScoreGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ScoreId", pojo.getScoreId());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(InfoSecurityRuleByScoreGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ScoreId", pojo.getScoreId());
			map.put("KeyId", pojo.getKeyId());
			map.put("KeyType", pojo.getKeyType());
			map.put("Score", pojo.getScore());
			map.put("DataChange_LastTime", pojo.getDataChange_LastTime());
			map.put("ElseScore", pojo.getElseScore());
	
			return map;
		}
	}
}