package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.BaseData_IPInfo;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.InfoSecurity_VacationOptionInfo;
import com.ctrip.infosec.flowtable4j.model.MainInfoOfPayadapt;
import com.ctrip.infosec.sars.util.SpringContextHolder;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zhangsx on 2015/5/6.
 */
public class PayAdaptService {
    private static JdbcTemplate cardRiskJdbcTemplate;

    private static Logger logger = LoggerFactory.getLogger(Counter.class);

    static {
        cardRiskJdbcTemplate = SpringContextHolder.getBean("cardRiskDBTemplate");
    }

    public static boolean shouldBeChecked() {
        String flag = cardRiskJdbcTemplate.queryForObject(" SELECT Active FROM [dbo].[CardRisk_AppFlag] (nolock) WHERE NameType='IsCheckPayAdapterFlowRule'", String.class);
        if ("T".equals(flag)) {
            return true;
        }
        return false;
    }

    public static MainInfoOfPayadapt getMainInfoByTypeAndId(final int orderType, final long orderId) {
        return cardRiskJdbcTemplate.query("select top 1 v.ProductName,u.Uid,c.MobilePhone,c.ContactEmail,d.DID from InfoSecurity_MainInfo m with (nolock) \n" +
                "                                     left join InfoSecurity_VacationInfo v (nolock) on m.ReqID=v.ReqID\n" +
                "                                     left join InfoSecurity_UserInfo u  (nolock) on m.ReqID=u.ReqID\n" +
                "                                     left join InfoSecurity_ContactInfo c  (nolock) on m.ReqID=c.ReqID\n" +
                "                                     left join InfoSecurity_DeviceIDInfo d  (nolock) on m.ReqID=d.ReqID\n" +
                "                                    where m.OrderType=? and m.OrderID=? order by m.ReqID desc", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, orderType);
                preparedStatement.setLong(2, orderId);
            }
        }, new ResultSetExtractor<MainInfoOfPayadapt>() {
            @Override
            public MainInfoOfPayadapt extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                MainInfoOfPayadapt mainInfoOfPayadapt = new MainInfoOfPayadapt();
                if (resultSet.next()) {
                    mainInfoOfPayadapt.setContactEmail(resultSet.getString("ContactEmail"));
                    mainInfoOfPayadapt.setDid(resultSet.getString("DID"));
                    mainInfoOfPayadapt.setMobilePhone(resultSet.getString("MobilePhone"));
                    mainInfoOfPayadapt.setProductName(resultSet.getString("ProductName"));
                    mainInfoOfPayadapt.setUid(resultSet.getString("Uid"));
                }
                return mainInfoOfPayadapt;
            }
        });
    }

    public static Map<String, Object> getAdapterData(final int orderType, final long orderId) {
        String sql = getSqlByOrderType(orderType);
        if (sql.length() == 0) {
            return new HashMap<String, Object>();
        }
        return cardRiskJdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1,orderType);
                preparedStatement.setLong(2,orderId);
            }
        }, new ResultSetExtractor<Map<String, Object>>() {
            @Override
            public Map<String, Object> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, Object> map = new HashMap<String, Object>();
                if (resultSet.next()) {
                    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                        map.put(resultSet.getMetaData().getColumnName(i+1), resultSet.getObject(i+1));
                    }
                }
                return map;
            }
        });
    }

    public static void addIpInfo(int orderType, Map<String, Object> data2Check) {
        if (CtripOrderType.HotelGroup.getCode() == orderType || CtripOrderType.Flights.getCode() == orderType) {
            BaseData_IPInfo ipCountryCity = null;
            for(Iterator<String> it = data2Check.keySet().iterator();it.hasNext();) {
                String key = it.next();
                if("USERIPVALUE".equalsIgnoreCase(key)){
                    long ipAddrValue = Long.valueOf(Objects.toString(data2Check.get(key), ""));
                    String ipAddress = ipConvertToStr(ipAddrValue);
                    ipCountryCity = getIpInfoFromUserProfileData(ipAddress);
                }
            }
            if (ipCountryCity != null)
            {
                data2Check.put("Province",ipCountryCity.getProvince());
                data2Check.put("IPCity",ipCountryCity.getCity());
            }
        }
    }

    public static void addOptionName(int orderType, Map<String, Object> data2Check){
        if(CtripOrderType.TTD.getCode()==orderType){
            StringBuilder vacationOptionName = null;
            for(Iterator<String> it = data2Check.keySet().iterator();it.hasNext();){
                String key = it.next();
                if("VACATIONINFOID".equalsIgnoreCase(key)){
                    int vacationInfoID = Integer.parseInt(data2Check.get(key).toString());
                    List<InfoSecurity_VacationOptionInfo> listVacationOptionInfo = getListByVacationInfoID(vacationInfoID);
                    if (listVacationOptionInfo != null)
                    {
                        vacationOptionName = new StringBuilder();
                        for (InfoSecurity_VacationOptionInfo pInfo : listVacationOptionInfo)
                        {
                            //	度假子项名称
                            vacationOptionName.append(pInfo.getOptionName() + "|");
                        }
                    }
                }
            }
            if(vacationOptionName!=null){
                data2Check.put("VacationoptionName", "|" + vacationOptionName);
            }
        }
    }

    private static String getSqlByOrderType(int orderType) {
        if (CtripOrderType.CRH.getCode() == orderType) {
            return "select top 1 m.ReqID,m.OrderType,u.VipGrade,c.MobilePhone,u.BindedMobilePhone,u.RelatedMobilephone,c.MobilePhoneCity,e.Dcity,e.Acity,m.Serverfrom,o.OrderToSignUpDate,m.Amount,c.ContactEMail,u.RelatedEMail,u.CusCharacter \n" +
                    "\n" +
                    "from InfoSecurity_MainInfo m  with (nolock) \n" +
                    "join InfoSecurity_UserInfo u  with (nolock) \n" +
                    "on m.ReqID=u.ReqID\n" +
                    "join InfoSecurity_ContactInfo c  with (nolock) \n" +
                    "on m.ReqID=c.ReqID\n" +
                    "join InfoSecurity_ExRailInfo e  with (nolock) \n" +
                    "on m.ReqID=e.ReqID\n" +
                    "join InfoSecurity_OtherInfo o  with (nolock) \n" +
                    "on m.ReqID=o.ReqID\n" +
                    "\n" +
                    "where m.OrderType=? and m.OrderID=? \n" +
                    "order by m.ReqID desc";
        } else if (CtripOrderType.HotelGroup.getCode() == orderType) {
            return "select top 1 m.ReqID,m.OrderType,o.OrderToSignUpDate,m.Amount,m.Serverfrom,g.City,c.MobilePhoneCity,i.UserIPValue,u.VipGrade,c.ContactEMail,c.MobilePhone,u.BindedMobilePhone,u.RelatedMobilephone,u.RelatedEMail,u.BindedEmail,u.CusCharacter \n" +
                    "\n" +
                    "from InfoSecurity_MainInfo m with (nolock) \n" +
                    "join InfoSecurity_UserInfo u with (nolock) \n" +
                    "on m.ReqID=u.ReqID\n" +
                    "join InfoSecurity_ContactInfo c with (nolock) \n" +
                    "on m.ReqID=c.ReqID\n" +
                    "join InfoSecurity_OtherInfo o with (nolock) \n" +
                    "on m.ReqID=o.ReqID\n" +
                    "join InfoSecurity_HotelGroupInfo g with (nolock) \n" +
                    "on m.ReqID=g.ReqID\n" +
                    "join InfoSecurity_IPInfo i with (nolock) \n" +
                    "on m.ReqID=i.ReqID\n" +
                    "\n" +
                    "where m.OrderType=? and m.OrderID=? \n" +
                    "order by m.ReqID desc";
        } else if (CtripOrderType.Flights.getCode() == orderType) {
            return "select top 1 m.ReqID,m.OrderType,m.Serverfrom,u.VipGrade,c.SendTickerAddr,o.TakeOffToOrderDate,f.FlightClass,m.Amount,o.OrderToSignUpDate,i.UserIPValue\n" +
                    ",(select top 1 CityName from BaseData_City with (nolock) where City=f.Dcity) as Dcity\n" +
                    ",(select top 1 ProvinceName from BaseData_City with (nolock) where City=f.Dcity) as DCityProvince\n" +
                    ",(select top 1 CityName from BaseData_City  with (nolock) where City=f.Acity) as Acity\n" +
                    ",(select top 1 ProvinceName from BaseData_City with (nolock) where City=f.Acity) as ACityProvince\n" +
                    ",c.ContactEMail,u.BindedMobilePhone,c.MobilePhone,i.Continent,u.CusCharacter,u.BindedEmail\n" +
                    ",c.MobilePhoneCity,u.RelatedMobilephone,u.RelatedEMail,c.MobilePhoneProvince\n" +
                    "\n" +
                    "from InfoSecurity_MainInfo m with (nolock) \n" +
                    "join InfoSecurity_UserInfo u with (nolock) \n" +
                    "on m.ReqID=u.ReqID\n" +
                    "join InfoSecurity_ContactInfo c with (nolock) \n" +
                    "on m.ReqID=c.ReqID\n" +
                    "join InfoSecurity_OtherInfo o with (nolock) \n" +
                    "on m.ReqID=o.ReqID\n" +
                    "join InfoSecurity_IPInfo i with (nolock) \n" +
                    "on m.ReqID=i.ReqID\n" +
                    "join InfoSecurity_FlightsOrderInfo f with (nolock) \n" +
                    "on m.ReqID=f.ReqID\n" +
                    "\n" +
                    "where m.OrderType=? and m.OrderID=? \n" +
                    "order by m.ReqID desc";
        } else if (CtripOrderType.TTD.getCode() == orderType) {
            return "select top 1 m.ReqID,m.OrderType,\n" +
                    "m.Serverfrom,u.VipGrade,v.ProductName,\n" +
                    "c.MobilePhone,u.RelatedMobilephone,u.BindedMobilePhone,c.MobilePhoneCity\n" +
                    ",(SELECT TOP 1 CityName FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = substring(u.BindedMobilePhone,0,8)) as BindedMobilePhoneCity\n" +
                    ",(SELECT TOP 1 CityName FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = substring(u.RelatedMobilephone,0,8)) as RelatedMobilephoneCity\n" +
                    ",m.Amount,o.OrderToSignUpDate,u.CusCharacter,v.VacationInfoID   \n" +
                    "from InfoSecurity_MainInfo m with (nolock) \n" +
                    "join InfoSecurity_UserInfo u with (nolock) \n" +
                    "on m.ReqID=u.ReqID\n" +
                    "join InfoSecurity_ContactInfo c with (nolock) \n" +
                    "on m.ReqID=c.ReqID\n" +
                    "join InfoSecurity_OtherInfo o with (nolock) \n" +
                    "on m.ReqID=o.ReqID\n" +
                    "join InfoSecurity_VacationInfo v with (nolock) \n" +
                    "on m.ReqID=v.ReqID\n" +
                    "\n" +
                    "where m.OrderType=? and m.OrderID=? \n" +
                    "order by m.ReqID desc";
        } else {
            return "";
        }
    }

    private static String ipConvertToStr(long Ip) {
        int a = (int) (Ip / 16777216);
        int b = (int) ((Ip % 16777216) / 65536);
        int c = (int) (((Ip % 16777216) % 65536) / 256);
        int d = (int) (((Ip % 16777216) % 65536) % 256);

        return a + "." + b + "." + c + "." + d;
    }

    private static BaseData_IPInfo getIpInfoFromUserProfileData(String userIP) {
        userIP = userIP.replace("\"", "");
        int index = userIP.indexOf(":");
        if (index > 0) {
            userIP = userIP.substring(0, index);
        }
        BaseData_IPInfo ipInfo = new BaseData_IPInfo();
        if (isIPAddress(userIP)) {
            final long ipAddrValue = ipConvertTo10(userIP);
//            ipInfo = new BaseData_IPInfoDAL().GetByIpAddr(ipAddrValue);
//SELECT TOP 1 " + SelectFieldList + @"  FROM " + TableName + " with (nolock)  WHERE [BaseData_IPInfo].[StartAddr] <= @IpAddr order by StartAddr DESC
            cardRiskJdbcTemplate.query("SELECT TOP 1 StartAddr, EndAddr, Type_Company, Country, Province, City, CountryId, ProvinceId, CityId, latitude, longitude, area, remark, DataChange_LastTime, NationCode" +
                    "  FROM BaseData_IPInfo with (nolock)  WHERE [BaseData_IPInfo].[StartAddr] <= ? order by StartAddr DESC", new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                    preparedStatement.setLong(1,ipAddrValue);
                }
            }, new ResultSetExtractor<BaseData_IPInfo>() {
                @Override
                public BaseData_IPInfo extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    BaseData_IPInfo result = new BaseData_IPInfo();
                    if(resultSet.next()){
                        result.setArea(resultSet.getString("area"));
                        result.setCity(resultSet.getString("City"));
                        result.setCityId(resultSet.getInt("CityId"));
//                        result.setContinentID(1);
                        result.setCountry(resultSet.getString("Country"));
                        result.setCountryId(resultSet.getInt("CountryId"));
                        result.setDataChange_LastTime(resultSet.getTimestamp("DataChange_LastTime"));
                        result.setEndAddr(resultSet.getLong("EndAddr"));
                        result.setLatitude(resultSet.getString("latitude"));
                        result.setLongitude(resultSet.getString("longitude"));
                        result.setNationCode(resultSet.getString("NationCode"));
                        result.setProvince(resultSet.getString("Province"));
                        result.setProvinceId(resultSet.getInt("ProvinceId"));
                        result.setType_Company(resultSet.getString("Type_Company"));
                        result.setRemark(resultSet.getString("remark"));
                        result.setStartAddr(resultSet.getLong("StartAddr"));
                    }
                    return result;
                }
            });
