package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.model.CheckResultLog;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
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
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes)
    {
        return  cardRiskDBTemplate.queryForMap(sql,args,argTypes);
    }

    public Map<String, Object> queryForMap(String sql, Object args, int argTypes)
    {
        return  cardRiskDBTemplate.queryForMap(sql,args,argTypes);
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
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
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
}
