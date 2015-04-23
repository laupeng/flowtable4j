package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.common.IpConvert;
import com.ctrip.infosec.flowtable4j.translate.dao.DataProxySources;
import com.ctrip.infosec.flowtable4j.translate.dao.ESBSources;
import com.ctrip.infosec.flowtable4j.translate.dao.HotelGroupSources;
import com.ctrip.infosec.flowtable4j.translate.dao.RedisSources;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroupOperation
{

    @Autowired
    HotelGroupSources hotelGroupSources;

    @Autowired
    RedisSources redisSources;

    @Autowired
    ESBSources esbSources;

    @Autowired
    DataProxySources dataProxySources;

    /**
     * 添加手机对应的省市信息
     */
    public void fillMobilePhone(Map data)
    {
        String mobilePhone = data.get(HotelGroup.MobilePhone) == null ? "" : data.get(HotelGroup.MobilePhone).toString();
        if(mobilePhone == null || mobilePhone.length() <= 6)
            return;

        Map mobileInfo = hotelGroupSources.getCityAndProv(mobilePhone);
    }

    /**
     * 添加用户的用户等级信息
     * @param data
     */
    public void fillUserCusCharacter(Map data)
    {
        String uid = data.get(HotelGroup.Uid) == null ? "" : data.get(HotelGroup.Uid).toString();
        String serviceName = "UserProfileService";
        String operationName = "DataQuery";
        List tagContents = new ArrayList();
        tagContents.add("RECENT_IP");
        tagContents.add("RECENT_IPAREA");
        Map params = new HashMap();
        params.put("uid",uid);
        params.put("tagNames",tagContents);

        Map uidInfo = dataProxySources.queryForMap(serviceName, operationName, params);
        String CusCharacter = uidInfo.get("CusCharacter") == null ? "" : uidInfo.get("CusCharacter").toString();
        data.put(HotelGroup.CusCharacter,CusCharacter);
    }

    public void fillIpInfo(Map data)
    {
        String userIp = data.get(HotelGroup.UserIP) == null ? "" : data.get(HotelGroup.UserIP).toString();
        data.put(HotelGroup.UserIPAdd,userIp);
        Long userIPValue = IpConvert.ipConvertTo10(userIp);
        data.put(HotelGroup.UserIPValue,userIPValue);
        //
        Map ipInfo = hotelGroupSources.getIpCountryCity(userIPValue);
        if(ipInfo != null && ipInfo.size()>0)
        {
            String ContinentID = ipInfo.get("ContinentID") == null ? "" : ipInfo.get("ContinentID").toString();
            String CityId = ipInfo.get("CityId") == null ? "" : ipInfo.get("CityId").toString();
            String NationCode = ipInfo.get("NationCode") == null ? "" : ipInfo.get("NationCode").toString();
            data.put(HotelGroup.Continent,ContinentID);
            data.put(HotelGroup.IPCity,CityId);
            data.put(HotelGroup.IPCountry,NationCode);
        }
    }
}
