package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * CheckEnitity转换为PO Entity
 * 放入工具类方法
 * Created by thyang on 2015-06-10.
 */
@Component
public class CheckRiskDAO {

    private static Logger logger = LoggerFactory.getLogger(CheckRiskDAO.class);

    protected static JsonMapper mapper = new JsonMapper();

    private static Map<String, String> originalRiskLevel = new HashMap<String, String>();

    @Autowired
    CardRiskService cardRiskDb;

    @Autowired
    FlowtableService flowDb;

    @Autowired
    CUSDbService cusDb;

    @Autowired
    ESBClient esbClient;

    @Autowired
    Counter counter;

    public String getTableName(String keyField, Integer orderType) {
        return originalRiskLevel.get(String.format("%s|%s", keyField, orderType).toUpperCase());
    }

    public String getOriginRiskLevelCount(Map<String, String> kv, Integer orderType) {
        Integer[] orderTypes = new Integer[]{0, orderType};
        for (String k : kv.keySet()) {
            String v = kv.get(k);
            if (!Strings.isNullOrEmpty(v)) {
                for (Integer orderTy : orderTypes) {
                    String tableName = getTableName(k, orderType);
                    String field = k.toLowerCase();
                    if (!Strings.isNullOrEmpty(tableName)) {
                        String sql = String.format("SELECT DISTINCT originalrisklevel FROM %s WITH(NOLOCK) WHERE %s=:%s AND originalrisklevel >=195 and CreateDate>=:starttimelimit and CreateDate<=:timelimit", tableName.toUpperCase(), field, field);
                        String count = counter.getCounter("COUNT", sql, field, -720, 0, "", v);
                        if (!count.equals("0")) {
                            return "1";
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * 获取七天内均值消费金额
     *
     * @param CCardNoCode    char
     * @param startTimeLimit char yyyy-MM-dd hh:mm:ss
     * @param timeLimit      char yyyy-MM-dd hh:mm:ss
     * @return
     */
    public Double getAvgAmount7(String CCardNoCode, String startTimeLimit, String timeLimit) {
        try {
            String sql = "SELECT avg(Amount) AS amt FROM CTRIP_FLT_CCardNoCode_Amount WITH(NOLOCK) " +
                    "WHERE CCardNoCode=? and CreateDate>=? and CreateDate<=?";
            Map avgAmount = flowDb.queryForMap(sql, new Object[]{CCardNoCode, startTimeLimit, timeLimit}, new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP});
            if (avgAmount != null) {
                return Double.parseDouble(avgAmount.get("amt").toString());
            }
        } catch (Exception exp) {
            logger.warn("查询CTRIP_FLT_CCardNoCode_Amount信息异常", exp);
        }
        return 0d;
    }

    /**
     * 根据CityId获取城市名称与省
     *
     * @param city
     * @return
     */
    public Map<String, Object> getCityNameProvince(String city) {
        try {
            String sql = "SELECT TOP 1 cityname,province,country,provincename,hotelprovince FROM BaseData_City WITH(NOLOCK) WHERE City=? ";
            return cardRiskDb.queryForMap(sql, new Object[]{city}, new int[]{Types.BIGINT});
        } catch (Exception exp) {
            logger.warn("查询ip对应的城市信息异常:", exp);
        }
        return null;
    }

    /**
     * 获取省份证归属省信息  BaseData_IDCardInfo
     *
     * @param iDCardNumber
     * @return
     */
    public Map<String, Object> getIDCardProvince(String iDCardNumber) {
        try {
            String sql = "SELECT TOP 1 provincename,cityname FROM BaseData_IDCardInfo WITH(NOLOCK) WHERE IDCardNumber=? ";
            return cardRiskDb.queryForMap(sql, new Object[]{iDCardNumber}, new int[]{Types.VARCHAR});
        } catch (Exception exp) {
            logger.warn("获取省份证归属省信息:", exp);
        }
        return null;
    }

    /**
     * @param uid            char
     * @param startTimeLimit char yyyy-MM-dd HH:mm:ss
     * @param timeLimit      char yyyy-MM-dd HH:mm:ss
     * @return
     */
    public String getUidOrderDate(String uid, String startTimeLimit, String timeLimit) {
        try {
            String sql = "SELECT TOP 1 orderdate FROM CTRIP_ALL_UID_OrderDate WITH (NOLOCK) WHERE " +
                    "Uid = ? and CreateDate>=? and CreateDate<=?";
            Map orderDate = flowDb.queryForMap(sql, new Object[]{uid, startTimeLimit, timeLimit}, new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP});
            if (orderDate != null) {
                return orderDate.get("orderdate").toString();
            }
        } catch (Exception exp) {
            logger.warn("查询CTRIP_ALL_UID_OrderDate异常", exp);
        }
        return null;
    }

    /**
     * 通过手机号查询对应的城和市
     * CityName,ProvinceName
     *
     * @param mobilePhone char
     * @return 返回手机号对应的城市信息
     */
    public Map<String, Object> getMobileCityAndProv(String mobilePhone) {
        try {
            if (!Strings.isNullOrEmpty(mobilePhone) && mobilePhone.length() > 6) {
                mobilePhone = mobilePhone.substring(0, 7);
                String sql = "SELECT TOP 1 provincename,cityname  FROM BaseData_MobilePhoneInfo WITH(NOLOCK) WHERE MobileNumber=? ";
                return cardRiskDb.queryForMap(sql, new Object[]{mobilePhone}, new int[]{Types.BIGINT});
            }
        } catch (Exception exp) {
            logger.warn("从sql查询手机号对应的城市信息异常", exp);
        }
        return null;
    }

    /**
     * 获取IP地址对应信息
     *
     * @param ipValue long
     * @return
     */
    public Map<String, Object> getIpCountryCity(long ipValue) {
        try {
            String sql = "SELECT TOP 1 countrycode,countryname,city,cityid,continent,continentid,citynamech,countrynamech  FROM IpCountryCity WITH(NOLOCK) WHERE IpStart <= ? ORDER BY IpStart DESC ";
            return cardRiskDb.queryForMap(sql, new Object[]{ipValue}, new int[]{Types.BIGINT});
        } catch (Exception exp) {
            logger.warn("查询ip对应的城市信息异常:", exp);
        }
        return null;
    }

    /**
     * 获取DID相关信息
     *
     * @param orderId char
     * @param payId   char
     * @return
     */
    public Map<String, Object> getDIDInfo(String orderId, String payId) {
        try {
            String sql = "SELECT TOP 1 did  FROM CacheData_DeviceIDInfo WITH(NOLOCK) WHERE Oid = ? AND Payid = ? ORDER BY RecordID desc";
            return flowDb.queryForMap(sql, new Object[]{orderId, payId}, new int[]{Types.VARCHAR, Types.VARCHAR});
        } catch (Exception exp) {
            logger.warn("查询DID信息异常", exp);
        }
        return null;
    }

    /**
     * 获取 CreditCardRule_ForeignCard
     *
     * @param cardTypeId int
     * @param cardBin    char
     * @return
     */
    public Map<String, Object> getForeignCardInfo(String cardTypeId, String cardBin) {
        try {
            String sql = "SELECT city,bankofcardissue,nationality,cardname FROM CreditCardRule_ForeignCard WITH(NOLOCK) WHERE CardTypeID = ? and CardRule = ?";
            return cardRiskDb.queryForMap(sql, new Object[]{cardTypeId, cardBin}, new int[]{Types.INTEGER, Types.VARCHAR});
        } catch (Exception exp) {
            logger.warn("getForeignCardInfo异常", exp);
        }
        return null;
    }

    /**
     * 获取CardRisk_Leaked_Uid
     *
     * @param uid char
     * @return
     */
    public Map<String, Object> getLeakedInfo(String uid) {
        try {
            String sql = "SELECT active FROM CardRisk_Leaked_Uid WITH(NOLOCK) WHERE Uid =?";
            return cusDb.queryForMap(sql, new Object[]{uid}, new int[]{Types.VARCHAR});
        } catch (Exception exp) {
            logger.warn("获取CardRisk_Leaked_Uid异常", exp);
        }
        return null;
    }

    /**
     * 获取 BaseData_CardBankInfo
     *
     * @param creditCardType int
     * @param branchNo       str
     * @return
     */
    public Map<String, Object> getCardBankInfo(String creditCardType, String branchNo) {
        try {
            String sql = "SELECT TOP 1 branchcity,branchprovince  FROM BaseData_CardBankInfo WITH(NOLOCK) WHERE CreditCardType =? and BranchNo = ?";
            return cardRiskDb.queryForMap(sql, new Object[]{creditCardType, branchNo}, new int[]{Types.INTEGER, Types.VARCHAR});
        } catch (Exception exp) {
            logger.warn("获取BaseData_CardBankInfo查询异常", exp);
        }
        return null;
    }

    /**
     * 根据国家编号获取国家的名称和国际
     *
     * @param country Int
     * @return
     */
    public Map<String, Object> getCountryNameNationality(String country) {
        try {
            String sql = "SELECT TOP 1 nationality,countryname  FROM BaseData_CountryInfo WITH(NOLOCK) WHERE Country =? ";
            return cardRiskDb.queryForMap(sql, new Object[]{country}, new int[]{Types.BIGINT});
        } catch (Exception exp) {
            logger.warn("根据国家编号获取国家的名称和国际异常", exp);
        }
        return null;
    }

    public Map<String, Object> getLastProductInfo(String orderId, String orderType, String merchantId) {
        try {
            long id = Long.parseLong(orderId) % 4;
            String sql = String.format("SELECT content FROM InfoSecurity_LastProductInfo%s WITH(NOLOCK) WHERE pid=?", id);
            return cardRiskDb.queryForMap(sql, String.format("%s|%s|%s", orderId, orderType, merchantId), Types.VARCHAR);
        } catch (Exception ex) {
            logger.warn(String.format("获取历史产品数据失败:%s", orderId), ex);
        }
        return null;
    }

    public Map<String, Object> getLastPaymentInfo(String orderId, String orderType, String merchantId) {
        try {
            long id = Long.parseLong(orderId) % 4;
            String sql = String.format("SELECT content,risklevel,prepaytype FROM InfoSecurity_LastPaymentInfo%s WITH(NOLOCK) WHERE pid=?", id);
            return cardRiskDb.queryForMap(sql, String.format("%s|%s", orderId, orderType), Types.VARCHAR);
        } catch (Exception ex) {
            logger.warn(String.format("获取历史支付数据失败:%s", orderId), ex);
        }
        return null;
    }

    public void saveLastProductInfo(String orderId, String orderType, String merchantId, Map<String, Object> productInfo) {
        try {
            long id = Long.parseLong(orderId) % 4;
            String sql = String.format(
                    "IF EXISTS (SELECT 'X' FROM InfoSecurity_LastProductInfo%s WITH(NOLOCK) WHERE PID=:p1)\n" +
                            "   EXEC spA_InfoSecurity_LastProductInfo%s_u @PID=:p1,@Content=:p2\n" +
                            "ELSE\n" +
                            "   EXEC spA_InfoSecurity_LastProductInfo%s_i @PID=:p1,@Content=:p2", id);

            MapSqlParameterSource params = new MapSqlParameterSource();
            SqlParameterValue value0 = new SqlParameterValue(Types.VARCHAR, String.format("%s|%s", orderId, orderType));
            SqlParameterValue value1 = new SqlParameterValue(Types.VARCHAR, mapper.toJson(productInfo));
            params.addValue("p1", value0);
            params.addValue("p2", value1);

            cardRiskDb.cardDbNamedTemplate.execute(sql, params, new PreparedStatementCallback<Object>() {
                @Override
                public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    ps.execute();
                    return null;
                }
            });

        } catch (Exception ex) {
            logger.warn(String.format("保存历史产品数据失败:%s", orderId), ex);
        }
    }

    public void saveLastPaymentInfo(String orderId, String orderType, String merchantId, String prePayType, Map<String, Object> paymentInfo) {
        try {
            long id = Long.parseLong(orderId) % 4;
            String sql = String.format(
                    "IF EXISTS (SELECT 'X' FROM InfoSecurity_LastPaymentInfo%s WITH(NOLOCK) WHERE PID=:p1)\n" +
                            "   EXEC spA_InfoSecurity_LastPaymentInfo%s_u @PID=:p1,@Content=:p2,PrepayType=:p3\n" +
                            "ELSE\n" +
                            "   EXEC spA_InfoSecurity_LastPaymentInfo%s_i @PID=:p1,@Content=:p2,PrepayType=:p3", id);

            MapSqlParameterSource params = new MapSqlParameterSource();
            SqlParameterValue value0 = new SqlParameterValue(Types.VARCHAR, String.format("%s|%s", orderId, orderType));
            SqlParameterValue value1 = new SqlParameterValue(Types.VARCHAR, mapper.toJson(paymentInfo));
            SqlParameterValue value2 = new SqlParameterValue(Types.VARCHAR, prePayType);
            params.addValue("p1", value0);
            params.addValue("p2", value1);
            params.addValue("p3", value2);

            cardRiskDb.cardDbNamedTemplate.execute(sql, params, new PreparedStatementCallback<Object>() {
                @Override
                public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    ps.execute();
                    return null;
                }
            });
        } catch (Exception ex) {
            logger.warn(String.format("保存历史支付数据失败:%s", orderId), ex);
        }
    }

}