//            ipInfo = cardRiskJdbcTemplate.queryForObject("SELECT TOP 1 StartAddr, EndAddr, Type_Company, Country, Province, City, CountryId, ProvinceId, CityId, latitude, longitude, area, remark, DataChange_LastTime, NationCode "+
//                                        " FROM BaseData_IPInfo with (nolock)  WHERE [BaseData_IPInfo].[StartAddr] <= ? order by StartAddr DESC",BaseData_IPInfo.class);
        }

        ipInfo.setCountryId(0);
        return ipInfo;
    }

    private static boolean isIPAddress(String ip) {
        if (Strings.isNullOrEmpty(ip) || ip.length() < 7 || ip.length() > 15) {
            return false;
        }
        String regformat = "^(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5])$";
        Pattern pattern = Pattern.compile(regformat);
        return pattern.matcher(ip).matches();
    }

    private static long ipConvertTo10(String ip) {
        long n_Ip = 0;

        if (ip != null && ip != "") {
            String[] arr;
            arr = ip.replace(".",",").split(",");
            if (arr.length == 4) {
                long a = Long.parseLong(arr[0].toString());
                long b = Long.parseLong(arr[1].toString());
                long c = Long.parseLong(arr[2].toString());
                long d = Long.parseLong(arr[3].toString());

                n_Ip = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
            }
        }
        return n_Ip;
    }

    private static List<InfoSecurity_VacationOptionInfo> getListByVacationInfoID(final int vacationInfoID){
        String sql = "SELECT VacationOption, VacationInfoID, OptionID, OptionName, OptionQty, DataChange_LastTime, FunSubOrderType, SupplierID, SupplierName" +
                " FROM dbo.InfoSecurity_VacationOptionInfo with (nolock) WHERE [InfoSecurity_VacationOptionInfo].[VacationInfoID] = ?";
        return cardRiskJdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1,vacationInfoID);
            }
        }, new ResultSetExtractor<List<InfoSecurity_VacationOptionInfo>>() {
            @Override
            public List<InfoSecurity_VacationOptionInfo> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                List<InfoSecurity_VacationOptionInfo> result = new ArrayList<InfoSecurity_VacationOptionInfo>();
                while(resultSet.next()){
                    InfoSecurity_VacationOptionInfo item = new InfoSecurity_VacationOptionInfo();

                    item.setFunSubOrderType(resultSet.getString("FunSubOrderType"));
                    item.setOptionID(resultSet.getInt("OptionID"));
                    item.setOptionName(resultSet.getString("OptionName"));
                    item.setOptionQty(resultSet.getInt("OptionQty"));
                    item.setSupplierID(resultSet.getString("SupplierID"));
                    item.setSupplierName(resultSet.getString("SupplierName"));
                    item.setVacationInfoID(resultSet.getInt("VacationInfoID"));
                    item.setVacationOption(resultSet.getInt("VacationOption"));

                    result.add(item);
                }
                return result;
            }
        });
    }
}
