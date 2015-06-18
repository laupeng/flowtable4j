package com.ctrip.infosec.flowtable4j.dal;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * CheckEnitity转换为PO Entity
 * 放入工具类方法
 * Created by thyang on 2015-06-10.
 */
@Component
public class CheckRiskDAO {

    private static Logger logger = LoggerFactory.getLogger(CheckRiskDAO.class);

    @Autowired
    CardRiskService cardRiskDb;

    @Autowired
    FlowtableService flowDb;

    @Autowired
    CUSDbService cusDb;

    @Autowired
    ESBClient esbClient;

    /**
     * 获取InfoSecurity_AppInfo
     *
     * @param reqId long
     * @return
     */
    public Map getAppInfo(String reqId) {
        return getRecordByKey("InfoSecurity_AppInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取七天内均值消费金额
     *
     * @param CCardNoCode char
     * @param startTimeLimit char yyyy-MM-dd hh:mm:ss
     * @param timeLimit char yyyy-MM-dd hh:mm:ss
     * @return
     */
    public Double getAvgAmount7(String CCardNoCode, String startTimeLimit, String timeLimit) {
        try {
            String sql = "SELECT avg(Amount) AS AMT FROM CTRIP_FLT_CCardNoCode_Amount WITH(NOLOCK) " +
                    "WHERE CCardNoCode=? and CreateDate>=? and CreateDate<=?";
            Map avgAmount = flowDb.queryForMap(sql, new Object[]{CCardNoCode, startTimeLimit, timeLimit}, new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP});
            if (avgAmount != null) {
                return Double.parseDouble(avgAmount.get("AMT").toString());
            }
        } catch (Exception exp) {
            logger.warn("查询CTRIP_FLT_CCardNoCode_Amount信息异常",exp);
        }
        return 0d;
    }

    /**
     * 添加用户的用户等级信息
     *
     * @param uid
     */
    public String getCusCharacter(String uid, String vipFlag) {
        if (vipFlag.equals("T")) {
            return "VIP";
        }
        String cuscharacter = "";
        String contentType = "Customer.User.GetCustomerInfo";
        String contentBody = "<GetCustomerInfoRequest><UID>" + uid + "</UID></GetCustomerInfoRequest>";
        String xpath = "/Response/GetCustomerInfoResponse";
        String customerInfo = null;
        try {
            customerInfo = esbClient.requestESB(contentType, contentBody);
        } catch (Exception e) {
            //logger.warn("查询用户" + uid + "的Customer的信息异常" ,e);
        }
//        String FirstPkgOrderDate = customerInfo.get("FirstPkgOrderDate") == null ? "" : customerInfo.get("FirstPkgOrderDate").toString();
//        String FirstHotelOrderDate = customerInfo.get("FirstHotelOrderDate") == null ? "" : customerInfo.get("FirstHotelOrderDate").toString();
//        String FirstFlightOrderDate = customerInfo.get("FirstFlightOrderDate") == null ? "" : customerInfo.get("FirstFlightOrderDate").toString();
//        if(FirstPkgOrderDate.equals("0001-01-01T00:00:00") && FirstHotelOrderDate.equals("0001-01-01T00:00:00") && FirstFlightOrderDate.equals("0001-01-01T00:00:00"))
//        {
        cuscharacter = "NEW";
//        }else
//        {
//            cuscharacter = "REPEAT";
//        }
        return cuscharacter;
    }

    private Map getRecordByKey(String tableName, String keyFieldName, String keyValue, int[] argType) {
        try {
            return cardRiskDb.queryForMap(String.format("SELECT * FROM %s WITH(NOLOCK) WHERE %s = ?", tableName), new Object[]{keyValue}, argType);
        } catch (Exception exp) {
            logger.warn(String.format("查询%s信息异常:", tableName), exp);
        }
        return null;
    }

    private Map getTop1RecordByKey(String tableName, String keyFieldName, String keyValue, int[] argType) {
        try {
            return cardRiskDb.queryForMap(String.format("SELECT TOP 1 * FROM %s WITH(NOLOCK) WHERE %s =?", tableName), new Object[]{keyValue}, argType);
        } catch (Exception exp) {
            logger.warn(String.format("查询%s信息异常:", tableName), exp);
        }
        return null;
    }

    private List<Map<String, Object>> getListByKey(String tableName, String keyFieldName, String keyValue, int[] argType) {
        try {
            return cardRiskDb.queryForList(String.format("SELECT * FROM %s WITH(NOLOCK) WHERE %s = ?", tableName), new Object[]{keyValue}, argType);
        } catch (Exception exp) {
            logger.warn(String.format("查询%s信息异常:", tableName), exp);
        }
        return null;
    }

    /**
     * 获取机票订单信息 InfoSecurity_FlightsOrderInfo
     *
     * @param reqId
     * @return
     */
    public Map getFlightsOrderInfo(String reqId) {
        return getRecordByKey("InfoSecurity_FlightsOrderInfo", "ReqId", reqId, new int[]{Types.BIGINT});
    }

    /**
     * 获取InfoSecurity_PassengerInfo
     *
     * @param flightsOrderId
     * @return
     */
    public List<Map<String, Object>> getPassengerInfo(String flightsOrderId) {
        return getListByKey("InfoSecurity_PassengerInfo", "FlightsOrderID", flightsOrderId, new int[]{Types.BIGINT});
    }

    /**
     * 获取InfoSecurity_SegmentInfo
     *
     * @param flightsOrderId
     * @return
     */
    public List<Map<String, Object>> getSegmentInfo(String flightsOrderId) {
        return getListByKey("InfoSecurity_SegmentInfo", "FlightsOrderID", flightsOrderId, new int[]{Types.BIGINT});
    }

    /**
     * 根据CityId获取城市名称与省
     *
     * @param city
     * @return
     */
    public Map getCityNameProvince(String city) {
        return getTop1RecordByKey("BaseData_City", "City", city, new int[]{Types.BIGINT});
    }

    /**
     * 获取省份证归属省信息  BaseData_IDCardInfo
     * @param iDCardNumber
     * @return
     */
    public Map getIDCardProvince(String iDCardNumber) {
      return getTop1RecordByKey("BaseData_IDCardInfo","IDCardNumber",iDCardNumber,new int[]{Types.VARCHAR});
    }

    /**
     *
     * @param uid char
     * @param startTimeLimit char yyyy-MM-dd HH:mm:ss
     * @param timeLimit char yyyy-MM-dd HH:mm:ss
     * @return
     */
    public String getUidOrderDate(String uid, String startTimeLimit, String timeLimit) {
        try {
            String sql = "SELECT TOP 1 OrderDate FROM CTRIP_ALL_UID_OrderDate with (nolock) where " +
                    "Uid = ? and CreateDate>=? and CreateDate<=?";
            Map orderDate = flowDb.queryForMap(sql,new Object[]{uid, startTimeLimit, timeLimit},new int[]{Types.VARCHAR,Types.TIMESTAMP,Types.TIMESTAMP});
            if(orderDate!=null){
               return orderDate.get("OrderDate").toString();
            }
        } catch (Exception exp) {
            logger.warn("查询CTRIP_ALL_UID_OrderDate异常" ,exp);
        }
        return null;
    }

    /**
     * 获取InfoSecurity_CorporationInfo
     *
     * @param reqId long
     * @return
     */
    public Map getCorporationInfo(String reqId) {
        return getTop1RecordByKey("InfoSecurity_CorporationInfo","ReqId",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 通过手机号查询对应的城和市
     * CityName,ProvinceName
     *
     * @param mobilePhone char
     * @return 返回手机号对应的城市信息
     */
    public Map getMobileCityAndProv(String mobilePhone) {
        try {
            if (!Strings.isNullOrEmpty(mobilePhone) && mobilePhone.length()> 6) {
                mobilePhone = mobilePhone.substring(0, 7);
                return getTop1RecordByKey("BaseData_MobilePhoneInfo", "MobileNumber", mobilePhone, new int[]{Types.BIGINT});
            }
        } catch (Exception exp) {
            logger.warn("从sql查询手机号对应的城市信息异常", exp);
        }
        return null;
    }

    public String getValue(Map data, String key) {
        Object obj = data.get(key);
        return obj == null ? "" : String.valueOf(obj);
    }

    /**
     * 获取IP地址对应信息
     *
     * @param ipValue long
     * @return
     */
    public Map getIpCountryCity(long ipValue) {
        try {
            String sql = "SELECT TOP 1 *  FROM IpCountryCity WITH(NOLOCK) WHERE IpStart <= ? ORDER BY IpStart DESC ";
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
     * @param payId char
     * @return
     */
    public Map getDIDInfo(String orderId, String payId) {
        try {
            String sql = "SELECT TOP 1 *  FROM CacheData_DeviceIDInfo WITH(NOLOCK) WHERE Oid = ? AND Payid = ? ORDER BY RecordID desc";
            return flowDb.queryForMap(sql, new Object[]{orderId, payId}, new int[]{Types.VARCHAR, Types.VARCHAR});
        } catch (Exception exp) {
            logger.warn("查询DID信息异常", exp);
        }
        return null;
    }

    /**
     * 获取
     * 铁友除外     *
     * @param orderType int
     * @param orderId long
     * @return
     */
    public String getLastReqId(String orderId,String orderType,String merchantOrderID) {
        try {
            Map kv=null;
            if(orderType.equals("18")) {
                String sql = "SELECT TOP 1 ReqID FROM InfoSecurity_MainInfo WITH(NOLOCK) " +
                        "WHERE OrderId = ? and OrderType = ? ORDER BY  ReqID DESC ";
                kv = cardRiskDb.queryForMap(sql, new Object[]{orderId, orderType}, new int[]{Types.BIGINT, Types.INTEGER});
            }
            else
            {
                String sql = "SELECT TOP 1 ReqID  FROM InfoSecurity_MainInfo WITH(NOLOCK) " +
                        "WHERE  OrderType = ? and MerchantOrderID = ?  ORDER BY ReqID DESC";
                kv = cardRiskDb.queryForMap(sql, new Object[]{orderType, merchantOrderID}, new int[]{Types.INTEGER, Types.VARCHAR});
            }
            if(kv!=null){
                return getValue(kv,"ReqID");
            }
        } catch (Exception exp) {
            logger.warn("查询MainInfo信息异常", exp);
        }
        return null;
    }

    /**
     * 获取 CreditCardRule_ForeignCard
     * @param cardTypeId int
     * @param cardBin  char
     * @return
     */
    public Map getForeignCardInfo(String cardTypeId, String cardBin) {
        try {
            String sql = "SELECT *  FROM CreditCardRule_ForeignCard WITH(NOLOCK) WHERE CardTypeID = ? and CardRule = ?";
            return cardRiskDb.queryForMap(sql, new Object[]{cardTypeId, cardBin}, new int[]{Types.INTEGER, Types.VARCHAR});
        } catch (Exception exp) {
            logger.warn("getForeignCardInfo异常", exp);
        }
        return null;
    }

    /**
     * 获取 InfoSecurity_PaymentInfo
     *
     * @param lastReqID long
     * @return
     */
    public List<Map<String, Object>> getListPaymentInfo(String lastReqID) {
        return getListByKey("InfoSecurity_PaymentInfo","ReqID",lastReqID,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_CardInfo
     *
     * @param paymentInfoId long
     * @return
     */
    public List<Map<String, Object>> getCardInfoList(String paymentInfoId) {
        return getListByKey("InfoSecurity_CardInfo","PaymentInfoID",paymentInfoId,new int[]{Types.BIGINT});
    }

    /**
     * 获取InfoSecurity_PaymentMainInfo
     *
     * @param reqId long
     * @return
     */
    public Map getPaymentMainInfo(String reqId) {
        return getRecordByKey("InfoSecurity_PaymentMainInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_ContactInfo
     *
     * @param reqId long
     * @return
     */
    public Map getContactInfo(String reqId) {
        return  getRecordByKey("InfoSecurity_ContactInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取InfoSecurity_UserInfo
     *
     * @param reqId long
     * @return
     */
    public Map getUserInfo(String reqId) {
        return getRecordByKey("InfoSecurity_UserInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_IPInfo
     *
     * @param reqId long
     * @return
     */
    public Map getIpInfo(String reqId) {
        return getRecordByKey("InfoSecurity_IPInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取InfoSecurity_OtherInfo
     *
     * @param reqId long
     * @return
     */
    public Map getOtherInfo(String reqId) {
        return getRecordByKey("InfoSecurity_OtherInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取CardRisk_Leaked_Uid
     *
     * @param uid char
     * @return
     */
    public Map getLeakedInfo(String uid) {
        return getTop1RecordByKey("CardRisk_Leaked_Uid","Uid",uid, new int[]{Types.VARCHAR});
    }

    /**
     * 获取 InfoSecurity_HotelGroupInfo
     *
     * @param reqId long
     * @return
     */
    public List<Map<String, Object>> getHotelGroupInfo(String reqId) {
        return getListByKey("InfoSecurity_HotelGroupInfo", "ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_HotelInfo
     *
     * @param reqId long
     * @return
     */
    public Map getHotelInfo(String reqId) {
        return getRecordByKey("InfoSecurity_HotelInfo", "ReqID", reqId, new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_ExRailUserInfo
     * @param exRailId
     * @return
     */
    public Map getExRailUserInfo(String exRailId)
    {
        return  getTop1RecordByKey("InfoSecurity_ExRailUserInfo","ExRailInfoID",exRailId,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_VacationInfo
     * @param reqId
     * @return
     */
    public Map getVacationInfo(String reqId)
    {
        return  getTop1RecordByKey("InfoSecurity_VacationInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_VacationOptionInfo
     * @param vacationInfoID
     * @return
     */
    public List<Map<String,Object>> getVacationOptionInfoList(String vacationInfoID)
    {
        return  getListByKey("InfoSecurity_VacationOptionInfo","VacationInfoID",vacationInfoID,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_VacationUserInfo
     * @param vacationInfoID
     * @return
     */
    public List<Map<String,Object>> getVacationUserInfoList(String vacationInfoID)
    {
        return  getListByKey("InfoSecurity_VacationUserInfo","VacationInfoID",vacationInfoID,new int[]{Types.BIGINT});
    }

    /**
     * 获取 InfoSecurity_MiceInfo
     * @param reqId
     * @return
     */
    public Map<String,Object> getMiceInfo(String reqId)
    {
        return  getTop1RecordByKey("InfoSecurity_MiceInfo","ReqID",reqId,new int[]{Types.BIGINT});
    }


    /**
     * 获取 BaseData_CardBankInfo
     *
     * @param creditCardType int
     * @param branchNo str
     * @return
     */
    public Map getCardBankInfo(String creditCardType, String branchNo) {
        try {
            String sql = "SELECT TOP 1 *  FROM BaseData_CardBankInfo WITH(NOLOCK) WHERE CreditCardType =? and BranchNo = ?";
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
    public Map getCountryNameNationality(String country) {
       return  getTop1RecordByKey("BaseData_CountryInfo","Country",country,new int[]{Types.BIGINT});
    }

}
