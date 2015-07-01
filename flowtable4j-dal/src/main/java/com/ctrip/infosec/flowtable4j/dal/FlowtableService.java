package com.ctrip.infosec.flowtable4j.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

/**
 * Created by thyang on 2015-06-10.
 */
@Component
public class FlowtableService {
    @Autowired
    @Qualifier("flowDbTemplate")
    JdbcTemplate flowDbTemplate ;
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes)
    {
        return  flowDbTemplate.queryForMap(sql,args,argTypes);
    }

    public void saveFlowTable(final long reqid,final String tableName, final String keyField1, final Object KeyField1Value , final String keyField2, final Object keyField2Value){
        flowDbTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                String storedProc = "{call spA_%s_i (?,?,?,?,?)}";
                storedProc = String.format(storedProc, tableName);
                CallableStatement callableStatement = connection.prepareCall(storedProc);
                callableStatement.setObject("reqid",reqid);
                callableStatement.setObject(keyField1,KeyField1Value);
                callableStatement.setObject(keyField2, keyField2Value);
                callableStatement.setObject("createdate",null);
                callableStatement.setObject("datachange_lasttime",null);
//                callableStatement.registerOutParameter("retcode", Types.BIGINT);

                return callableStatement;
            }
        }, new CallableStatementCallback<Long>() {
            @Override
            public Long doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                callableStatement.execute();
                return Long.valueOf(0);
            }
        });
    }
}
