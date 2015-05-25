package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.sars.util.SpringContextHolder;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;

/**
 * Created by zhangsx on 2015/5/6.
 */
public class PayAdaptService {
    private static JdbcTemplate cardRiskJdbcTemplate;

    private static Logger logger = LoggerFactory.getLogger(Counter.class);

    static {
        cardRiskJdbcTemplate = SpringContextHolder.getBean("cardRiskDBTemplate");
    }

    public static boolean isPayAdapatFlowRuleOpen() {
//        String flag = cardRiskJdbcTemplate.queryForObject(" SELECT Active FROM [dbo].[CardRisk_AppFlag] (nolock) WHERE NameType='IsCheckPayAdapterFlowRule'", String.class);
//        if ("T".equals(flag)) {
//            return true;
//        }
//        return false;
        return true;
    }

    /**
     * 获取支付适配黑白名单校验实体
     *
     * @param orderType
     * @param orderId
     * @return
     */
    public static Map<String, Object> fillBWGCheckEntity(final int orderType, final long orderId) {
        return cardRiskJdbcTemplate.query(
                "SELECT top 1 v.ProductName,u.Uid,c.MobilePhone,c.ContactEmail,d.DID " +
                        " FROM InfoSecurity_MainInfo m WITH (nolock)  " +
                        " LEFT JOIN InfoSecurity_VacationInfo v (nolock) on m.ReqID=v.ReqID " +
                        " LEFT JOIN InfoSecurity_UserInfo u  (nolock) on m.ReqID=u.ReqID " +
                        " LEFT JOIN InfoSecurity_ContactInfo c  (nolock) on m.ReqID=c.ReqID " +
                        " LEFT JOIN InfoSecurity_DeviceIDInfo d  (nolock) on m.ReqID=d.ReqID " +
                        " WHERE  m.OrderID=? and m.OrderType=? " +
                        " ORDER BY m.ReqID desc",
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, orderId);
                        preparedStatement.setInt(2, orderType);
                    }
                }, new ResultSetExtractor<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        Map<String, Object> payAdaptEntity = new HashMap<String, Object>();
                        if (resultSet.next()) {
                            payAdaptEntity.put("ContactEmail", resultSet.getString("ContactEmail"));
                            payAdaptEntity.put("DID", resultSet.getString("DID"));
                            payAdaptEntity.put("MobilePhone", resultSet.getString("MobilePhone"));
                            //payAdaptEntity.setProductName(resultSet.getString("ProductName"));
                            payAdaptEntity.put("Uid", resultSet.getString("Uid"));
                        }
                        return payAdaptEntity;
                    }
                });
    }

    /**
     * 获取支付适配流量规则交易实体
     *
     * @param orderType
     * @param orderId
     * @return
     */
    public static Map<String, Object> fillPayAdaptCheckEntity(final int orderType, final long orderId) {
        String sql = getPayAdaptCommandTextByOrderType(orderType);
        if (sql.length() == 0) {
            return new HashMap<String, Object>();
        }
        return cardRiskJdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, orderId);
                preparedStatement.setInt(2, orderType);
            }
        }, new ResultSetExtractor<Map<String, Object>>() {
            @Override
            public Map<String, Object> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, Object> map = new HashMap<String, Object>();
                if (resultSet.next()) {
                    //First column index is 1
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        map.put(resultSet.getMetaData().getColumnName(i).toUpperCase(), Strings.nullToEmpty(resultSet.getString(i)));
                    }
                }
                return map;
            }
        });
    }

    public static void getIPInfo(int orderType, Map<String, Object> data2Check) {
        if (CtripOrderType.HotelGroup.getCode() == orderType || CtripOrderType.Flights.getCode() == orderType) {
            Map<String,String> ipCountryCity = null;
            for(String key:data2Check.keySet()){
                if ("USERIPVALUE".equalsIgnoreCase(key)) {
                    long ipAddrValue = Long.valueOf(Objects.toString(data2Check.get(key), ""));
                    ipCountryCity = getIpInfoFromUserProfileData(ipAddrValue);
                    break;
                }
            }
            if (ipCountryCity != null) {
                data2Check.put("PROVINCE", ipCountryCity.get("Province"));
                data2Check.put("IPCITY", ipCountryCity.get("City"));
            }
        }
    }

    public static void getOptions(int orderType, Map<String, Object> data2Check) {
        if (CtripOrderType.TTD.getCode() == orderType) {
            StringBuilder vacationOptionName = new StringBuilder("|");
            for(String key:data2Check.keySet()) {
                if ("VACATIONINFOID".equalsIgnoreCase(key)) {
                    int vacationInfoID = Integer.parseInt(data2Check.get(key).toString());
                    List<Map<String,String>> listVacationOptionInfo = getListByVacationInfoID(vacationInfoID);
                    if (listVacationOptionInfo != null) {
                        for (Map<String,String> pInfo :listVacationOptionInfo) {
                            //	度假子项名称
                            vacationOptionName.append(pInfo.get("OptionName") + "|");
                        }
                    }
                }
            }
            if (vacationOptionName.length()>1) {
                data2Check.put("VACATIONOPTIONNAME", vacationOptionName);
            }
        }
    }

    /**
     * 支付适配流量规则，报文信息
     *
     * @param orderType
     * @return
     */
    private static String getPayAdaptCommandTextByOrderType(int orderType) {
        if (CtripOrderType.CRH.getCode() == orderType) {
            return "SELECT TOP 1 m.ReqID,m.OrderType,u.VipGrade,c.MobilePhone,u.BindedMobilePhone,u.RelatedMobilephone," +
                    "c.MobilePhoneCity,e.Dcity,e.Acity,m.Serverfrom,o.OrderToSignUpDate,m.Amount,c.ContactEMail,u.RelatedEMail,u.CusCharacter " +
                    "FROM InfoSecurity_MainInfo m with (nolock) " +
                    "JOIN InfoSecurity_UserInfo u with (nolock) on m.ReqID=u.ReqID " +
                    "JOIN InfoSecurity_ContactInfo c with (nolock) on m.ReqID=c.ReqID " +
                    "JOIN InfoSecurity_ExRailInfo e with (nolock) on m.ReqID=e.ReqID " +
                    "JOIN InfoSecurity_OtherInfo o with (nolock) on m.ReqID=o.ReqID " +
                    "WHERE m.OrderID=?  and m.OrderType=? " +
                    "ORDER BY m.ReqID desc";
        } else if (CtripOrderType.HotelGroup.getCode() == orderType) {
            return "SELECT top 1 m.ReqID,m.OrderType,o.OrderToSignUpDate,m.Amount,m.Serverfrom,g.City,c.MobilePhoneCity," +
                    "i.UserIPValue,u.VipGrade,c.ContactEMail,c.MobilePhone,u.BindedMobilePhone,u.RelatedMobilephone,u.RelatedEMail,u.BindedEmail,u.CusCharacter " +
                    "FROM InfoSecurity_MainInfo m with (nolock) " +
                    "JOIN InfoSecurity_UserInfo u with (nolock) on m.ReqID=u.ReqID " +
                    "JOIN InfoSecurity_ContactInfo c with (nolock) on m.ReqID=c.ReqID " +
                    "JOIN InfoSecurity_OtherInfo o with (nolock) on m.ReqID=o.ReqID " +
                    "JOIN InfoSecurity_HotelGroupInfo g with (nolock) on m.ReqID=g.ReqID " +
                    "JOIN InfoSecurity_IPInfo i with (nolock) on m.ReqID=i.ReqID " +
                    "WHERE  m.OrderID=? and  m.OrderType=? " +
                    "ORDER BY m.ReqID desc";
        } else if (CtripOrderType.Flights.getCode() == orderType) {
            return "SELECT top 1 m.ReqID,m.OrderType,m.Serverfrom,u.VipGrade,c.SendTickerAddr,o.TakeOffToOrderDate,f.FlightClass,m.Amount,o.OrderToSignUpDate,i.UserIPValue," +
                    "(SELECT top 1 CityName from BaseData_City with (nolock) where City=f.Dcity) as Dcity," +
                    "(SELECT top 1 ProvinceName from BaseData_City with (nolock) where City=f.Dcity) as DCityProvince," +
                    "(SELECT top 1 CityName from BaseData_City  with (nolock) where City=f.Acity) as Acity," +
                    "(SELECT top 1 ProvinceName from BaseData_City with (nolock) where City=f.Acity) as ACityProvince," +
                    "c.ContactEMail,u.BindedMobilePhone,c.MobilePhone,i.Continent,u.CusCharacter,u.BindedEmail," +
                    "c.MobilePhoneCity,u.RelatedMobilephone,u.RelatedEMail,c.MobilePhoneProvince " +
                    "FROM InfoSecurity_MainInfo m with (nolock) " +
                    "JOIN InfoSecurity_UserInfo u with (nolock) on m.ReqID=u.ReqID " +
                    "JOIN InfoSecurity_ContactInfo c with (nolock) on m.ReqID=c.ReqID " +
                    "JOIN InfoSecurity_OtherInfo o with (nolock) on m.ReqID=o.ReqID " +
                    "JOIN InfoSecurity_IPInfo i with (nolock) on m.ReqID=i.ReqID " +
                    "JOIN InfoSecurity_FlightsOrderInfo f with (nolock) on m.ReqID=f.ReqID " +
                    "WHERE m.OrderID=? and  m.OrderType=? " +
                    "ORDER BY m.ReqID desc";
        } else if (CtripOrderType.TTD.getCode() == orderType) {
            return "SELECT top 1 m.ReqID,m.OrderType,m.Serverfrom,u.VipGrade,v.ProductName,c.MobilePhone,u.RelatedMobilephone,u.BindedMobilePhone,c.MobilePhoneCity," +
                    "(SELECT TOP 1 CityName FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = substring(u.BindedMobilePhone,0,8)) as BindedMobilePhoneCity," +
                    "(SELECT TOP 1 CityName FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = substring(u.RelatedMobilephone,0,8)) as RelatedMobilephoneCity," +
                    "m.Amount,o.OrderToSignUpDate,u.CusCharacter,v.VacationInfoID " +
                    "FROM InfoSecurity_MainInfo m with (nolock) " +
                    "JOIN InfoSecurity_UserInfo u with (nolock) on m.ReqID=u.ReqID " +
                    "JOIN InfoSecurity_ContactInfo c with (nolock) on m.ReqID=c.ReqID " +
                    "JOIN InfoSecurity_OtherInfo o with (nolock)  on m.ReqID=o.ReqID " +
                    "JOIN InfoSecurity_VacationInfo v with (nolock) on m.ReqID=v.ReqID " +
                    "WHERE m.OrderID=? and m.OrderType=? " +
                    "ORDER BY m.ReqID desc";
        }
        return "";
    }

    private static Map<String, String> getIpInfoFromUserProfileData(final long ipAddrValue) {
         if (ipAddrValue > 0) {
            cardRiskJdbcTemplate.query(
                            "SELECT TOP 1 Province, City " + //",Type_Company, Country,CountryId, ProvinceId, CityId, latitude, longitude, area, NationCode " +
                            "FROM BaseData_IPInfo with (nolock)  " +
                            "WHERE  StartAddr  <= ? order by StartAddr DESC", new PreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStatement) throws SQLException {
                            preparedStatement.setLong(1, ipAddrValue);
                        }
                    }, new ResultSetExtractor<Map<String, String>>() {
                        @Override
                        public Map<String, String> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                            Map<String, String> result = new HashMap<String, String>();
                            if (resultSet.next()) {
//                                result.put("Area", resultSet.getString("area"));
                                  result.put("City", resultSet.getString("City"));
//                                result.put("CityId", resultSet.getString("CityId"));
//                                result.put("Country", resultSet.getString("Country"));
//                                result.put("CountryId", resultSet.getString("CountryId"));
//                                result.put("Latitude", resultSet.getString("latitude"));
//                                result.put("Longitude", resultSet.getString("longitude"));
//                                result.put("NationCode", resultSet.getString("NationCode"));
                                  result.put("Province", resultSet.getString("Province"));
//                                result.put("ProvinceId", resultSet.getString("ProvinceId"));
//                                result.put("Type_Company", resultSet.getString("Type_Company"));
                            }
                            return result;
                        }
                    });
        }
        return null;
    }

    private static List<Map<String,String>> getListByVacationInfoID(final int vacationInfoID) {
        String sql = "SELECT  OptionName" +
                    //",VacationOption, VacationInfoID, OptionID, OptionQty, DataChange_LastTime, FunSubOrderType, SupplierID, SupplierName" +
                    " FROM dbo.InfoSecurity_VacationOptionInfo WITH (nolock) WHERE VacationInfoID = ?";
        return cardRiskJdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, vacationInfoID);
            }
        }, new ResultSetExtractor<List<Map<String,String>>>() {
            @Override
            public List<Map<String,String>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                List<Map<String,String>> result = new ArrayList<Map<String,String>>();
                while (resultSet.next()) {
                    Map<String,String> item = new HashMap<String, String>();
//                    item.put("FunSubOrderType", resultSet.getString("FunSubOrderType"));
//                    item.put("OptionID", resultSet.getString("OptionID"));
                      item.put("OptionName", resultSet.getString("OptionName"));
//                    item.put("OptionQty", resultSet.getString("OptionQty"));
//                    item.put("SupplierID", resultSet.getString("SupplierID"));
//                    item.put("SupplierName", resultSet.getString("SupplierName"));
//                    item.put("VacationInfoID", resultSet.getString("VacationInfoID"));
//                    item.put("VacationOption", resultSet.getString("VacationOption"));
                    result.add(item);
                }
                return result;
            }
        });
    }
}
