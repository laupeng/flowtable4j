package com.ctrip.infosec.flowtable4j.translate.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroupSources
{
    @Resource(name="CardRiskDB")
    private JdbcTemplate cardRiskDBTemplate;

    @Resource(name = "RiskCtrlPreProcDB")
    private JdbcTemplate riskCtrlPreProcDBTemplate;

    /**
     * 这个方法可以作为公共的方法 //Todo 下次把这个方法放到公共查询里面 或者 把从数据库查询改成从郁伟的DataProxy查询！
     * 通过手机号查询对应的城和市
     * @param mobilePhone 手机号
     * @return
     */
    public Map getCityAndProv(String mobilePhone)
    {
        Map mobileInfo = null;
        try
        {
            String subMobileNum = mobilePhone.substring(0,7);
            String sqlCommand = "SELECT Top 1 *" + " FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = "+subMobileNum;
            mobileInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        }catch(Exception exp)
        {
            //log for warn
        }
        return mobileInfo;//这里取出里面的 CityName 和 ProvinceName 这两个字段
    }

    //获取当前IP所在地信息(Top 1 OrderBy IpStart Desc)  IpCountryCity
    public Map getIpCountryCity(long ipValue)
    {
        Map ipInfo = null;
        try{
            String sqlCommand = "SELECT Top 1 * FROM IpCountryCity with (nolock) WHERE IpStart <= "+ipValue +" ORDER BY IpStart DESC ";//FIXME 这里问徐洪修正
            ipInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        }catch(Exception exp)
        {
            //log for warn
        }
        return ipInfo;
    }
}
