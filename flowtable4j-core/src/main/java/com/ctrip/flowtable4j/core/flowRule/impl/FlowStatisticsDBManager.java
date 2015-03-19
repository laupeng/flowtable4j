package com.ctrip.flowtable4j.core.flowRule.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.ctrip.flowtable4j.core.flowRule.RuleManager;

/**
 * 流量统计执行sql
 * 
 * @author weiyu
 * @date 2015年3月18日
 */
public class FlowStatisticsDBManager {

	
	
	/**
	 * 替换sql语句中的命名参数 (select * from table where name=@name and age=@age 转换后为
	 * select * from table where name=? and age =? )
	 * 
	 * @param sql
	 * @return
	 */
	private String parseSql(String sql,Map paramsMap) {
		
		String regex = "(:(//w+))";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(sql);
		paramsMap.clear();
		int idx = 1;
		while (m.find()) {
			// 参数名称可能有重复，使用序号来做Key
			paramsMap.put(new Integer(idx++), m.group(2));
		}
		String result = sql.replaceAll(regex, "?");
		return result;
	}

	/**
	 * 填充PreparedStatement参数
	 * 
	 * @param ps
	 * @param param
	 * @return
	 */
	private boolean fillParameters(PreparedStatement ps,Map paramsMap, Map sqlParam) {
		
		boolean result = true;
		String paramName = null;
		Object paramValue = null;
		int idx = 1;
		for (Iterator itr = paramsMap.entrySet().iterator(); itr.hasNext();) {
			Entry entry = (Entry) itr.next();
			paramName = (String) entry.getValue();
			idx = ((Integer) entry.getKey()).intValue();
			// 不包含会返回null
			paramValue = sqlParam.get(paramName);
			try {
				ps.setObject(idx, paramValue);
			} catch (Exception e) {
				result = false;
			}
		}
		return result;
	}
	
	public String execSql(long startTimeLimit,long timeLimit,String paramName,String paramValue,String basicSql
			,String matchColumnName,String matchValue,RuleManager.FlowStatisticType flowStatisticType){
		String sql ;

		if(flowStatisticType== RuleManager.FlowStatisticType.COUNT){
			basicSql = "select " + matchColumnName + " from (" + basicSql + ") t1 " 
						+ "WHERE "+ matchColumnName +" is NOT NULL GROUP BY "+matchColumnName; 
		}
		else if (flowStatisticType == RuleManager.FlowStatisticType.SUM){
			basicSql = "select sum(" + matchColumnName + ") from (" + basicSql + ") t1 "; 
		}
		
		Map paramsMap = new HashMap();
		sql = parseSql(basicSql,paramsMap);
		Connection conn = null;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			Map sqlParam = new HashMap();
			sqlParam.put("StartTimeLimit", new java.sql.Date(startTimeLimit));
			sqlParam.put("TimeLimit", new java.sql.Date(timeLimit));
			sqlParam.put(paramName, paramValue);
			
			if(fillParameters(ps,paramsMap,sqlParam))
			{
				ResultSet rs = ps.executeQuery();

				if(flowStatisticType== RuleManager.FlowStatisticType.COUNT){
					 int currentValue=0;
					if(org.apache.commons.lang.StringUtils.isNotEmpty(matchValue)){
						currentValue=1;
					}
					else 
						matchValue = "";
					while(rs.next()){
						String dbValue = rs.getString(0);
						if(!matchValue.equalsIgnoreCase(dbValue)){
							currentValue++;
						}
					}
					rs.close();
					return "" + currentValue;
				}
				else if (flowStatisticType == RuleManager.FlowStatisticType.SUM){
					String v = "0";
					if(rs.next()){
						v = rs.getString(0);
					}
					return v;
				}
			}
			else
				return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(conn != null && !conn.isClosed())
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	

}
