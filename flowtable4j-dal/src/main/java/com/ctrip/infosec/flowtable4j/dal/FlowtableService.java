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
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by thyang on 2015-06-10.
 */
@Component
public class FlowtableService {

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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
                StringBuilder storedProc = new StringBuilder("{call spA_%s_i (");
                storedProc.append("@reqid=?,");
                storedProc.append("@").append(keyField1).append("=?,");
                storedProc.append("@").append(keyField2).append("=?,");
                storedProc.append("@createdate=?)}");
                CallableStatement callableStatement = connection.prepareCall(String.format(storedProc.toString(),tableName));
                callableStatement.setObject(1,reqid);
                callableStatement.setObject(2,KeyField1Value);
                callableStatement.setObject(3,keyField2Value);
                callableStatement.setObject(4,sdf.format(System.currentTimeMillis()));
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
