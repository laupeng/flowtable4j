package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.PayAdaptResultItem;
import com.ctrip.infosec.flowtable4j.model.SimpleStaticThreadPool;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
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
import java.util.List;

/**
 * Created by thyang on 2015-06-26.
 */
@Component
public class PaybaseDbService {
    protected static JsonMapper mapper = new JsonMapper();
    @Autowired
    @Qualifier("paybaseDbTemplate")
    JdbcTemplate paybaseDbTemplate;

    public void save(final String merchantId, final Long orderId, final int orderType, final String uid, final List<PayAdaptResultItem> results) {
        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                paybaseDbTemplate.execute(
                        new CallableStatementCreator() {
                            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                                String storedProc = "{call spA_PaymentRiskControl_i ( ?,?,?,?,?,?,?,?)}";// 调用的sql
                                CallableStatement cs = con.prepareCall(storedProc);
                                cs.setLong("OrderID", orderId);
                                cs.setString("MerchantID", merchantId);
                                cs.setInt("OrderType", orderType);
                                cs.setString("Uid", uid);
                                cs.setTimestamp("DataChange_LastTime", null);
                                cs.setTimestamp("CreateDate", null);
                                cs.setString("ActionMode", mapper.toJson(results));
                                cs.registerOutParameter("RID", Types.BIGINT);// 注册输出参数的类型



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
