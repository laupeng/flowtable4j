package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.model.CheckResultLog;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.ctrip.infosec.flowtable4j.model.persist.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Created by thyang on 2015-06-10.
 */
@Component
public class CardRiskService {

    @Autowired
    @Qualifier("cardRiskDBTemplate")
    JdbcTemplate cardRiskDBTemplate;

    @Autowired
    @Qualifier("cardDbNamedTemplate")
    public NamedParameterJdbcTemplate cardDbNamedTemplate;

    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes)
    {
        return  cardRiskDBTemplate.queryForMap(sql,args,argTypes);
    }

    public Map<String, Object> queryForMap(String sql, Object args, int argTypes)
    {
        return  cardRiskDBTemplate.queryForMap(sql,new Object[]{args},new int[]{argTypes});
    }

    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes)
    {
        return  cardRiskDBTemplate.queryForList(sql, args, argTypes);
    }

    public List<Map<String, Object>> queryForList(String sql)
    {
        return  cardRiskDBTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> queryForList(String sql, Object[] args)
    {
        return  cardRiskDBTemplate.queryForList(sql, args);
    }

    public void saveCheckResultLog(RiskResult result) {
        final long reqId = result.getReqId();

        for (final CheckResultLog item : result.getResults()) {
            SimpleStaticThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    cardRiskDBTemplate.execute(
                            new CallableStatementCreator() {
                                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                                    String storedProc = "{call spA_InfoSecurity_CheckResult4j_i ( ?,?,?,?,?,?,?,?)}";// 调用的sql
                                    CallableStatement cs = con.prepareCall(storedProc);
                                    cs.setLong(2, reqId);
                                    cs.setString(3, item.getRuleType());
                                    cs.setInt(4, item.getRuleID());
                                    cs.setString(5, Objects.toString(item.getRuleName(), ""));
                                    cs.setInt(6, item.getRiskLevel());
                                    cs.setString(7, Objects.toString(item.getRuleRemark(), ""));
                                    cs.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                                    cs.registerOutParameter(1, Types.BIGINT);// 注册输出参数的类型
                                    return cs;
                                }
                            }, new CallableStatementCallback() {
                                public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                                    cs.execute();
                                    return null;
                                }
                            });
                }
            });
        }
    }

    //通用的save方法
    public long saveImpl(final Map<String, Object> toSave, final List<TableInfo> tableInfo, final String tableName, final long reqId) {
        final String[] identityName = {""};
        if (toSave != null && tableInfo != null) {
            return cardRiskDBTemplate.<Long>execute(new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                    String storedProc = "{call spA_%s_i (%s)}";
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < tableInfo.size(); i++) {
                        if (i == tableInfo.size() - 1)
                            sb.append("?");
                        else
                            sb.append("?,");
                    }
                    storedProc = String.format(storedProc, tableName, sb.toString());
                    CallableStatement callableStatement = connection.prepareCall(storedProc);
                    for (TableInfo t : tableInfo) {
                        if (t.getIs_identity() == 1) {
                            identityName[0] = t.getName();
                            callableStatement.registerOutParameter(t.getName(), Types.BIGINT);
                        } else {
                            Object value = null;
                            if("reqid".equals(t.getName()))
                                value = reqId;
                            else
                                value = t.getValue(toSave);
                            callableStatement.setObject(t.getName(),value);
                        }
                    }
                    return callableStatement;
                }
            }, new CallableStatementCallback<Long>() {
                @Override
                public Long doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                    callableStatement.execute();
                    if(identityName[0].length()>0)
                        return callableStatement.getLong(identityName[0]);
                    return Long.valueOf(0);
                }
            });
        }
        return 0;
    }
}
