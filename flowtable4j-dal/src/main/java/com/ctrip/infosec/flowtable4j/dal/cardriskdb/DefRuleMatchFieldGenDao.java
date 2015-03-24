package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.DefRuleMatchFieldGen;
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

public class DefRuleMatchFieldGenDao {
	private static final String DATA_BASE = "CardRiskDB_INSERT_1";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from Def_RuleMatchField WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM Def_RuleMatchField WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by FieldID desc ) as rownum" 
			+" from Def_RuleMatchField (nolock)) select * from CTE where rownum between %s and %s";

	private static final String BASIC_INSERT_SP_NAME = "spA_Def_RuleMatchField_i";
	private static final String BATCH_INSERT_SP_NAME = "sp3_Def_RuleMatchField_i";
	private static final String BASIC_DELETE_SP_NAME = "spA_Def_RuleMatchField_d";
	private static final String BATCH_DELETE_SP_NAME = "sp3_Def_RuleMatchField_d";
	private static final String BASIC_UPDATE_SP_NAME = "spA_Def_RuleMatchField_u";
	private static final String BATCH_UPDATE_SP_NAME = "sp3_Def_RuleMatchField_u";
	private static final String RET_CODE = "retcode";
	
	private DalParser<DefRuleMatchFieldGen> parser = new DefRuleMatchFieldGenParser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<DefRuleMatchFieldGen> rowextractor = null;
	private DalTableDao<DefRuleMatchFieldGen> client;
	private DalClient baseClient;
	
	public DefRuleMatchFieldGenDao() {
		this.client = new DalTableDao<DefRuleMatchFieldGen>(parser);
		dbCategory = this.client.getDatabaseCategory();

		this.rowextractor = new DalRowMapperExtractor<DefRuleMatchFieldGen>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query DefRuleMatchFieldGen by the specified ID
	 * The ID must be a number
	**/
	public DefRuleMatchFieldGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<DefRuleMatchFieldGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<DefRuleMatchFieldGen> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}

	public static class DefRuleMatchFieldGenParser extends AbstractDalParser<DefRuleMatchFieldGen> {
		public static final String DATABASE_NAME = "CardRiskDB_INSERT_1";
		public static final String TABLE_NAME = "Def_RuleMatchField";
		private static final String[] COLUMNS = new String[]{
			"FieldID",
			"ColumnName",
			"TableName",
			"Active",
			"DataChange_LastTime",
			"Remark",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"FieldID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.CHAR,
			Types.TIMESTAMP,
			Types.VARCHAR,
		};
		
		public DefRuleMatchFieldGenParser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public DefRuleMatchFieldGen map(ResultSet rs, int rowNum) throws SQLException {
			DefRuleMatchFieldGen pojo = new DefRuleMatchFieldGen();
			
			pojo.setFieldID((Integer)rs.getObject("FieldID"));
			pojo.setColumnName((String)rs.getObject("ColumnName"));
			pojo.setTableName((String)rs.getObject("TableName"));
			pojo.setActive((String)rs.getObject("Active"));
			pojo.setDataChange_LastTime((Timestamp)rs.getObject("DataChange_LastTime"));
			pojo.setRemark((String)rs.getObject("Remark"));
	
			return pojo;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(DefRuleMatchFieldGen pojo) {
			return pojo.getFieldID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(DefRuleMatchFieldGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("FieldID", pojo.getFieldID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(DefRuleMatchFieldGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("FieldID", pojo.getFieldID());
			map.put("ColumnName", pojo.getColumnName());
			map.put("TableName", pojo.getTableName());
			map.put("Active", pojo.getActive());
			map.put("DataChange_LastTime", pojo.getDataChange_LastTime());
			map.put("Remark", pojo.getRemark());
	
			return map;
		}
	}
}