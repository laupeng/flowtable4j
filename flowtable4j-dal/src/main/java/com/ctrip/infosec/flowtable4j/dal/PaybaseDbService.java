package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.MapX;
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
import java.util.Map;

/**
 * Created by thyang on 2015-06-26.
 */
@Component
public class PaybaseDbService {
    protected static JsonMapper mapper = new JsonMapper();
    @Autowired
    @Qualifier("paybaseDbTemplate")
    public JdbcTemplate jdbcTemplate;

    public int getCityCodeByAirPort(String airPort) {
        String sql = "SELECT City FROM AirPort(nolock) WHERE AirPort=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, new Object[]{airPort}, new int[]{Types.VARCHAR});
        if (map != null && map.size() > 0) {
            return Integer.parseInt(MapX.getString(map, "City"));
        }
        return 0;
    }

    public void save(final String merchantId, final Long orderId, final int orderType, final String uid, final List<PayAdaptResultItem> results) {
        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                jdbcTemplate.execute(
                        new CallableStatementCreator() {
                            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                                String storedProc = "{call spA_PaymentRiskControl_i (@OrderID=?,@MerchantID=?,@OrderType=?,@Uid=?,@ActionMode=?,@RID=?)}";// 调用的sql
                                CallableStatement cs = con.prepareCall(storedProc);
                                cs.setLong(1,orderId);
                                cs.setString(2, merchantId);
                                cs.setInt(3,orderType);
                                cs.setString(4, uid);
                                cs.setString(5, mapper.toJson(results));
                                cs.registerOutParameter(6, Types.BIGINT);// 注册输出参数的类型
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
