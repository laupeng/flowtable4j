package com.ctrip.infosec.flowtable4j.dal.cardriskdb;

import com.ctrip.infosec.flowtable4j.dal.baseDAO;
import com.ctrip.infosec.flowtable4j.dal.cardriskdb.entity.InfoSecurityCheckResultLogGen;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class InfoSecurityCheckResultLogGenDao implements baseDAO<InfoSecurityCheckResultLogGen,Number> {
	
    @Resource(name = "cardRiskDBTemplate")
    JdbcTemplate cardRiskDBTemplate;
	
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
	
	
	/**
	 * Query InfoSecurityCheckResultLogGen by the specified ID
	 * The ID must be a number
	**/
	public InfoSecurityCheckResultLogGen queryByPk(Number id) throws SQLException {
		String sql = ALL_SQL_PATTERN +" where LogID = ?";
		InfoSecurityCheckResultLogGen gen = this.cardRiskDBTemplate.queryForObject(
				sql,
		        new Object[]{id},new InfoSecurityCheckResultLogGenRowMapper());
		return gen;
	}
	
	/**
	 * Get all records in the whole table
	**/
	public List<InfoSecurityCheckResultLogGen> getAll() throws SQLException {
		List<InfoSecurityCheckResultLogGen> gens = cardRiskDBTemplate.query(
				ALL_SQL_PATTERN, new InfoSecurityCheckResultLogGenRowMapper()
		        );		
		
		return gens;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * SP Insert
	**/
	public int insert(InfoSecurityCheckResultLogGen entity) throws SQLException {
		if(null == entity)
			return 0;
		DataSource dataSource = cardRiskDBTemplate.getDataSource();  
		Connection conn=null;  
		Map ddMap=new HashMap();  
		conn=dataSource.getConnection();  
		CallableStatement cs = conn.prepareCall("{call spA_InfoSecurity_CheckResultLog_i (?,?,?,?,?,?,?,?,?)}");   
		cs.registerOutParameter(1,  java.sql.Types.BIGINT );
		cs.setLong(2, entity.getReqID());
		cs.setString(3,entity.getRuleType());
		cs.setInt(4,entity.getRuleID());
		cs.setString(5, entity.getRuleName());
		cs.setInt(6, entity.getRiskLevel());
		cs.setString(7, entity.getRuleRemark());
		cs.setTimestamp(8, entity.getCreateDate());
		cs.setTimestamp(9, entity.getDataChange_LastTime());
		cs.execute();  
		ResultSet rs = cs.getResultSet();
		if(rs.next())
			return rs.getInt(1);
		else
			return -1;
	}


	
	
	public class InfoSecurityCheckResultLogGenRowMapper implements RowMapper<InfoSecurityCheckResultLogGen>
	{
        public InfoSecurityCheckResultLogGen mapRow(ResultSet rs, int rowNum) throws SQLException {
        	InfoSecurityCheckResultLogGen gen = new InfoSecurityCheckResultLogGen();
        	gen.setLogID(rs.getLong("LogID"));
        	gen.setReqID(rs.getLong("ReqID"));
        	gen.setRuleType(rs.getString("RuleType"));
        	gen.setRuleID(rs.getInt("RuleID"));
        	gen.setRuleName(rs.getString("RuleName"));
        	gen.setRiskLevel(rs.getInt("RiskLevel"));
        	gen.setRuleRemark(rs.getString("RuleRemark"));
        	gen.setCreateDate(rs.getTimestamp("CreateDate"));
        	gen.setDataChange_LastTime(rs.getTimestamp("DataChange_LastTime"));
            return gen;
        }
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


	@Override
	public int update(InfoSecurityCheckResultLogGen entity) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(InfoSecurityCheckResultLogGen entity) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
}