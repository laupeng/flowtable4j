package com.ctrip.flowtable4j.core.dao.cardriskdb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ctrip.flowtable4j.core.dao.cardriskdb.entity.InfoSecurity_CheckResultLog;

public class InfoSecurityCheckResultLogGenDao implements baseDAO<InfoSecurity_CheckResultLog> {

	@Resource(name = "cardRiskDBTemplate")
	JdbcTemplate cardRiskDBTemplate;
/*
	@LogID	bigint
	@ReqID	bigint
	@RuleType	char
	@RuleID	int
	@RuleName	varchar
	@RiskLevel	int
	@RuleRemark	varchar
	@CreateDate	datetime
	@DataChange_LastTime	datetime
*/	
	@Override
	public int save(final InfoSecurity_CheckResultLog entity) {
		// TODO Auto-generated method stub

		String param2Value = (String) cardRiskDBTemplate.execute(new CallableStatementCreator() {
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call spA_InfoSecurity_CheckResultLog_i(?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.registerOutParameter(1, java.sql.Types.BIGINT);
				cs.setLong(2, entity.getReqID());
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.execute();
				return cs.getLong(1);// 获取输出参数的值
			}
		});

		return 0;
	}

	@Override
	public int update(InfoSecurity_CheckResultLog entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(InfoSecurity_CheckResultLog entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<InfoSecurity_CheckResultLog> getList(String sql) {
		List resultList = (List) cardRiskDBTemplate.execute(new CallableStatementCreator() {
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call sp_list_table(?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, "p1");// 设置输入参数的值
				cs.registerOutParameter(2, java.sql.Types.VARCHAR);// 注册输出参数的类型
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List resultsMap = new ArrayList();
				cs.execute();
				ResultSet rs = (ResultSet) cs.getObject(2);// 获取游标一行的值
				while (rs.next()) {// 转换每行的返回值到Map中
					Map rowMap = new HashMap();
					rowMap.put("id", rs.getString("id"));
					rowMap.put("name", rs.getString("name"));
					resultsMap.add(rowMap);
				}
				rs.close();
				return resultsMap;
			}
		});

		for (int i = 0; i < resultList.size(); i++) {
			Map rowMap = (Map) resultList.get(i);
			String id = rowMap.get("id").toString();
			String name = rowMap.get("name").toString();
			System.out.println("id=" + id + ";name=" + name);
		}

		return resultList;
	}

	/*
@LogID
@ReqID
@RuleType
@RuleID
@RuleName
@RiskLevel
@RuleRemark
@CreateDate
@DataChange_LastTime 
	 * */
	
}
