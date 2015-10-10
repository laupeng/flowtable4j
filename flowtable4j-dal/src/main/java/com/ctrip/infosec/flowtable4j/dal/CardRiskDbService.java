package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.CheckResultLog;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-10.
 */
@Component
public class CardRiskDbService {

    @Autowired
    @Qualifier("cardriskDbTemplate")
    public JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("cardriskDbNamedTemplate")
    public NamedParameterJdbcTemplate namedJdbcTemplate;

    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) {
        return jdbcTemplate.queryForMap(sql, args, argTypes);
    }

    public Map<String, Object> queryForMap(String sql, Object args, int argTypes) {
        return jdbcTemplate.queryForMap(sql, new Object[]{args}, new int[]{argTypes});
    }

    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) {
        return jdbcTemplate.queryForList(sql, args, argTypes);
    }

    public List<Map<String, Object>> queryForList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> queryForList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args);
    }

    private String getRuleType(CheckResultLog item) {
        String ruleType = item.getRuleType();
        if (CheckType.BW.toString().equals(ruleType)) {
            if (item.getRiskLevel().equals(0)) {
                ruleType = "W";
            } else {
                ruleType = "B";
            }

        } else if (CheckType.FLOWRULE.toString().equals(ruleType)) {
            ruleType = "D";
        }
        return ruleType;
    }

    public void saveThirdAFSResult(final Map<String, Object> values) {
        try {
            jdbcTemplate.<Long>execute(
                    new CallableStatementCreator() {
                        @Override
                        public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                            String storedProc = "{call  spA_T3AFS_Result_i{@ReqID=?,@OrderID=? ,@OrderType=?,@ServiceType=?,@ServiceStatus=?,@ServiceError=?,@Decision=?,@DecisionMessage=?,@AFSResult=?,@ReasonCode=?,@AdditionalMessage=?,@ResponseStr=?,@EventID=?}";
                            CallableStatement stmt = connection.prepareCall(storedProc);
                            int i = 0;
                            stmt.registerOutParameter(i++, Types.BIGINT);
                            stmt.setObject(i++, values.get("orderid"));
                            stmt.setObject(i++, values.get("ordertype"));
                            stmt.setObject(i++, values.get("servicetype"));
                            stmt.setObject(i++, values.get("servicestatus"));
                            stmt.setObject(i++, values.get("serviceerror"));
                            stmt.setObject(i++, values.get("decision"));
                            stmt.setObject(i++, values.get("decisionmessage"));
                            stmt.setObject(i++, values.get("afsresult"));
                            stmt.setObject(i++, values.get("reasoncode"));
                            stmt.setObject(i++, values.get("additionalmessage"));
                            stmt.setObject(i++, values.get("responsestr"));
                            stmt.setObject(i++, values.get("eventid"));
                            return stmt;
                        }
                    }, new CallableStatementCallback() {
                        @Override
                        public Void doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                            callableStatement.execute();
                            return null;
                        }
                    });
        } catch (Exception ex) {
            //
        }

    }
}
