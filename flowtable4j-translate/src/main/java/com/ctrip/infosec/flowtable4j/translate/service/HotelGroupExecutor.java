package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.dao.DataProxySources;
import com.ctrip.infosec.flowtable4j.translate.dao.ESBSources;
import com.ctrip.infosec.flowtable4j.translate.dao.HotelGroupSources;
import com.ctrip.infosec.flowtable4j.translate.dao.RedisSources;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroupExecutor
{
    @Autowired
    HotelGroupSources hotelGroupSources;

    @Autowired
    RedisSources redisSources;

    @Autowired
    ESBSources esbSources;

    @Autowired
    //DataProxySources dataProxySources;

    @Autowired
    HotelGroupOperation hotelGroupOperation;

    public void complementData(Map data)
    {
        data.put(HotelGroup.LastCheck,"T");
        data.put(HotelGroup.CorporationID,"");

        //根据uid取出crm信息
        String serviceName = "CRMService";
        String operationName = "getMemberInfo";
        String uid = data.get(HotelGroup.Uid) == null ? "" : data.get(HotelGroup.Uid).toString();
        Map params = ImmutableMap.of("uid", uid);
        Map crmInfo = DataProxySources.queryForMap(serviceName, operationName, params);

        int checkType = data.get(HotelGroup.CheckType) == null ? Integer.MIN_VALUE : Integer.parseInt(data.get(HotelGroup.CheckType).toString());
        switch (checkType)
        {
            case 0:
                hotelGroupOperation.fillMobilePhone(data);
                //这里获取用户的用户属性（NEW,REPEAT,VIP） 这里有两个方法：1，直接调用esb，2，调用郁伟新增加的DataProxy
                hotelGroupOperation.fillUserCusCharacter(data);
                hotelGroupOperation.fillIpInfo(data);
                //处理ip相关的信息

                break;
            case 1:

                break;
            case 2:

                break;
            default:
                break;
        }


    }

    public void convertToBlackCheckItem(Map data)
    {

    }

    public void convertToFlowRuleCheckItem(Map data)
    {

    }
}
